package dao.impl.hibernate;

import common.constants.MedicalRecordConstants;
import common.constants.PrescribeConstants;
import dao.JPAUtil;
import dao.PrescribeDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import model.error.HospitalError;
import model.hibernate.Prescribe;

import java.util.List;

public class PrescribeDaoImpl implements PrescribeDao {
    private final JPAUtil jpaUtil;
    private EntityManager em;

    @Inject
    public PrescribeDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<Prescribe>> getAll() {
        Either<HospitalError, List<Prescribe>> result;
        em = jpaUtil.getEntityManager();
        try {
            List<Prescribe> appointments = em.createNamedQuery("HQL_GET_ALL_PRESCRIBE", Prescribe.class).getResultList();
            if (appointments != null) {
                result = Either.right(appointments);
            } else {
                result = Either.left(new HospitalError(0, PrescribeConstants.PRESCRIBE_NOT_FOUND));
            }
        } catch (Exception ex) {
            result = Either.left(new HospitalError(0, PrescribeConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, List<Prescribe>> getAll(int idMedicalRecord) {
        try {
            em = jpaUtil.getEntityManager();
            TypedQuery<Prescribe> query = em.createQuery(
                    "SELECT p FROM Prescribe p WHERE p.idMedicalRecords = :idMR",
                    Prescribe.class
            );
            query.setParameter("idMR", idMedicalRecord);
            List<Prescribe> prescribes = query.getResultList();
            if (prescribes.isEmpty())
                return Either.left(new HospitalError(2, PrescribeConstants.PRESCRIBE_NOT_FOUND));
            return Either.right(prescribes);
        } catch (Exception ex) {
            return Either.left(new HospitalError(2, PrescribeConstants.ERROR_READING_FILE));
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public Either<HospitalError, Prescribe> get(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(Prescribe prescribe) {
        em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        Either<HospitalError, Integer> result;

        try {
            tx.begin();
            em.persist(prescribe);
            tx.commit();
            result = Either.right(1);
        } catch (PersistenceException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            result = Either.left(new HospitalError(0, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND));
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(Prescribe o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Prescribe o) {
        return null;
    }
}
