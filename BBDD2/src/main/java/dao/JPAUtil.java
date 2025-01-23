package dao;

import common.constants.CommonConstants;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@Singleton
public class JPAUtil {

    private EntityManagerFactory emf;

    public JPAUtil() {
        emf = getEmf();
    }

    private EntityManagerFactory getEmf() {
        return Persistence.createEntityManagerFactory(CommonConstants.PERSISTENCE_UNIT_NAME);
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
