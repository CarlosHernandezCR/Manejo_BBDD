package model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescribe {
    private String name;
    private String dosage;

    public Prescribe(model.hibernate.Prescribe prescribe) {
        name = prescribe.getName();
        dosage = prescribe.getDossages();
    }

    public Prescribe(Document prescribeDoc) {
        name = prescribeDoc.getString("name");
        dosage = prescribeDoc.getString("dosage");
    }
}
