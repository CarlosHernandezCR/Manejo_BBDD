package model.mongo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    private String idDoctor;
    private String diagnoses;
    private String admissionDate;
    @SerializedName("Prescribes")
    private List<Prescribe> prescribes;

    public MedicalRecord(model.hibernate.MedicalRecord medicalRecord) {
        idDoctor = String.valueOf(medicalRecord.getDoctor().getIdDoctor());
        diagnoses = medicalRecord.getDiagnoses();
        admissionDate = medicalRecord.getAdmissionDate().toString();
        prescribes = new ArrayList<>();
    }

    public MedicalRecord(Document medicalRecordDocument) {
        idDoctor = medicalRecordDocument.getString("idDoctor");
        diagnoses = medicalRecordDocument.getString("diagnoses");
        admissionDate = medicalRecordDocument.getString("admissionDate");
        List<Document> prescribesDocs = (List<Document>) medicalRecordDocument.get("Prescribes");
        prescribes = new ArrayList<>();
        if (prescribesDocs != null) {
            for (Document prescribeDoc : prescribesDocs) {
                Prescribe prescribe = new Prescribe(prescribeDoc);
                prescribes.add(prescribe);
            }
        }
    }
}
