package dao;

import io.vavr.control.Either;
import model.Payment;
import model.XML.MedicalRecordXML;
import model.error.HospitalError;

import java.util.List;

public interface PaymentDao {
    Either<HospitalError, List<Payment>> getAll();
    Either<HospitalError,List<Payment>> getAll(int idPatient);
    Either<HospitalError,Payment> get(int id);
    Either<HospitalError, Integer> add(Payment o);
    Either<HospitalError, Integer> delete(int id);
    Either<HospitalError, Integer> update(int id, MedicalRecordXML medicalRecordXML);
}
