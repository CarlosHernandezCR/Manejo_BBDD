package dao;

import io.vavr.control.Either;
import model.error.HospitalError;
import model.hibernate.Doctor;

import java.util.List;

public interface DoctorsDao {
    Either<HospitalError, List<Doctor>> getAll();

    Either<HospitalError, Doctor> get(int id);

    Either<HospitalError, Doctor> get(String name);

    Either<HospitalError, Integer> add(Doctor o);

    Either<HospitalError, Integer> add(List<model.mongo.Doctor> doctors);

    Either<HospitalError, Integer> delete(Doctor o);

    Either<HospitalError, Integer> update(Doctor o);
}
