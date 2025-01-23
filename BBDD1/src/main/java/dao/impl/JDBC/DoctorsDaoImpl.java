package dao.impl.JDBC;

import common.constants.DoctorConstants;
import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import dao.DoctorsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Doctor;
import model.error.HospitalError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorsDaoImpl implements DoctorsDao {
    private final DBConnectionPool db;

    @Inject
    public DoctorsDaoImpl(DBConnectionPool db) {
        this.db = db;
    }
    @Override
    public Either<HospitalError, List<Doctor>> getAll() {
        Either<HospitalError, List<Doctor>> result;
        try (Connection con = db.getConnection();
             PreparedStatement statement = con.prepareStatement(SQLQueries.GET_ALL_DOCTORS)){
            ResultSet rs = statement.executeQuery();
            List<Doctor> doctors = new ArrayList<>();
            while (rs.next()) {
                doctors.add(new Doctor(rs));
            }
            if(doctors.isEmpty()) {
                result = Either.left(new HospitalError(0, DoctorConstants.DOCTOR_NOT_FOUND));
            } else {
                result = Either.right(doctors);
            }
        } catch (SQLException ex) {
            result = Either.left(new HospitalError(0, DoctorConstants.ERROR_READING_FILE));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Doctor> get(int id) {
        Either<HospitalError, Doctor> result;
        try (Connection con = db.getConnection();
             PreparedStatement statement = con.prepareStatement(SQLQueries.GET_DOCTOR_BY_ID)) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = Either.right(new Doctor(rs));
            } else {
                result = Either.left(new HospitalError(0, DoctorConstants.DOCTOR_NOT_FOUND));
            }
        } catch (SQLException ex) {
            result = Either.left(new HospitalError(0, DoctorConstants.ERROR_READING_FILE));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(Doctor o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Doctor o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Doctor o) {
        return null;
    }
}
