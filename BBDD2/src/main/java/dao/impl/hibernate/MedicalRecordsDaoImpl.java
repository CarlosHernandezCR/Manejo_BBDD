package dao.impl.hibernate;

import common.constants.MedicalRecordConstants;
import dao.JPAUtil;
import dao.MedicalRecordsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import model.error.HospitalError;
import model.hibernate.MedicalRecord;
import model.hibernate.Prescribe;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordsDaoImpl implements MedicalRecordsDao {
    private JPAUtil jpaUtil;
    private EntityManager em;

    @Inject
    public MedicalRecordsDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll() {
        Either<HospitalError, List<MedicalRecord>> result;
        em = jpaUtil.getEntityManager();
        try {
            List<MedicalRecord> medicalRecords = em.createNamedQuery("HQL_GET_ALL_MEDICALRECORDS", MedicalRecord.class).getResultList();
            if (medicalRecords.isEmpty())
                result = Either.left(new HospitalError(0, MedicalRecordConstants.IS_EMPTY));
            else
                result = Either.right(medicalRecords);
        } catch (PersistenceException ex) {
            result = Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(LocalDate date, int before) {
        Either<HospitalError, List<MedicalRecord>> result;
        em = jpaUtil.getEntityManager();
        try {
            TypedQuery<MedicalRecord> query;
            if (before == -1)
                query = em.createQuery("SELECT m FROM MedicalRecord m WHERE m.admissionDate < :date", MedicalRecord.class);
            else if (before == 1)
                query = em.createQuery("SELECT m FROM MedicalRecord m WHERE m.admissionDate > :date", MedicalRecord.class);
            else
                query = em.createQuery("SELECT m FROM MedicalRecord m WHERE m.admissionDate = :date", MedicalRecord.class);
            query.setParameter("date", date);
            List<MedicalRecord> medicalRecords = query.getResultList();
            if (medicalRecords.isEmpty()) {
                result = Either.left(new HospitalError(1, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND));
            } else {
                result = Either.right(medicalRecords);
            }
        } catch (PersistenceException ex) {
            result = Either.left(new HospitalError(0, MedicalRecordConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Either<HospitalError, MedicalRecord> get(int idMedicalRecords) {
        Either<HospitalError, MedicalRecord> result;
        em = jpaUtil.getEntityManager();
        try {
            MedicalRecord medicalRecord = em.find(MedicalRecord.class, idMedicalRecords);
            if (medicalRecord != null) {
                result = Either.right(medicalRecord);
            } else {
                result = Either.left(new HospitalError(1, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND));
            }
        } catch (PersistenceException ex) {
            result = Either.left(new HospitalError(0, MedicalRecordConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(MedicalRecord mr) {
        em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(mr);
            tx.commit();
            int id = mr.getIdMedicalRecords();
            for (Prescribe prescribe : mr.getPrescribes()) {
                prescribe.setIdMedicalRecords(id);
                tx.begin();
                em.persist(prescribe);
                tx.commit();
            }
            return Either.right(mr.getPrescribes().size() + 1);
        } catch (PersistenceException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            return Either.left(new HospitalError(0, MedicalRecordConstants.ERROR_WRITING_FILE));
        } finally {
            em.close();
        }
    }

    @Override
    public Either<HospitalError, Integer> delete(MedicalRecord o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(List<MedicalRecord> medicalRecords, boolean confirm) {
        if (confirm) {
            em = jpaUtil.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                for (MedicalRecord medicalRecord : medicalRecords) {
                    deletePrescribe(medicalRecord.getIdMedicalRecords(), em, tx);
                    tx.begin();
                    MedicalRecord managedMedicalRecord = em.merge(medicalRecord);
                    em.remove(managedMedicalRecord);
                    tx.commit();
                }
                return Either.right(1);
            } catch (PersistenceException ex) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                return Either.left(new HospitalError(0, MedicalRecordConstants.ERROR_WRITING_FILE));
            } finally {
                em.close();
            }
        } else {
            em = jpaUtil.getEntityManager();
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                for (MedicalRecord medicalRecord : medicalRecords) {
                    em.remove(medicalRecord);
                }
                tx.commit();
                return Either.right(1);
            } catch (PersistenceException | IllegalArgumentException ex) {
                if (tx.isActive()) {
                    tx.rollback();
                }
                if (ex instanceof IllegalArgumentException) {
                    return Either.left(new HospitalError(33, MedicalRecordConstants.MEDICALRECORD_WITH_PRESCRIBES));
                } else {
                    return Either.left(new HospitalError(0, MedicalRecordConstants.ERROR_DELETING_MEDICALRECORDS));
                }
            } finally {
                em.close();
            }
        }
    }

    private void deletePrescribe(int idMedicalRecords, EntityManager em, EntityTransaction tx) {
        try {
            TypedQuery<Prescribe> query = em.createQuery("SELECT p FROM Prescribe p WHERE p.idMedicalRecords = :id", Prescribe.class);
            query.setParameter("id", idMedicalRecords);
            List<Prescribe> prescribes = query.getResultList();
            for (Prescribe prescribe : prescribes) {
                tx.begin();
                em.remove(prescribe);
                tx.commit();
            }
        } catch (PersistenceException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenceException();
        }
    }

    @Override
    public Either<HospitalError, Integer> update(MedicalRecord o) {
        return null;
    }

}
