package model.mappers;


import model.MedicalRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class MedicalRecordsMapper implements RowMapper<MedicalRecord> {
    @Override
    public MedicalRecord mapRow(ResultSet rs, int rowNum) {
        return new MedicalRecord(rs);
    }
}
