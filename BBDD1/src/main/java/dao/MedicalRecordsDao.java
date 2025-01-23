package dao;

import io.vavr.control.Either;
import model.MedicalRecord;
import model.Patient;
import model.XML.ListMedicalRecordsXML;
import model.error.HospitalError;

import java.time.LocalDate;
import java.util.List;

public interface MedicalRecordsDao {
    Either<HospitalError,List<MedicalRecord>>getAll();
    Either<HospitalError,List<MedicalRecord>> getAll(int idPatient);
    Either<HospitalError,List<MedicalRecord>> getAll(LocalDate date);
    Either<HospitalError,MedicalRecord> get(int id);
    Either<HospitalError, Integer> add(MedicalRecord o);
    Either<HospitalError, Integer> add(ListMedicalRecordsXML list);

    Either<HospitalError, Integer> add(List<MedicalRecord> list);

    Either<HospitalError, Integer> delete(MedicalRecord o);
    Either<HospitalError, Integer> delete(int id);
    Either<HospitalError, Integer> delete(Patient patient);
    Either<HospitalError, Integer> delete(List<MedicalRecord> medicalRecords);

    Either<HospitalError, Integer> update(MedicalRecord o);
}
