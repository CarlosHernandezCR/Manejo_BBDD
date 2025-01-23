package dao.impl.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Field;
import common.config.ConfigurationXML;
import jakarta.inject.Inject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.size;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;

public class AggregationsDao {

    MongoCollection<Document> patientsCollection;
    MongoCollection<Document> doctorsCollection;

    @Inject
    public AggregationsDao(ConfigurationXML configurationXML) {
        MongoClient mongo = MongoClients.create(configurationXML.getProperty("urlMongo"));
        MongoDatabase db = mongo.getDatabase(configurationXML.getProperty("databaseName"));
        patientsCollection = db.getCollection(configurationXML.getProperty("collection"));
        doctorsCollection = db.getCollection(configurationXML.getProperty("collection2"));

    }

    public List<Document> getMedicationWithHighestDosage() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                sort(descending("dosage")),
                limit(1),
                project(fields(include("MedicalRecords.Prescribes.name", "MedicalRecords.Prescribes.dosage"), excludeId()))
        )).into(new ArrayList<>());
    }

    public List<Document> getMedicalRecordsOfPatient(String patientName) {
        return patientsCollection.aggregate(Arrays.asList(
                match(eq("name", patientName)),
                addFields(new Field<>("countMedicalRecords", new Document("$size", "$MedicalRecords"))),
                project(fields(include("name", "countMedicalRecords"), excludeId()))
        )).into(new ArrayList<>());
    }

    public List<Document> getNumberOfMedications() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                group("$name", sum("numberOfMedications", 1)),
                project(fields(include("name", "numberOfMedications")))
        )).into(new ArrayList<>());
    }

    public List<Document> getPatientsPrescribedWithAmoxicilina() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                match(eq("MedicalRecords.Prescribes.name", "Amoxicilina")),
                project(fields(include("name"), excludeId()))
        )).into(new ArrayList<>());
    }

    public List<Document> getAverageNumberOfMedications() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                group("$id", avg("averageNumberOfMedications", 1)),
                project(fields(include("averageNumberOfMedications"), excludeId()))
        )).into(new ArrayList<>());
    }

    public List<Document> getMostPrescribedMedication() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                group("$MedicalRecords.Prescribes.name", sum("count", 1)),
                sort(descending("count")),
                limit(1)
        )).into(new ArrayList<>());
    }

    public List<Document> getMostPrescribedMedicationOfEachPatient() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                group(new Document("patientName", "$name").append("medication", "$MedicalRecords.Prescribes.name"), sum("count", 1)),
                sort(descending("count")),
                group("$_id.patientName", first("mostPrescribedMedication", "$_id.medication"))
        )).into(new ArrayList<>());
    }

    public List<Document> getMostPrescribedMedicationPerPatient() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                unwind("$MedicalRecords.Prescribes"),
                group(new Document("patientName", "$name").append("medication", "$MedicalRecords.Prescribes.name"), sum("count", 1)),
                sort(descending("count")),
                limit(1)
        )).into(new ArrayList<>());
    }

    public List<Document> getDoctorsWithoutPatients() {
        return doctorsCollection.aggregate(Arrays.asList(
                lookup("Patients", "id", "Appointments.idDoctor", "patientsAppointments"),
                lookup("Patients", "id", "MedicalRecords.idDoctor", "patientsMedicalRecords"),
                addFields(new Field<>("patients", new Document("$concatArrays", Arrays.asList("$patientsAppointments", "$patientsMedicalRecords")))),
                match(size("patients", 0)),
                project(fields(include("name", "speciality", "phone"), excludeId()))
        )).into(new ArrayList<>());
    }

    public List<Document> getDoctorWithMorePatients() {
        return doctorsCollection.aggregate(Arrays.asList(
                lookup("Patients", "id", "Appointments.idDoctor", "patientsAppointments"),
                lookup("Patients", "id", "MedicalRecords.idDoctor", "patientsMedicalRecords"),
                unwind("$patientsAppointments"),
                unwind("$patientsMedicalRecords"),
                group("$_id", first("name", "$name"), sum("numberOfPatients", 1)),
                sort(descending("numberOfPatients")),
                limit(1),
                project(fields(include("name"), excludeId()))
        )).into(new ArrayList<>());
    }

    public List<Document> getPatientWithMoreMedicalRecords() {
        return patientsCollection.aggregate(Arrays.asList(
                unwind("$MedicalRecords"),
                group(new Document("id", "$_id").append("name", "$name"), sum("numberOfMedicalRecords", 1)),
                sort(descending("numberOfMedicalRecords")),
                limit(1),
                project(fields(include("id", "name", "numberOfMedicalRecords")))
        )).into(new ArrayList<>());
    }
}
