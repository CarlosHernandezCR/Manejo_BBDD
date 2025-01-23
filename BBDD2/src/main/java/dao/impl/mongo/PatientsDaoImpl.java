package dao.impl.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import common.config.ConfigurationXML;
import common.constants.PatientsConstants;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.mongo.Patient;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PatientsDaoImpl {
    private final String urlMongo;
    private final String databaseName;
    private final String patientCollection;

    @Inject
    public PatientsDaoImpl(ConfigurationXML configurationXML) {
        this.urlMongo = configurationXML.getProperty("urlMongo");
        this.databaseName = configurationXML.getProperty("databaseName");
        this.patientCollection = configurationXML.getProperty("collection");
    }

    public Either<HospitalError, List<model.mongo.Patient>> getAll() {
        Either<HospitalError, List<Patient>> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> patientsCollection = database.getCollection(patientCollection);

            List<Patient> patients = new ArrayList<>();
            for (Document patientDocument : patientsCollection.find()) {
                patients.add(new Patient(patientDocument));
            }
            result = Either.right(patients);
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, PatientsConstants.ERROR_READING_FILE));
        }
        return result;
    }


    public Either<HospitalError, Patient> get(String name) {
        Either<HospitalError, Patient> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> patientsCollection = database.getCollection(patientCollection);

            Document patientDocument = patientsCollection.find(eq("name", name)).first();
            if (patientDocument == null) {
                result = Either.left(new HospitalError(0, PatientsConstants.PATIENT_NOT_FOUND));
            } else {
                Patient patient = new Patient(patientDocument);
                result = Either.right(patient);
            }
        } catch (Exception e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Integer> add(List<model.mongo.Patient> o) {
        Either<HospitalError, Integer> result;
        Integer count = 0;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);
            for (model.mongo.Patient patient : o) {
                count++;
                Document patientDocument = new Document("name", patient.getName())
                        .append("dob", patient.getDob())
                        .append("phone", patient.getPhone());
                if (!patient.getAppointments().isEmpty()) {
                    List<Document> appointmentsDocuments = new ArrayList<>();
                    for (model.mongo.Appointment appointment : patient.getAppointments()) {
                        Document appointmentDocument = new Document("date", appointment.getDate())
                                .append("idDoctor", appointment.getIdDoctor());
                        appointmentsDocuments.add(appointmentDocument);
                        count++;
                    }
                    patientDocument.append("Appointments", appointmentsDocuments);
                } else {
                    patientDocument.append("Appointments", new ArrayList<>());
                }
                if (!patient.getMedicalRecords().isEmpty()) {
                    List<Document> medicalRecordsDocuments = new ArrayList<>();
                    for (model.mongo.MedicalRecord medicalRecord : patient.getMedicalRecords()) {
                        count++;
                        Document medicalRecordDocument = new Document("idDoctor", medicalRecord.getIdDoctor())
                                .append("diagnoses", medicalRecord.getDiagnoses())
                                .append("admissionDate", medicalRecord.getAdmissionDate());
                        if (!medicalRecord.getPrescribes().isEmpty()) {
                            List<Document> prescribesDocuments = new ArrayList<>();
                            for (model.mongo.Prescribe prescribe : medicalRecord.getPrescribes()) {
                                Document prescribeDocument = new Document("name", prescribe.getName())
                                        .append("dosage", prescribe.getDosage());
                                prescribesDocuments.add(prescribeDocument);
                                count++;
                            }
                            medicalRecordDocument.append("Prescribes", prescribesDocuments);
                        }
                        medicalRecordsDocuments.add(medicalRecordDocument);
                    }
                    patientDocument.append("MedicalRecords", medicalRecordsDocuments);
                } else {
                    patientDocument.append("MedicalRecords", new ArrayList<>());
                }
                collection.insertOne(patientDocument);
            }
            result = Either.right(count);
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, PatientsConstants.ERROR_WRITING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Integer> delete(Patient o) {
        Either<HospitalError, Integer> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);
            collection.deleteOne(eq("name", o.getName()));
            result = Either.right(1);
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, PatientsConstants.ERROR_WRITING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Integer> update(model.mongo.Patient patient) {
        Either<HospitalError, Integer> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);

            Document patientDocument = new Document("name", patient.getName())
                    .append("dob", patient.getDob())
                    .append("phone", patient.getPhone());

            if (!patient.getAppointments().isEmpty()) {
                List<Document> appointmentsDocuments = new ArrayList<>();
                for (model.mongo.Appointment appointment : patient.getAppointments()) {
                    Document appointmentDocument = new Document("date", appointment.getDate())
                            .append("idDoctor", appointment.getIdDoctor());
                    appointmentsDocuments.add(appointmentDocument);
                }
                patientDocument.append("Appointments", appointmentsDocuments);
            } else {
                patientDocument.append("Appointments", new ArrayList<>());
            }

            if (!patient.getMedicalRecords().isEmpty()) {
                List<Document> medicalRecordsDocuments = new ArrayList<>();
                for (model.mongo.MedicalRecord medicalRecord : patient.getMedicalRecords()) {
                    Document medicalRecordDocument = new Document("idDoctor", medicalRecord.getIdDoctor())
                            .append("diagnoses", medicalRecord.getDiagnoses())
                            .append("admissionDate", medicalRecord.getAdmissionDate());

                    if (!medicalRecord.getPrescribes().isEmpty()) {
                        List<Document> prescribesDocuments = new ArrayList<>();
                        for (model.mongo.Prescribe prescribe : medicalRecord.getPrescribes()) {
                            Document prescribeDocument = new Document("name", prescribe.getName())
                                    .append("dosage", prescribe.getDosage());
                            prescribesDocuments.add(prescribeDocument);
                        }
                        medicalRecordDocument.append("Prescribes", prescribesDocuments);
                    }
                    medicalRecordsDocuments.add(medicalRecordDocument);
                }
                patientDocument.append("MedicalRecords", medicalRecordsDocuments);
            } else {
                patientDocument.append("MedicalRecords", new ArrayList<>());
            }
            collection.replaceOne(eq("name", patient.getName()), patientDocument);
            result = Either.right(1);
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, PatientsConstants.ERROR_WRITING_FILE));
        }
        return result;
    }
}
