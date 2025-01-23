package dao.impl.Spring;

import common.constants.PatientsConstants;
import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import dao.PatientsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Patient;
import model.XML.ListPatientsXML;
import model.XML.MedicalRecordXML;
import model.error.HospitalError;
import model.mappers.PatientMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class PatientsDaoImpl implements PatientsDao {
    private DBConnectionPool db;
     @Inject
    public PatientsDaoImpl(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<HospitalError, List<Patient>> getAll() {
        Either<HospitalError,List<Patient>> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<Patient> medicalRecords = jtm.query(SQLQueries.GET_ALL_PATIENTS, new PatientMapper());
        if (medicalRecords.isEmpty()) {
            result = Either.left(new HospitalError(0, PatientsConstants.PATIENT_NOT_FOUND));
        } else result = Either.right(medicalRecords);
        return result;
    }

    @Override
    public Either<HospitalError, Patient> get(int id) {
        Either<HospitalError,Patient> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<Patient> patients = jtm.query(SQLQueries.GET_PATIENT_BY_ID, new PatientMapper(), id);
        if (patients.isEmpty()) {
            result = Either.left(new HospitalError(0, PatientsConstants.PATIENT_NOT_FOUND));
        } else result = Either.right(patients.get(0));
        return result;
    }

    @Override
    public Either<HospitalError, List<Patient>> get(String medication) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(Patient o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(ListPatientsXML list) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(List<Patient> list) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Patient o) {
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
