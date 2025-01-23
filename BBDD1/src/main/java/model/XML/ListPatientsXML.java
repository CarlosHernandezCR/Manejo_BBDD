package model.XML;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name = "patients")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
@Data
public class ListPatientsXML {
    @XmlElement(name = "patient")
    private List<PatientXML> patients;

}

