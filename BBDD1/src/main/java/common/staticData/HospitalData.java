package common.staticData;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HospitalData {
    public static List<Appointment> appointments = new ArrayList<>();
    public static List<Credential> credentials = new ArrayList<>();
    public static List<Doctor> doctors = new ArrayList<>();
    public static List<MedicalRecord> medicalRecords = new ArrayList<>();
    public static List<Patient> patients = new ArrayList<>();
    public static List<Payment> payments = new ArrayList<>();
    public static List<Prescribe> prescriptions = new ArrayList<>();

    static {
        appointments.add(new Appointment(1, LocalDate.parse("2023-01-20"), 1, 1));
        appointments.add(new Appointment(2, LocalDate.parse("2023-02-15"), 2, 2));
        appointments.add(new Appointment(3, LocalDate.parse("2023-03-30"), 3, 3));
        appointments.add(new Appointment(4, LocalDate.parse("2023-03-30"), 2, 1));


        credentials.add(new Credential(1, 1, "marcos", "marcos"));
        credentials.add(new Credential(2, 2, "matias", "matias"));
        credentials.add(new Credential(3, 3,"noelia", "noelia"));

        doctors.add(new Doctor(1,"Carlos", "cardiologist", "111111111"));
        doctors.add(new Doctor(2,"Jose", "physiotherapist", "222222222"));
        doctors.add(new Doctor(3,"Victor", "psychologist", "333333333"));

        medicalRecords.add(new MedicalRecord(1, 1, 1, "Hypertension", LocalDate.parse("2023-01-10"), new ArrayList<>()));
        medicalRecords.add(new MedicalRecord(4, 1, 1, "Hypertension", LocalDate.parse("2023-01-10"), new ArrayList<>()));
        medicalRecords.add(new MedicalRecord(2, 2, 2, "Flu", LocalDate.parse("2023-02-05"), new ArrayList<>()));
        medicalRecords.add(new MedicalRecord(3, 3, 3, "Pregnancy", LocalDate.parse("2023-03-20"), new ArrayList<>()));

        patients.add(new Patient(1, "Marcos M", "5551234567",LocalDate.parse("1990-05-15") ));
        patients.add(new Patient(2, "Matias more","5259876543", LocalDate.parse("1985-08-20")));
        patients.add(new Patient(3, "Noelia Arrue","5955678901", LocalDate.parse("1978-02-10")));
        patients.add(new Patient(4, "Carlos Hernandez","777777777", LocalDate.parse("2002-12-06")));

        payments.add(new Payment(1, LocalDate.parse("2023-01-15"), 100, 1));
        payments.add(new Payment(2, LocalDate.parse("2023-02-10"), 50, 2));
        payments.add(new Payment(3, LocalDate.parse("2023-03-25"), 200, 3));
        payments.add(new Payment(4, LocalDate.parse("2023-01-01"), 150, 1));

        prescriptions.add(new Prescribe(1, "Aspirin", "1 tablet every 6 hours", 1));
        prescriptions.add(new Prescribe(2, "Cough Syrup", "2 teaspoons every 8 hours", 2));
        prescriptions.add(new Prescribe(3, "Folic Acid", "1 tablet daily", 3));
        prescriptions.add(new Prescribe(4, "Paracetamol", "1 pill every 8 hours", 1));
        prescriptions.add(new Prescribe(5, "Ibuprofeno", "1 pill every 6 hours", 1));
        prescriptions.add(new Prescribe(6, "Strepsils", "1 pill every 4 hours", 2));
    }
}

