package model.mappers;

import model.Prescribe;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;

public class PrescribeMapper implements RowMapper<Prescribe> {
    @Override
    public Prescribe mapRow(ResultSet rs, int rowNum){
        return new Prescribe(rs);
    }
}
