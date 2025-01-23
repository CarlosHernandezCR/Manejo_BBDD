package model.mappers;

import model.Patient;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class PatientMapper implements RowMapper<Patient> {
    @Override
    public Patient mapRow(ResultSet rs, int rowNum) {
        return new Patient(rs);
    }
}
