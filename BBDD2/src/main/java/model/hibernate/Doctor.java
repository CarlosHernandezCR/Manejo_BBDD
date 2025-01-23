package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Doctor", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_DOCTORS", query = "from Doctor ")
public class Doctor {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idDoctor")
    private int idDoctor;
    @Basic
    @Column(name = "speciality")
    private String speciality;
    @Basic
    @Column(name = "phone")
    private String phone;
    @Basic
    @Column(name = "name")
    private String name;

    public model.mongo.Doctor toMongo() {
        return new model.mongo.Doctor(String.valueOf(idDoctor), name, speciality, phone);
    }

}
