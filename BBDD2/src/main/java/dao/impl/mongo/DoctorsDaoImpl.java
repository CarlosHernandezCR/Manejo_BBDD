package dao.impl.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import common.config.ConfigurationXML;
import common.constants.DoctorConstants;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.mongo.Doctor;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class DoctorsDaoImpl {
    private String urlMongo;
    private String databaseName;
    private String doctorsCollection;

    @Inject
    public DoctorsDaoImpl(ConfigurationXML configurationXML) {
        this.urlMongo = configurationXML.getProperty("urlMongo");
        this.databaseName = configurationXML.getProperty("databaseName");
        this.doctorsCollection = configurationXML.getProperty("collection2");
    }

    public Either<HospitalError, List<Doctor>> getAll() {
        return null;
    }

    public Either<HospitalError, Doctor> get(int id) {
        String idString = String.valueOf(id);
        Either<HospitalError, Doctor> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> doctorCollection = database.getCollection(doctorsCollection);

            Document doctorDocument = doctorCollection.find(eq("id", idString)).first();
            if (doctorDocument == null) {
                result = Either.left(new HospitalError(0, DoctorConstants.DOCTOR_NOT_FOUND));
            } else {
                Doctor patient = new Doctor(doctorDocument);
                result = Either.right(patient);
            }
        } catch (Exception e) {
            result = Either.left(new HospitalError(1, DoctorConstants.ERROR_READING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Doctor> get(String name) {
        Either<HospitalError, Doctor> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> doctorCollection = database.getCollection(doctorsCollection);

            Document doctorDocument = doctorCollection.find(eq("name", name)).first();
            if (doctorDocument == null) {
                result = Either.left(new HospitalError(0, DoctorConstants.DOCTOR_NOT_FOUND));
            } else {
                Doctor patient = new Doctor(doctorDocument);
                result = Either.right(patient);
            }
        } catch (Exception e) {
            result = Either.left(new HospitalError(1, DoctorConstants.ERROR_READING_FILE));
        }
        return result;
    }

    public Either<HospitalError, Integer> add(List<model.mongo.Doctor> doctors) {
        Either<HospitalError, Integer> result;
        try (MongoClient mongoClient = MongoClients.create(urlMongo)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(doctorsCollection);
            for (model.mongo.Doctor doctor : doctors) {
                Document doctorDocument = new Document("id", doctor.getId())
                        .append("name", doctor.getName())
                        .append("specialty", doctor.getSpeciality())
                        .append("phone", doctor.getPhone());
                collection.insertOne(doctorDocument);
            }
            result = Either.right(doctors.size());
        } catch (Exception e) {
            result = Either.left(new HospitalError(0, DoctorConstants.ERROR_WRITING_FILE));
        }
        return result;
    }
}
