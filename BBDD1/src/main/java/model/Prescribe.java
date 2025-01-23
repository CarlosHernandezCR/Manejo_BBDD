package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Prescribe {
    private int id;
    private String name;
    private String dossages;
    private int idMedicalRecords;

    public Prescribe(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        this.name = data[1];
        this.dossages = data[2];
        this.idMedicalRecords = Integer.parseInt(data[3]);
    }

    public Prescribe(ResultSet rs) {
        try {
            this.id = rs.getInt("idPrescribe");
            this.name = rs.getString("name");
            this.dossages = rs.getString("dossages");
            this.idMedicalRecords = rs.getInt("idMedicalRecords");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Prescribe(String name, String dossages) {
        id=0;
        this.name = name;
        this.dossages = dossages;
        idMedicalRecords=0;
    }

    public String toFileLine() {
        return id + ";" + name + ";" + dossages + ";" + idMedicalRecords;
    }
}
