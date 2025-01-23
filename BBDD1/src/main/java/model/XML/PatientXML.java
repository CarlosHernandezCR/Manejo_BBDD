package model.XML;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@XmlRootElement(name = "patient")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientXML {
    @XmlElement
    private int id;
    @XmlElement
    private String name;
    @XmlElement
    private String phone;
    @XmlElement
    private String dob;
    @XmlElementWrapper(name="MedicalRecords")
    @XmlElement(name = "medicalRecord")
    private List<MedicalRecordXML> medicalRecords;
}
//<!-- Ejemplo de un archivo XML que contiene datos de pacientes, historiales médicos y prescripciones -->
//
//<patients>
//    <patient>
//        <id>1</id>
//        <name>John Doe</name>
//        <phone>123-456-7890</phone>
//        <dob>1990-01-01</dob>
//        <medicalRecords>
//            <medicalRecord>
//                <nameDoctor>Dr. Smith</nameDoctor>
//                <diagnosis>Cold</diagnosis>
//                <prescribes>
//                    <prescribe>
//                        <name>Aspirin</name>
//                        <dosage>1 tablet every 4 hours</dosage>
//                    </prescribe>
//                    <!-- Otros prescripciones aquí -->
//                </prescribes>
//            </medicalRecord>
//            <!-- Otros historiales médicos aquí -->
//        </medicalRecords>
//    </patient>
//    <!-- Otros pacientes aquí -->
//</patients>
