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
@Table(name = "Payment", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_PAYMENTS", query = "from Payment ")
public class Payment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idPayment")
    private int idPayment;
    @Basic
    @Column(name = "date")
    private Date date;
    @Basic
    @Column(name = "quantity")
    private Double quantity;
    @ManyToOne
    @JoinColumn(name = "idPatient")
    private Patient patient;


}
