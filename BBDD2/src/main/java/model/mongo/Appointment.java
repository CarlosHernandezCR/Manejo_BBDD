package model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private String idDoctor;
    private String date;

    public Appointment(model.hibernate.Appointment appointment) {
        this.date = appointment.getDate().toString();
        this.idDoctor = String.valueOf(appointment.getDoctor().getIdDoctor());
    }

    public Appointment(Document appointmentDocument) {
        this.idDoctor = appointmentDocument.getString("idDoctor");
        this.date = appointmentDocument.getString("date");
    }
}
