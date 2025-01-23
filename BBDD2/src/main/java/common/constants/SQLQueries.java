package common.constants;

public class SQLQueries {
    private SQLQueries() {
    }
    public static final String GETCREDENTIALS = "SELECT * FROM Credential WHERE username=?";
    public static final String GET_ALL_PATIENTS = "SELECT * FROM Patient";
    public static final String GET_ALL_MEDICALRECORDS_WITH_IDPATIENT = "SELECT * FROM MedicalRecords WHERE idPatient = ?";
    public static final String GET_ALL_PRESCRIBES_WITH_IDMEDICALRECORD = "SELECT * FROM Prescribe WHERE idMedicalRecords = ?";
    public static final String GET_PATIENT_BY_ID = "SELECT * FROM Patient WHERE idPatient = ?";
    public static final String GET_ALL_PAYMENT_WITH_IDPATIENT = "SELECT * FROM Payment WHERE idPatient = ?";
    public static final String INSERT_MEDICAL_RECORD = "INSERT INTO MedicalRecords (idDoctor,idPatient, diagnoses,admissionDate) VALUES (?,?, ?, ?)";
    public static final String GET_ALL_DOCTORS = "SELECT * FROM Doctor";
    public static final String GET_DOCTOR_BY_ID = "SELECT * FROM Doctor WHERE idDoctor = ?";
    public static final String INSERT_PRESCRIBE = "INSERT INTO Prescribe (name, dossages,idMedicalRecords) VALUES (?, ?, ?)";
    public static final String DELETE_PRESCRIBES_WITH_IDMEDICALRECORD = "DELETE FROM Prescribe WHERE idMedicalRecords = ?";
    public static final String DELETE_MEDICALRECORDS_WITH_IDPATIENT = "DELETE FROM MedicalRecords WHERE idPatient = ?";
    public static final String DELETE_CREDENTIALS_WITH_IDPATIENT = "DELETE FROM Credential WHERE idPatient = ?";
    public static final String DELETE_PATIENT_WITH_ID = "DELETE FROM Patient WHERE idPatient = ?";
    public static final String DELETE_PAYMENTS_WITH_IDPATIENT = "DELETE FROM Payment WHERE idPatient = ?";
    public static final String DELETE_APPOINTMENTS_WITH_IDPATIENT = "DELETE FROM Appointment WHERE idPatient = ?";
    public static final String GET_PATIENT_WITH_MOST_MEDICAL_RECORDS = "SELECT " +
            "p." + "name" + ", " +
            "p." + "phone" + ", " +
            "COUNT(mr." + "idMedicalRecords" + ") AS " + "medicalRecordsCount" + " " +
            "FROM " + "Patient" + " p " +
            "JOIN " + "MedicalRecords" + " mr ON p." + "idPatient" + " = mr." + "idPatient" + " " +
            "GROUP BY p." + "idPatient" + " " +
            "ORDER BY " + "medicalRecordsCount" + " DESC " +
            "LIMIT 1";
    public static final String GET_DATE_WITH_MORE_PATIENTS = "SELECT " +
            "DATE(a.date) AS date, " +
            "COUNT(a.idAppointment) AS appointmentsCount " +
            "FROM Appointment a " +
            "GROUP BY date " +
            "ORDER BY appointmentsCount DESC " +
            "LIMIT 1";
    public static final String GET_ALL_MEDICALRECORDS = "SELECT * FROM MedicalRecords";
    public static final String GET_NEWEST_MR_BY_PATIENT = "SELECT * FROM MedicalRecords WHERE idPatient = ? ORDER BY admissionDate DESC LIMIT 1";
    public static final String GET_PRESCRIBE_WITH_ID = "SELECT * FROM Prescribe WHERE idPrescribe = ?";
    public static final String UPDATE_PRESCRIBE = "UPDATE Prescribe SET name = ?, dossages = ? WHERE idPrescribe = ?";
    public static final String GET_ALL_MEDICALRECORDS_ASC = "SELECT * FROM MedicalRecords ORDER BY admissionDate ASC";
    public static final String GET_ALL_MEDICALRECORDS_DESC = "SELECT * FROM MedicalRecords ORDER BY admissionDate DESC";
    public static final String DELETE_MEDICALRECORDS_WITH_ID = "DELETE FROM MedicalRecords WHERE idMedicalRecords = ?";
    public static final String GET_PATIENTS_BY_MEDICATION = "SELECT p.*" +
            "FROM Patient p JOIN MedicalRecords mr ON p.idPatient = mr.idPatient JOIN Prescribe pr ON mr.idMedicalRecords = pr.idMedicalRecords " +
            "WHERE pr.name = ? AND pr.dossages = ? GROUP BY p.idPatient";
    public static final String GET_PATIENTS_AND_MEDICATIONS = "SELECT p.idPatient, p.name, COUNT(pr.name) AS medicationsCount " +
            "FROM Patient p JOIN MedicalRecords mr ON p.idPatient = mr.idPatient JOIN Prescribe pr ON mr.idMedicalRecords = pr.idMedicalRecords " +
            "GROUP BY p.idPatient, p.name";
    public static final String GET_FROM_YEAR_TO_PREVIOUS = "SELECT * FROM MedicalRecords WHERE YEAR(admissionDate) < ?";
    public static final String ERROR_GETTING_PATIENT_WITH_MOST_MEDICAL_RECORDS = "Error getting patient with most medical records";
    public static final String ERROR_GETTING_APPOINTMENT_WITH_MOST_PATIENTS = "Error getting appointment with most patients";
}