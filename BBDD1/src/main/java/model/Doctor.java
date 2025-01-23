package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Doctor{
    private int id;
    private String name;
    private String phone;
    private String speciality;

    public Doctor(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        this.name = data[1];
        this.phone = data[2];
        this.speciality = data[3];
    }

    public Doctor(ResultSet rs) {
        try {
            this.id = rs.getInt("idDoctor");
            this.name = rs.getString("name");
            this.phone = rs.getString("phone");
            this.speciality = rs.getString("speciality");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toFileLine() {
        return id + ";" + name + ";" + phone + ";" + speciality;
    }

}
