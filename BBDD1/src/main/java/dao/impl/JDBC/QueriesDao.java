package dao.impl.JDBC;

import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Patient;
import model.PatientUIV2;
import model.error.HospitalError;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static common.constants.CommonConstants.NO_DATA_FOUND;

public class QueriesDao {
    private final DBConnectionPool db;

    @Inject
    public QueriesDao(DBConnectionPool db) {
        this.db = db;
    }

    public String getPatientWithMostMedicalRecords() {
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SQLQueries.GET_PATIENT_WITH_MOST_MEDICAL_RECORDS)) {

            if (rs.next()) {
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                int count = rs.getInt("medicalRecordsCount");

                return "Patient with most medical records: " + name + " " + phone + " with " + count + " records.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return NO_DATA_FOUND;
    }

    public String getDateWithMorePatients() {
        try (Connection con = db.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(SQLQueries.GET_DATE_WITH_MORE_PATIENTS)) {

            if (rs.next()) {
                String date = rs.getString("date");
                return "Date with more patients: " + date;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return NO_DATA_FOUND;
        }
        return NO_DATA_FOUND;
    }

public Either<HospitalError, List<Patient>> getPatientsByMedication(String name, String dosage) {
    try (Connection con = db.getConnection();
         PreparedStatement stmt = con.prepareStatement(SQLQueries.GET_PATIENTS_BY_MEDICATION)) {

        stmt.setString(1, name);
        stmt.setString(2, dosage);

        try (ResultSet rs = stmt.executeQuery()) {
            List<Patient> patients = new ArrayList<>();
            while (rs.next()) {
                patients.add(new Patient(rs));
            }
            return Either.right(patients);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return Either.left(new HospitalError(0,"Error getting patients by medication"));
    }
}

public Either<HospitalError, List<PatientUIV2>> getPatientsAndMedications() {
    try (Connection con = db.getConnection();
         Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(SQLQueries.GET_PATIENTS_AND_MEDICATIONS)) {

        List<PatientUIV2> patients = new ArrayList<>();
        while (rs.next()) {
            patients.add(new PatientUIV2(rs));
        }
        return Either.right(patients);

    } catch (SQLException e) {
        e.printStackTrace();
        return Either.left(new HospitalError(0,"Error getting patients and medications"));
    }
}
}
