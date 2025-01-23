package dao.impl.hibernate;

import common.constants.PaymentConstants;
import dao.JPAUtil;
import dao.PaymentDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import model.error.HospitalError;
import model.hibernate.Payment;

import java.util.List;

public class PaymentDaoImpl implements PaymentDao {
    private JPAUtil jpaUtil;

    @Inject
    public PaymentDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<Payment>> getAll() {
        Either<HospitalError, List<Payment>> result;
        EntityManager em = jpaUtil.getEntityManager();

        try {
            List<Payment> payments = em.createNamedQuery("HQL_GET_ALL_PAYMENTS", Payment.class).getResultList();
            if (payments != null)
                result = Either.right(payments);
            else
                result = Either.left(new HospitalError(0, PaymentConstants.IS_EMPTY));
        } catch (PersistenceException ex) {
            result = Either.left(new HospitalError(0, PaymentConstants.ERROR_READING_FILE));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, List<Payment>> getAll(int idPatient) {
        return null;
    }

    @Override
    public Either<HospitalError, Payment> get(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(Payment p) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Payment p) {
        return null;
    }
}
