package dao;

import io.vavr.control.Either;
import model.Patient;
import model.XML.ListPatientsXML;
import model.XML.MedicalRecordXML;
import model.error.HospitalError;

import java.util.List;

public interface PatientsDao {
    Either<HospitalError,List<Patient>>getAll();
    Either<HospitalError,Patient> get(int id);
    Either<HospitalError,List<Patient>> get(String medication);
    Either<HospitalError, Integer> add(Patient o);
    Either<HospitalError,Integer> add(ListPatientsXML list);
    Either<HospitalError,Integer> add(List<Patient> list);
    Either<HospitalError, Integer> delete(Patient o);
    Either<HospitalError, Integer> delete(int id);
    Either<HospitalError, Integer> update(int id, MedicalRecordXML medicalRecordXML);
}
