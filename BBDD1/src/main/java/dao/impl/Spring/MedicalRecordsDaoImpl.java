package dao.impl.Spring;

import common.constants.MedicalRecordConstants;
import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import dao.MedicalRecordsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.MedicalRecord;
import model.Patient;
import model.XML.ListMedicalRecordsXML;
import model.error.HospitalError;
import model.mappers.MedicalRecordsMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordsDaoImpl implements MedicalRecordsDao {
    DBConnectionPool db;

    @Inject
    public MedicalRecordsDaoImpl(DBConnectionPool db) {
        this.db = db;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll() {
        Either<HospitalError, List<MedicalRecord>> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<MedicalRecord> medicalRecords = jtm.query(SQLQueries.GET_ALL_MEDICALRECORDS, new MedicalRecordsMapper());
        if (medicalRecords.isEmpty()) {
            result = Either.left(new HospitalError(0, "Error getting medical records"));
        } else result = Either.right(medicalRecords);
        return result;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(int idPatient) {
        return null;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(LocalDate date) {
        Either<HospitalError, List<MedicalRecord>> result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<MedicalRecord> medicalRecords = jtm.query(SQLQueries.GET_FROM_YEAR_TO_PREVIOUS, new MedicalRecordsMapper(), date.getYear());
        if (medicalRecords.isEmpty()) {
            result = Either.left(new HospitalError(0, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND));
        } else {
            result = Either.right(medicalRecords);
        }
        return result;
    }

    @Override
    public Either<HospitalError, MedicalRecord> get(int idPatient) {
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<MedicalRecord> medicalRecord = jtm.query(SQLQueries.GET_NEWEST_MR_BY_PATIENT, new MedicalRecordsMapper(), idPatient);
        if (medicalRecord.isEmpty()) {
            return Either.left(new HospitalError(0, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND));
        } else {
            return Either.right(medicalRecord.get(0));
        }
    }

    @Override
    public Either<HospitalError, Integer> add(MedicalRecord o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(ListMedicalRecordsXML list) {
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
public Either<HospitalError, Integer> delete(List<MedicalRecord> medicalRecords) {
    DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(db.getDataSource());
    TransactionStatus txStatus = transactionManager.getTransaction(txDef);

    try {
        JdbcTemplate jtm = new JdbcTemplate(transactionManager.getDataSource());
        for (MedicalRecord medicalRecord : medicalRecords) {
            jtm.update(SQLQueries.DELETE_PRESCRIBES_WITH_IDMEDICALRECORD, medicalRecord.getId());
            jtm.update(SQLQueries.DELETE_MEDICALRECORDS_WITH_ID, medicalRecord.getId());
        }
        transactionManager.commit(txStatus);
        return Either.right(medicalRecords.size());
    } catch (DataAccessException e) {
        transactionManager.rollback(txStatus);
        return Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_DELETING_MEDICALRECORDS));
    }
}

    @Override
    public Either<HospitalError, Integer> delete(Patient patient) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(MedicalRecord o) {
        return null;
    }
}
