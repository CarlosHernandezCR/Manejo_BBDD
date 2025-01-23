package dao.impl.JDBC;

import common.constants.MedicalRecordConstants;
import common.constants.PrescribeConstants;
import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.MedicalRecord;
import model.Patient;
import model.Prescribe;
import model.XML.ListMedicalRecordsXML;
import model.error.HospitalError;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MedicalRecordsDaoImpl implements dao.MedicalRecordsDao{
    private final DBConnectionPool db;

    @Inject
    public MedicalRecordsDaoImpl(DBConnectionPool db) {
        this.db = db;
    }
    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll() {
        return null;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(int idPatient) {
        Either<HospitalError, List<MedicalRecord>> result;
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement statement = con.prepareStatement(SQLQueries.GET_ALL_MEDICALRECORDS_WITH_IDPATIENT)) {
            statement.setInt(1, idPatient);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                medicalRecords.add(new MedicalRecord(rs));
            }
            if (medicalRecords.isEmpty()) {
                result = Either.left(new HospitalError(1, MedicalRecordConstants.IS_EMPTY));
            } else result = Either.right(medicalRecords);
        } catch (SQLException ex) {
            Logger.getLogger(MedicalRecord.class.getName()).log(Level.SEVERE, null, ex);
            result = Either.left(new HospitalError(2, MedicalRecordConstants.ERROR_READING_FILE));
        }
        return result;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(LocalDate date) {
        return null;
    }

    @Override
    public Either<HospitalError, MedicalRecord> get(int id) {
        return null;
    }

@Override
public Either<HospitalError, Integer> add(MedicalRecord m) {
    try (Connection con = db.getConnection()) {
        con.setAutoCommit(false);
        try (PreparedStatement statement = con.prepareStatement(SQLQueries.INSERT_MEDICAL_RECORD, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, m.getIdDoctor());
            statement.setInt(2, m.getIdPatient());
            statement.setString(3, m.getDiagnoses());
            statement.setDate(4, java.sql.Date.valueOf(m.getAdmissionDate()));

            int rowsAffected = statement.executeUpdate();


            Either<HospitalError, Integer> generatedKey = getGeneratedKey(m, statement);
            if (generatedKey != null) return generatedKey;

            Either<HospitalError, Integer> addPrescription = addPrescriptions(m, con);
            if (addPrescription != null) return addPrescription;
            rowsAffected += m.getPrescribes().size();
            con.commit();
            return Either.right(rowsAffected);
        } catch (SQLException ex) {
            con.rollback();///////////////////////////SOLO EN EL CATCH
            return Either.left(new HospitalError(2, MedicalRecordConstants.ERROR_WRITING_FILE));
        }finally {
            con.setAutoCommit(false);
        }
    } catch (SQLException ex) {
        return Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_WRITING_FILE));
    }
}

    @Override
    public Either<HospitalError, Integer> add(ListMedicalRecordsXML list) {
        return null;
    }

    private static Either<HospitalError, Integer> getGeneratedKey(MedicalRecord m, PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                m.setId(generatedKeys.getInt(1));
            } else {
                return Either.left(new HospitalError(2, MedicalRecordConstants.ERROR_WRITING_FILE));
            }
        }
        return null;
    }

    private static Either<HospitalError, Integer> addPrescriptions(MedicalRecord m, Connection con) throws SQLException {
        try (PreparedStatement statementPrescribe = con.prepareStatement(SQLQueries.INSERT_PRESCRIBE)) {
            for (Prescribe prescribe : m.getPrescribes()) {
                statementPrescribe.setString(1, prescribe.getName());
                statementPrescribe.setString(2, prescribe.getDossages());
                statementPrescribe.setInt(3, m.getId());
            }
        }catch (SQLException ex){
            con.rollback();
            return Either.left(new HospitalError(2, MedicalRecordConstants.ERROR_WRITING_FILE));
        }
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(List<MedicalRecord> list) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(MedicalRecord o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Patient p) {
        Either<HospitalError, Integer> result;
        int nTablesDeleted=0;
        try (Connection con = db.getConnection()) {
            List<MedicalRecord> medicalRecords = getAll(p.getId()).get();
            for(MedicalRecord medicalRecord:medicalRecords){
                try (PreparedStatement deletePrescribeStatement = con.prepareStatement(SQLQueries.DELETE_PRESCRIBES_WITH_IDMEDICALRECORD)){
                    deletePrescribeStatement.setInt(1, medicalRecord.getId());
                    nTablesDeleted=nTablesDeleted+deletePrescribeStatement.executeUpdate();
                }catch (SQLException sqle){
                    return Either.left(new HospitalError(1, PrescribeConstants.ERROR_DELETING_PRESCRIBES));
                }
            }
            try (PreparedStatement preparedStatement = con.prepareStatement(SQLQueries.DELETE_MEDICALRECORDS_WITH_IDPATIENT)) {
                preparedStatement.setInt(1, p.getId());
                nTablesDeleted=nTablesDeleted+preparedStatement.executeUpdate();
                result=Either.right(nTablesDeleted);
            }catch (SQLException sqle){
                result=Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_DELETING_MEDICALRECORDS));
            }
        } catch (SQLException e) {
            result = Either.left(new HospitalError(0, MedicalRecordConstants.ERROR_READING_FILE));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(List<MedicalRecord> medicalRecords) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(MedicalRecord o) {
        return null;
    }
}
