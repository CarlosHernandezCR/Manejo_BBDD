package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private int id;
    private LocalDate date;
    private int idPatient;
    private int idDoctor;

    public Appointment(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        String[] dateSplits = data[1].split("-");
        int year = Integer.parseInt(dateSplits[0]);
        int month = Integer.parseInt(dateSplits[1]);
        int day = Integer.parseInt(dateSplits[2]);
        date = LocalDate.of(year, month, day);
        this.idPatient = Integer.parseInt(data[2]);
        this.idDoctor = Integer.parseInt(data[3]);
    }
    public String toFileLine() {
        return id + ";" + date + ";" + idPatient + ";" + idDoctor;
    }
}
