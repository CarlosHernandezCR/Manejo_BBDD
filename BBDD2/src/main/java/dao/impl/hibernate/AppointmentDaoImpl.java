package dao.impl.hibernate;

import common.constants.AppointmentConstants;
import dao.AppointmentDao;
import dao.JPAUtil;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import model.error.HospitalError;
import model.hibernate.Appointment;

import java.util.List;

public class AppointmentDaoImpl implements AppointmentDao {
    private JPAUtil jpaUtil;

    @Inject
    public AppointmentDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<Appointment>> getAll() {
        Either<HospitalError, List<Appointment>> result;
        EntityManager em = jpaUtil.getEntityManager();

        try {
            List<Appointment> appointments = em.createNamedQuery("HQL_GET_ALL_APPOINTMENT", Appointment.class).getResultList();
            if (appointments != null) {
                result = Either.right(appointments);
            } else {
                result = Either.left(new HospitalError(0, AppointmentConstants.EMPTY_APPOINTMENT));
            }
        } catch (Exception ex) {
            result = Either.left(new HospitalError(0, AppointmentConstants.READ_ERROR));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, Appointment> get(String username) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(Appointment o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Appointment o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Appointment o) {
        return null;
    }
}
