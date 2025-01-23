package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Prescribe", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_PRESCRIBE",query = "from Prescribe ")
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
    @ManyToOne
    @JoinColumn(name = "idMedicalRecords")
    @Column(name = "idMedicalRecords")
    private MedicalRecord medicalRecords;
}
