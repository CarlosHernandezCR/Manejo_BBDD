package model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private String id;
    private String name;
    private String speciality;
    private String phone;

    public Doctor(Document doctorDocument) {
        this.id = doctorDocument.getString("id");
        this.name = doctorDocument.getString("name");
        this.speciality = doctorDocument.getString("specialty");
        this.phone = doctorDocument.getString("phone");
    }
}
