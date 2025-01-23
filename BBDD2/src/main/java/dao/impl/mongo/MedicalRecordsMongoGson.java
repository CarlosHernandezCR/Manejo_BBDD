package dao.impl.mongo;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import common.config.ConfigurationXML;
import jakarta.inject.Inject;
import model.mongo.MedicalRecord;
import model.mongo.Patient;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;

public class MedicalRecordsMongoGson {
    private String urlMongo;
    private String databaseName;
    private String patientCollection;
    private Gson gson;

    @Inject
    public MedicalRecordsMongoGson(ConfigurationXML configurationXML) {
        this.urlMongo = configurationXML.getProperty("urlMongo");
        this.databaseName = configurationXML.getProperty("databaseName");
        this.patientCollection = configurationXML.getProperty("collection");
        this.gson = new Gson();
    }

    public void update(Patient newPatient) {
        String patientId = newPatient.getId().toString();
        Patient oldPatient = get(patientId);

        for (MedicalRecord newRecord : newPatient.getMedicalRecords()) {
            if (!oldPatient.getMedicalRecords().contains(newRecord)) {
                add(patientId, newRecord);
            }
        }

        for (MedicalRecord oldRecord : oldPatient.getMedicalRecords()) {
            if (!newPatient.getMedicalRecords().contains(oldRecord)) {
                delete(patientId, oldRecord);
            }
        }
    }

    public Patient get(String patientId) {
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);

            Document patientDocument = collection.find(eq("id", patientId)).first();
            return gson.fromJson(patientDocument.toJson(), Patient.class);
        }
    }

    public void add(String patientId, MedicalRecord newRecord) {
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);

            Document newRecordDocument = Document.parse(gson.toJson(newRecord));
            collection.updateOne(eq("id", patientId), push("MedicalRecords", newRecordDocument));
        }
    }

    public void delete(String patientId, MedicalRecord recordToDelete) {
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);

            Document recordDocument = Document.parse(gson.toJson(recordToDelete));
            collection.updateOne(eq("id", patientId), pull("MedicalRecords", recordDocument));
        }
    }

}
