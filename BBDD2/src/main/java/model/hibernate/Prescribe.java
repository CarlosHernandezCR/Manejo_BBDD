package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Prescribe", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_PRESCRIBE", query = "from Prescribe ")
public class Prescribe {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idPrescribe")
    private int idPrescribe;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "dossages")
    private String dossages;
    @Column(name = "idMedicalRecords")
    private int idMedicalRecords;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idMedicalRecords", updatable = false, insertable = false)
    private MedicalRecord medicalRecord;

    public Prescribe(String name, String dossages) {
        this.name = name;
        this.dossages = dossages;
    }

    public Prescribe(int idPrescribe, String name, String dossages, int idMedicalRecords) {
        this.idPrescribe = idPrescribe;
        this.name = name;
        this.dossages = dossages;
        this.idMedicalRecords = idMedicalRecords;
    }
}
