package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Patient", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_PATIENTS", query = "from Patient ")
public class Patient {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idPatient")
    private int idPatient;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "dob")
    private Date dob;
    @Basic
    @Column(name = "phone")
    private String phone;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "patient")
    private Credential credential;

}
