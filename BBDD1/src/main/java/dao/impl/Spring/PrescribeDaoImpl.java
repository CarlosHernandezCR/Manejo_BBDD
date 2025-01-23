package dao.impl.Spring;

import common.constants.PrescribeConstants;
import common.constants.SQLQueries;
import common.util.DBConnectionPool;
import dao.PrescribeDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.MedicalRecord;
import model.Prescribe;
import model.error.HospitalError;
import model.mappers.PrescribeMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.List;
import java.util.Map;

public class PrescribeDaoImpl implements PrescribeDao {
    DBConnectionPool db;
    @Inject
    public PrescribeDaoImpl(DBConnectionPool db) {
        this.db = db;
    }
    @Override
    public Either<HospitalError, List<Prescribe>> getAll() {
        return null;
    }

    @Override
    public Either<HospitalError, List<Prescribe>> getAll(int id) {
        Either<HospitalError,List<Prescribe>>result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<Prescribe> prescribes = jtm.query(SQLQueries.GET_ALL_PRESCRIBES_WITH_IDMEDICALRECORD,new PrescribeMapper(),id);
        if (prescribes.isEmpty()) {
            result = Either.left(new HospitalError(0, "0, Error getting prescribes"));
        } else result = Either.right(prescribes);
        return result;
    }
    @Override
    public Either<HospitalError, Prescribe> get(int id) {
        Either<HospitalError,Prescribe>result;
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        List<Prescribe> prescribes = jtm.query(SQLQueries.GET_PRESCRIBE_WITH_ID,new PrescribeMapper(),id);
        if (prescribes.isEmpty()) {
            result = Either.left(new HospitalError(0, PrescribeConstants.PRESCRIBE_NOT_FOUND));
        } else result = Either.right(prescribes.get(0));
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(Prescribe o) {
        Either<HospitalError, Integer> result;
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(db.getDataSource()).withTableName("Prescribe");
        Map<String,Object> parameters = Map.of(
                "ID",o.getId(),
                "NAME",o.getName(),
                "DOSSAGES",o.getDossages(),
                "IDMEDICALRECORDS",o.getIdMedicalRecords()
        );
        int res = jdbcInsert.execute(parameters);
        if(res == 0) result= Either.left(new HospitalError(0, PrescribeConstants.PRESCRIBE_NOT_ADDED));
        else result= Either.right(res);
        return result;
    }
    @Override
    public Either<HospitalError, Integer> add(List<Prescribe> list) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Prescribe o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(MedicalRecord o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Prescribe o) {
        JdbcTemplate jtm = new JdbcTemplate(db.getDataSource());
        return Either.right(jtm.update(SQLQueries.UPDATE_PRESCRIBE,o.getName(),o.getDossages(),o.getId()));
    }
}
