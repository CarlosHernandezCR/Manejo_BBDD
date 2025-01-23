package model.XML;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "MedicalRecords")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListMedicalRecordsXML {
    @XmlElement(name = "medicalRecord")
    private List<MedicalRecordXML> medicalRecordXMLS;
    public ListMedicalRecordsXML() {
        medicalRecordXMLS = new ArrayList<>();
    }
    public List<MedicalRecordXML> getMedicalRecordXMLS() {
        return medicalRecordXMLS;
    }
}
