package dao;

import io.vavr.control.Either;
import model.error.HospitalError;
import model.hibernate.Patient;

import java.util.List;

public interface PatientsDao {
    Either<HospitalError, List<Patient>> getAll();

    Either<HospitalError, Patient> get(int id);

    Either<HospitalError, Patient> get(String name);

    Either<HospitalError, Integer> add(Patient o);

    Either<HospitalError, Integer> add(List<model.mongo.Patient> o);

    Either<HospitalError, Integer> delete(Patient o);

    Either<HospitalError, Integer> update(Patient patient);
}
