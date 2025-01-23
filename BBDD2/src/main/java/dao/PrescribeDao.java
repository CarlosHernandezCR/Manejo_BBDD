package dao;

import io.vavr.control.Either;
import model.error.HospitalError;
import model.hibernate.Prescribe;

import java.util.List;

public interface PrescribeDao {
    Either<HospitalError, List<Prescribe>> getAll();

    Either<HospitalError, List<Prescribe>> getAll(int idMedicalRecord);

    Either<HospitalError, Prescribe> get(int id);

    Either<HospitalError, Integer> add(Prescribe o);

    Either<HospitalError, Integer> delete(Prescribe o);

    Either<HospitalError, Integer> update(Prescribe o);

}
