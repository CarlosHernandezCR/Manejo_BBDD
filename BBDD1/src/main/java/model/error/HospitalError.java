package model.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HospitalError {
    private int error;
    private String message;
    private LocalDateTime date;

    public HospitalError(int error, String message) {
        this.error = error;
        this.message = message;
        this.date = LocalDateTime.now();
    }
}
