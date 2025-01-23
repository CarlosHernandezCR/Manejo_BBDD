package dao.impl.hibernate;

import common.constants.PatientsConstants;
import dao.JPAUtil;
import dao.PatientsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import model.error.HospitalError;
import model.hibernate.Patient;

import java.util.List;

public class PatientsDaoImpl implements PatientsDao {
    private JPAUtil jpaUtil;

    @Inject
    public PatientsDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<Patient>> getAll() {
        Either<HospitalError, List<Patient>> result;
        EntityManager em = jpaUtil.getEntityManager();
        try {
            List<Patient> patients = em.createNamedQuery("HQL_GET_ALL_PATIENTS", Patient.class).getResultList();
            if (patients.isEmpty())
                result = Either.left(new HospitalError(0, PatientsConstants.EMPTYLISTERROR));
            else
                result = Either.right(patients);
        } catch (PersistenceException ex) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Either<HospitalError, Patient> get(int id) {
        EntityManager em = null;
        try {
            em = jpaUtil.getEntityManager();
            Patient patient = em.find(Patient.class, id);
            if (patient != null)
                return Either.right(patient);
            else
                return Either.left(new HospitalError(1, PatientsConstants.PATIENT_NOT_FOUND));
        } catch (Exception e) {
            return Either.left(new HospitalError(1, e.getMessage()));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Either<HospitalError, Patient> get(String name) {
        EntityManager em = null;
        try {
            em = jpaUtil.getEntityManager();
            Patient patient = em.createQuery("SELECT d FROM Patient d WHERE d.name = :name", Patient.class)
                    .setParameter("name", name)
                    .getSingleResult();
            if (patient != null)
                return Either.right(patient);
            else
                return Either.left(new HospitalError(1, PatientsConstants.PATIENT_NOT_FOUND));
        } catch (Exception e) {
            return Either.left(new HospitalError(1, e.getMessage()));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Either<HospitalError, Integer> add(Patient patient) {
        EntityManager em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(patient);
            tx.commit();
            return Either.right(1);
        } catch (Exception e) {
            return Either.left(new HospitalError(1, e.getMessage()));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Either<HospitalError, Integer> add(List<model.mongo.Patient> patients) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Patient patient) {
        EntityManager em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (patient != null) {
                em.remove(em.merge(patient));
                tx.commit();
                return Either.right(1);
            } else {
                return Either.left(new HospitalError(2, PatientsConstants.PATIENT_NOT_FOUND));
            }
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            return Either.left(new HospitalError(1, e.getMessage()));
        } finally {
            em.close();
        }
    }


    @Override
    public Either<HospitalError, Integer> update(Patient patient) {
        EntityManager em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(patient);
            tx.commit();
            return Either.right(1);
        } catch (EntityNotFoundException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            return Either.left(new HospitalError(2, PatientsConstants.PATIENT_NOT_FOUND));
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            return Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE));
        } finally {
            em.close();
        }
    }
}
