package dao;

import io.vavr.control.Either;
import model.MedicalRecord;
import model.Prescribe;
import model.error.HospitalError;

import java.util.List;

public interface PrescribeDao {
    Either<HospitalError,List<Prescribe>>getAll();
    Either<HospitalError,List<Prescribe>>getAll(int id);
    Either<HospitalError,Prescribe> get(int id);
    Either<HospitalError, Integer> add(Prescribe o);

    Either<HospitalError, Integer> add(List<Prescribe> list);

    Either<HospitalError, Integer> delete(Prescribe  o);
    Either<HospitalError, Integer> delete(MedicalRecord o);
    Either<HospitalError, Integer> update(Prescribe o);

}
