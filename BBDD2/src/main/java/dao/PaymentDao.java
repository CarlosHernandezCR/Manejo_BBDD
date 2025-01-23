package dao;

import io.vavr.control.Either;
import model.error.HospitalError;
import model.hibernate.Payment;

import java.util.List;

public interface PaymentDao {
    Either<HospitalError, List<Payment>> getAll();

    Either<HospitalError, List<Payment>> getAll(int idPatient);

    Either<HospitalError, Payment> get(int id);

    Either<HospitalError, Integer> add(Payment p);

    Either<HospitalError, Integer> delete(int id);

    Either<HospitalError, Integer> update(Payment p);
}
