package dao;

import model.Doctor;
import io.vavr.control.Either;
import model.error.HospitalError;


import java.util.List;

public interface DoctorsDao {
    Either<HospitalError,List<Doctor>>getAll();
    Either<HospitalError,Doctor> get(int id);
    Either<HospitalError, Integer> add(Doctor o);
    Either<HospitalError, Integer> delete(Doctor o);
    Either<HospitalError, Integer> update(Doctor o);
}
