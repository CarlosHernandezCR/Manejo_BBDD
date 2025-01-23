package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MedicalRecords", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_MEDICALRECORDS", query = "from MedicalRecord ")
public class MedicalRecord {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idMedicalRecords")
    private int idMedicalRecords;
    @ManyToOne
    @JoinColumn(name = "idDoctor")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "idPatient")
    private Patient patient;
    @Basic
    @Column(name = "diagnoses")
    private String diagnoses;
    @Basic
    @Column(name = "admissionDate")
    private LocalDate admissionDate;
    @Transient
    private List<Prescribe> prescribes;
}
