package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MedicalRecord {
    private int id;
    private int idPatient;
    private int idDoctor;
    private String diagnoses;
    private LocalDate admissionDate;
    private List<Prescribe> prescribes;

    public MedicalRecord(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        this.idPatient = Integer.parseInt(data[1]);
        this.idDoctor = Integer.parseInt(data[2]);
        this.diagnoses = data[3];
        String[] dateSplits = data[4].split("-");
        int year = Integer.parseInt(dateSplits[0]);
        int month = Integer.parseInt(dateSplits[1]);
        int day = Integer.parseInt(dateSplits[2]);
        admissionDate = LocalDate.of(year, month, day);
        this.prescribes=new ArrayList<>();

    }

    public MedicalRecord(ResultSet rs) {
        try {
            this.id = rs.getInt("idMedicalRecords");
            this.idPatient = rs.getInt("idPatient");
            this.idDoctor = rs.getInt("idDoctor");
            this.diagnoses = rs.getString("diagnoses");
            this.admissionDate = rs.getDate("admissionDate").toLocalDate();
            this.prescribes=new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toFileLine() {
        return id + ";" + idPatient + ";" + idDoctor + ";" + diagnoses + ";" + admissionDate.toString();
    }
}
