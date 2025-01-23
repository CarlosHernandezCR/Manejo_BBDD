package dao.impl.Spring;

import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import dao.DoctorsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Doctor;
import model.error.HospitalError;
import model.mappers.DoctorMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DoctorDaoImpl implements DoctorsDao {
    DBConnectionPool db;
    @Inject
    public DoctorDaoImpl(DBConnectionPool db) {
        this.db = db;
    }
    @Override
    public Either<HospitalError, List<Doctor>> getAll() {
        return null;
    }

@Override
public Either<HospitalError, Doctor> get(int id) {
    JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
    try {
        Doctor doctor = jtm.queryForObject(SQLQueries.GET_DOCTOR_BY_ID, new DoctorMapper(),id );
        return Either.right(doctor);
    } catch (EmptyResultDataAccessException e) {
        return Either.left(new HospitalError(1, "Doctor with id " + id + " not found"));
    }
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
