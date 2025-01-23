package dao;

import io.vavr.control.Either;
import model.error.HospitalError;
import model.hibernate.MedicalRecord;

import java.time.LocalDate;
import java.util.List;

public interface MedicalRecordsDao {
    Either<HospitalError, List<MedicalRecord>> getAll();

    Either<HospitalError, List<MedicalRecord>> getAll(LocalDate date, int before);

    Either<HospitalError, MedicalRecord> get(int id);

    Either<HospitalError, Integer> add(MedicalRecord o);

    Either<HospitalError, Integer> delete(MedicalRecord o);

    Either<HospitalError, Integer> delete(List<MedicalRecord> o, boolean b);

    Either<HospitalError, Integer> update(MedicalRecord o);
}
