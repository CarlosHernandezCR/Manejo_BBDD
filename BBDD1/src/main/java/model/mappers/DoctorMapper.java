package model.mappers;

import model.Doctor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class DoctorMapper implements RowMapper<Doctor> {
    @Override
    public Doctor mapRow(ResultSet rs, int rowNum) {
        return new Doctor(rs);
    }
}
