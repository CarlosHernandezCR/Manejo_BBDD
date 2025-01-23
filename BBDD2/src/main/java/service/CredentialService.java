package service;

import common.constants.CredentialConstants;
import dao.CredentialsDao;
import dao.impl.hibernate.CredentialsDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.hibernate.Credential;

public class CredentialService {
    CredentialsDao credentialsDao;

    @Inject
    public CredentialService(CredentialsDaoImpl credentialsDao) {
        this.credentialsDao = credentialsDao;
    }

    public Either<HospitalError, Credential> login(String username, String password) {
        Either<HospitalError, Credential> data = credentialsDao.get(username);
        Either<HospitalError, Credential> result;
        if (data.isRight()) {
            Credential credential = data.get();
            if (credential.getPassword().equals(password)) {
                result = Either.right(credential);
            } else {
                result = Either.left(new HospitalError(0, CredentialConstants.WRONGCREDENTIALS));
            }
        } else {
            result = Either.left(data.getLeft());
        }
        return result;
    }
}
