package dao.impl.hibernate;

import common.constants.SQLQueries;
import dao.JPAUtil;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import model.error.HospitalError;
import model.hibernate.Patient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueriesHibernate {
    private JPAUtil jpaUtil;
    private EntityManager em;

    @Inject
    public QueriesHibernate(JPAUtil jpaUtil) {
        this.jpaUtil = jpaUtil;
    }

    public Either<HospitalError, Patient> getPatientWithMostMedicalRecords() {
        try {
            em = jpaUtil.getEntityManager();
            Patient patient = em.createQuery("SELECT m.patient FROM MedicalRecord m GROUP BY m.patient.idPatient ORDER BY COUNT(m) DESC", Patient.class)
                    .setMaxResults(1)
                    .getSingleResult();
            return Either.right(patient);
        } catch (Exception e) {
            return Either.left(new HospitalError(1, SQLQueries.ERROR_GETTING_PATIENT_WITH_MOST_MEDICAL_RECORDS));
        }
    }

    public Either<HospitalError, LocalDate> getAppointmentWithMostPatients() {
        try {
            em = jpaUtil.getEntityManager();
            LocalDate date = em.createQuery("SELECT a.date FROM Appointment a GROUP BY a.date ORDER BY COUNT(DISTINCT a.patient) DESC", LocalDate.class)
                    .setMaxResults(1)
                    .getSingleResult();
            return Either.right(date);
        } catch (Exception e) {
            return Either.left(new HospitalError(1, SQLQueries.ERROR_GETTING_APPOINTMENT_WITH_MOST_PATIENTS));
        }
    }

    public Either<HospitalError, Map<Patient, Long>> getPatientsWithNumberOfMedications() {
        try {
            em = jpaUtil.getEntityManager();
            List<Object[]> results = em.createQuery("SELECT p.medicalRecord.patient, COUNT(p) FROM Prescribe p GROUP BY p.medicalRecord.patient")
                    .getResultList();
            Map<Patient, Long> patients = new HashMap<>();
            for (Object[] result : results) {
                patients.put((Patient) result[0], (Long) result[1]);
            }
            return Either.right(patients);
        } catch (Exception e) {
            return Either.left(new HospitalError(1, "Error getting patients with number of medications"));
        }
    }
}