package model;

import java.sql.ResultSet;

public class PatientUIV2 {
    private int id;
    private String name;
    private int totalMedications;

    public PatientUIV2(ResultSet rs) {
        try {
            this.id = rs.getInt("idPatient");
            this.name = rs.getString("name");
            this.totalMedications = rs.getInt("medicationsCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public String toString() {
        return "PatientUIV2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalMedications=" + totalMedications +
                '}';
    }
}
