package dao;

import io.vavr.control.Either;
import model.error.HospitalError;
import model.hibernate.Credential;

import java.util.List;

public interface CredentialsDao {
    Either<HospitalError, List<Credential>> getAll();

    Either<HospitalError, Credential> get(String username);

    Either<HospitalError, Integer> add(Credential o);

    Either<HospitalError, Integer> delete(Credential o);

    Either<HospitalError, Integer> update(Credential o);
}
