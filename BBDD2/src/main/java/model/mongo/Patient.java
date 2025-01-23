package model.mongo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Patient {
    private ObjectId id;
    private String name;
    private String dob;
    private String phone;
    @SerializedName("Appointments")
    private List<Appointment> appointments;
    @SerializedName("MedicalRecords")
    private List<MedicalRecord> medicalRecords;

    public Patient(model.hibernate.Patient patient) {
        name = patient.getName();
        dob = patient.getDob().toString();
        phone = patient.getPhone();
        appointments = new ArrayList<>();
        medicalRecords = new ArrayList<>();
    }

    public Patient(Document patientDocument) {
        id = patientDocument.getObjectId("_id");
        name = patientDocument.getString("name");
        dob = patientDocument.getString("dob");
        phone = patientDocument.getString("phone");
        appointments = new ArrayList<>();
        List<Document> appointmentsDocuments = (List<Document>) patientDocument.get("Appointments");
        if (appointmentsDocuments != null) {
            for (Document appointmentDocument : appointmentsDocuments) {
                appointments.add(new Appointment(appointmentDocument));
            }
        }
        medicalRecords = new ArrayList<>();
        List<Document> medicalRecordsDocuments = (List<Document>) patientDocument.get("MedicalRecords");
        if (medicalRecordsDocuments != null) {
            for (Document medicalRecordDocument : medicalRecordsDocuments) {
                medicalRecords.add(new MedicalRecord(medicalRecordDocument));
            }
        }
    }
}
