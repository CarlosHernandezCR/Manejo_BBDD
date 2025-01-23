package dao;

import io.vavr.control.Either;
import model.Appointment;
import model.error.HospitalError;

import java.util.List;

public interface AppointmentDao {
    Either<HospitalError, List<Appointment>> getAll();
    Either<HospitalError,Appointment> get(String username);
    Either<HospitalError, Integer> add(Appointment o);
    Either<HospitalError, Integer> delete(Appointment o);
    Either<HospitalError, Integer> delete(int patientId);
    Either<HospitalError, Integer> update(Appointment o);
}
