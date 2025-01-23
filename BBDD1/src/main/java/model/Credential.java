package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credential {
    private int id;
    private int idPatient;
    private String username;
    private String password;
    public Credential(String fileLine) {
        String[] data = fileLine.split(";");
        this.id = Integer.parseInt(data[0]);
        this.idPatient = Integer.parseInt(data[1]);
        this.username = data[2];
        this.password = data[3];
    }
    public String toFileLine() {
        return id+";"+idPatient+";"+username + ";" + password;
    }
}
