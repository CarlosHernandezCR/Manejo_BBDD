package dao.impl.mongo;

import com.google.gson.Gson;
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

public class PatientsMongoGson {
    private String urlMongo;
    private String databaseName;
    private String patientCollection;
    private Gson gson;

    @Inject
    public PatientsMongoGson(ConfigurationXML configurationXML) {
        this.urlMongo = configurationXML.getProperty("urlMongo");
        this.databaseName = configurationXML.getProperty("databaseName");
        this.patientCollection = configurationXML.getProperty("collection");
        this.gson = new Gson();
    }

    public Either<HospitalError, List<model.mongo.Patient>> getAll() {
        Either<HospitalError, List<Patient>> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> patientsCollection = database.getCollection(patientCollection);

            List<Patient> patients = new ArrayList<>();
            for (Document patientDocument : patientsCollection.find()) {
                Patient patient = gson.fromJson(patientDocument.toJson(), Patient.class);
                patients.add(patient);
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
                Patient patient = gson.fromJson(patientDocument.toJson(), Patient.class);
                result = Either.right(patient);
            }
        } catch (Exception e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Integer> add(Patient patient) {
        Either<HospitalError, Integer> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);

            Document patientDocument = Document.parse(gson.toJson(patient));
            collection.insertOne(patientDocument);

            result = Either.right(1);
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, PatientsConstants.ERROR_WRITING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Integer> add(List<Patient> patient) {
        for (Patient p : patient) {
            add(p);
        }
        return Either.right(patient.size());
    }

    public Either<HospitalError, Integer> delete(Patient patient) {
        Either<HospitalError, Integer> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(patientCollection);
            collection.deleteOne(eq("name", patient.getName()));
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

            Document patientDocument = Document.parse(gson.toJson(patient));
            collection.replaceOne(eq("name", patient.getName()), patientDocument);
            result = Either.right(1);
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, PatientsConstants.ERROR_WRITING_FILE));
        }
        return result;
    }
}
