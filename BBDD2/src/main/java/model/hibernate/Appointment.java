package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Appointment", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_APPOINTMENT", query = "from Appointment ")
public class Appointment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idAppointment")
    private int idAppointment;
    @Basic
    @Column(name = "date")
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "idDoctor")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "idPatient")
    private Patient patient;

}
