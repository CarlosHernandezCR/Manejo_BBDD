package service;

import dao.impl.mongo.AggregationsDao;
import jakarta.inject.Inject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class AggregationsService {
    private final AggregationsDao aggregationsDao;

    @Inject
    public AggregationsService(AggregationsDao aggregationsDao) {
        this.aggregationsDao = aggregationsDao;
    }

    public String getMedicationWithHighestDosage() {
        return toString(aggregationsDao.getMedicationWithHighestDosage());
    }

    public String getMedicalRecordsOfPatient(String patientName) {
        return toString(aggregationsDao.getMedicalRecordsOfPatient(patientName));
    }

    public String getNumberOfMedications() {
        return toString(aggregationsDao.getNumberOfMedications());
    }

    public String getPatientsPrescribedWithAmoxicilina() {
        List<Document> listWithDuplicates = aggregationsDao.getPatientsPrescribedWithAmoxicilina();
        LinkedHashSet<Document> setWithoutDuplicates = new LinkedHashSet<>(listWithDuplicates);
        ArrayList<Document> listWithoutDuplicates = new ArrayList<>(setWithoutDuplicates);
        return toString(listWithoutDuplicates);
    }

    public String getAverageNumberOfMedications() {
        return toString(aggregationsDao.getAverageNumberOfMedications());
    }

    public String getMostPrescribedMedication() {
        return toString(aggregationsDao.getMostPrescribedMedication());
    }

    public String getMedicationsOfPatients() {
        return toString(aggregationsDao.getMostPrescribedMedicationOfEachPatient());
    }

    public String getMostPrescribedMedicationPerPatient() {
        return toString(aggregationsDao.getMostPrescribedMedicationPerPatient());
    }

    public String getDoctorsWithoutPatients() {
        return toString(aggregationsDao.getDoctorsWithoutPatients());
    }

    public String getDoctorsWithMostPatients() {
        return toString(aggregationsDao.getDoctorWithMorePatients());
    }

    public String getPatientsWithoutMedications() {
        return toString(aggregationsDao.getPatientWithMoreMedicalRecords());
    }

    private String toString(List<Document> documents) {
        StringBuilder result = new StringBuilder();
        for (Document document : documents) {
            result.append(document.toJson()).append("\n");
        }
        return result.toString();
    }
}
