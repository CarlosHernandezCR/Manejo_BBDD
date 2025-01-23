package dao.impl.hibernate;

import common.constants.CredentialConstants;
import dao.CredentialsDao;
import dao.JPAUtil;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import model.error.HospitalError;
import model.hibernate.Credential;

import java.util.List;

public class CredentialsDaoImpl implements CredentialsDao {
    private JPAUtil jpaUtil;
    private EntityManager em;

    @Inject
    public CredentialsDaoImpl(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }


    @Override
    public Either<HospitalError, List<Credential>> getAll() {
        return null;
    }

    @Override
    public Either<HospitalError, Credential> get(String name) {
        Either<HospitalError, Credential> result;
        em = jpaUtil.getEntityManager();

        try {
            Credential credentials = em.createQuery("SELECT c FROM Credential c WHERE c.username = :name", Credential.class)
                    .setParameter("name", name)
                    .getSingleResult();
            if (credentials != null) {
                result = Either.right(credentials);
            } else {
                result = Either.left(new HospitalError(0, CredentialConstants.NOT_FOUND));
            }
        } catch (PersistenceException ex) {
            result = Either.left(new HospitalError(0, CredentialConstants.NOT_FOUND));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(Credential c) {
        em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        Either<HospitalError, Integer> result;

        try {
            tx.begin();
            em.persist(c);
            tx.commit();
            result = Either.right(2);
        } catch (PersistenceException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            result = Either.left(new HospitalError(0, ex.getMessage()));
        } finally {
            em.close();
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(Credential credentials) {
        Either<HospitalError, Integer> result;
        em = jpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Credential credentialsToDelete = em.merge(credentials);
            em.remove(credentialsToDelete);
            tx.commit();
            result = Either.right(1);
        } catch (PersistenceException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            result = Either.left(new HospitalError(0, CredentialConstants.NOT_FOUND));
        } finally {
            em.close();
        }

        return result;
    }

    @Override
    public Either<HospitalError, Integer> update(Credential o) {
        return null;
    }
}
