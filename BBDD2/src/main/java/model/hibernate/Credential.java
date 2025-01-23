package model.hibernate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Credential", schema = "CarlosHernandez_Hospital")
@NamedQuery(name = "HQL_GET_ALL_CREDENTIALS", query = "from Credential ")
public class Credential {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idCredential")
    private int idCredential;
    @Basic
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "password")
    private String password;
    @OneToOne
    @JoinColumn(name = "idPatient")
    private Patient patient;

}
