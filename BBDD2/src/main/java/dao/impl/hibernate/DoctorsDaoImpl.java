package dao.impl.hibernate;

import common.constants.DoctorConstants;
import dao.DoctorsDao;
import dao.JPAUtil;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import model.error.HospitalError;
import model.hibernate.Doctor;

import java.util.List;

public class DoctorsDaoImpl implements DoctorsDao {
    private JPAUtil jpaUtil;
    private EntityManager em;

    @Inject
    public DoctorsDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<Doctor>> getAll() {
        Either<HospitalError, List<Doctor>> result;
        em = jpaUtil.getEntityManager();

        try {
            List<Doctor> appointments = em.createNamedQuery("HQL_GET_ALL_DOCTORS", Doctor.class).getResultList();
            if (appointments != null) {
                result = Either.right(appointments);
            } else {
                result = Either.left(new HospitalError(0, DoctorConstants.EMPTY_DOCTOR));
            }
        } catch (Exception ex) {
            result = Either.left(new HospitalError(0, DoctorConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, Doctor> get(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Doctor> get(String name) {
        try {
            em = jpaUtil.getEntityManager();
            Doctor doctor = em.createQuery("SELECT d FROM Doctor d WHERE d.name = :name", Doctor.class)
                    .setParameter("name", name)
                    .getSingleResult();
            if (doctor != null)
                return Either.right(doctor);
            else
                return Either.left(new HospitalError(1, DoctorConstants.DOCTOR_NOT_FOUND));
        } catch (Exception e) {
            return Either.left(new HospitalError(1, e.getMessage()));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Either<HospitalError, Integer> add(Doctor o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(List<model.mongo.Doctor> doctors) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Doctor o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Doctor o) {
        return null;
    }
}
