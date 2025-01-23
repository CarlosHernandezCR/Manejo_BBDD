package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    private int id;
    private LocalDate date;
    private double quantity;
    private int idPatient;

    public Payment(ResultSet rs) {
        try {
            this.id = rs.getInt("idPayment");
            this.date = rs.getDate("date").toLocalDate();
            this.quantity = rs.getDouble("quantity");
            this.idPatient = rs.getInt("idPatient");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toFileLine() {
        return id + ";" + date.toString() + ";" + quantity + ";" + idPatient;
    }

    public Payment(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        String[] dateSplits = data[1].split("-");
        int year = Integer.parseInt(dateSplits[0]);
        int month = Integer.parseInt(dateSplits[1]);
        int day = Integer.parseInt(dateSplits[2]);
        date = LocalDate.of(year, month, day);
        this.idPatient = Integer.parseInt(data[3]);
    }

}
