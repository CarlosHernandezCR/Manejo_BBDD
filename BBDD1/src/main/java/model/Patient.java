package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Patient {
    private int id;
    private String name;
    private String phone;
    private LocalDate dob;

    public Patient(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        this.name = data[1];
        this.phone = data[2];
        String[] dateSplits = data[3].split("-");
        int year = Integer.parseInt(dateSplits[0]);
        int month = Integer.parseInt(dateSplits[1]);
        int day = Integer.parseInt(dateSplits[2]);
        dob = LocalDate.of(year, month, day);    }

    public Patient(ResultSet rs) {
        try {
            this.id = rs.getInt("idPatient");
            this.name = rs.getString("name");
            this.phone = rs.getString("phone");
            this.dob = rs.getDate("dob").toLocalDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toFileLine() {
        return id + ";" + name + ";" + phone + ";" + dob.toString();
    }
}
