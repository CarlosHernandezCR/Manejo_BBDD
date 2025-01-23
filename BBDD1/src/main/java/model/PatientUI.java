package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PatientUI {
    private int id;
    private String name;
    private Double totalPayments;

    public PatientUI(Patient patient) {
        this.id = patient.getId();
        this.name = patient.getName();
        totalPayments=0.0;
    }
}
