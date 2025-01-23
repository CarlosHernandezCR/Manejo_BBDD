package model.XML;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "medicalRecord")
@XmlAccessorType(XmlAccessType.FIELD)
public class MedicalRecordXML {
    @XmlElement
    private String nameDoctor;
    @XmlElement
    private String diagnosis;
    @XmlElement(name = "prescribe")
    private List<PrescribeXML> prescribes;
}
