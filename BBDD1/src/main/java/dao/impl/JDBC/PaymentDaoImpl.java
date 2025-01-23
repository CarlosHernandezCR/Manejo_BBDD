package dao.impl.JDBC;

import common.constants.PaymentConstants;
import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import dao.PaymentDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Payment;
import model.XML.MedicalRecordXML;
import model.error.HospitalError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentDaoImpl implements PaymentDao {
    private final DBConnectionPool db;

    @Inject
    public PaymentDaoImpl(DBConnectionPool db) {
        this.db = db;
    }
    @Override
    public Either<HospitalError, List<Payment>> getAll() {
        return null;
    }
    @Override
    public Either<HospitalError, List<Payment>> getAll(int idPatient) {
        Either<HospitalError, List<Payment>> result;
        List<Payment> medicalRecords = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement statement = con.prepareStatement(SQLQueries.GET_ALL_PAYMENT_WITH_IDPATIENT)) {
            statement.setInt(1, idPatient);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                medicalRecords.add(new Payment(rs));
            }
            if (medicalRecords.isEmpty()) {
                result = Either.left(new HospitalError(1, PaymentConstants.IS_EMPTY));
            } else result = Either.right(medicalRecords);
        } catch (SQLException ex) {
            result = Either.left(new HospitalError(2, PaymentConstants.ERROR_READING_FILE));
        }
        return result;
    }
    @Override
    public Either<HospitalError, Payment> get(int id) {
        return null;
    }



    @Override
    public Either<HospitalError, Integer> add(Payment o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(int id, MedicalRecordXML medicalRecordXML) {
        return null;
    }
}
