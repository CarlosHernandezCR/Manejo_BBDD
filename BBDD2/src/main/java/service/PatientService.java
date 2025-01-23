package service;

import dao.*;
import dao.impl.hibernate.*;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.hibernate.Credential;
import model.hibernate.Patient;
import model.hibernate.Payment;
import model.mongo.Doctor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PatientService {
    PatientsDao patientsDaoHibernate;
    dao.impl.mongo.PatientsMongoGson patientsDaoMongo;
    MedicalRecordsDao medicalRecordsDao;
    PrescribeDao prescribeDao;
    PaymentDao paymentDaoImpl;
    QueriesHibernate queriesHibernate;
    AppointmentDao appointmentsDao;
    CredentialsDao credentialsDao;
    dao.impl.mongo.DoctorsDaoImpl doctordao;

    @Inject
    public PatientService(PatientsDaoImpl patientsDaoHibernate, MedicalRecordsDaoImpl medicalRecordsDao,
                          PrescribeDaoImpl prescribeDao, PaymentDaoImpl paymentDaoImpl, QueriesHibernate queriesHibernate,
                          AppointmentDaoImpl appointmentsDao, dao.impl.mongo.PatientsMongoGson patientsDaoMongo,
                          CredentialsDaoImpl credentialsDao, dao.impl.mongo.DoctorsDaoImpl doctordao) {
        this.patientsDaoHibernate = patientsDaoHibernate;
        this.medicalRecordsDao = medicalRecordsDao;
        this.prescribeDao = prescribeDao;
        this.paymentDaoImpl = paymentDaoImpl;
        this.queriesHibernate = queriesHibernate;
        this.appointmentsDao = appointmentsDao;
        this.patientsDaoMongo = patientsDaoMongo;
        this.credentialsDao = credentialsDao;
        this.doctordao = doctordao;
    }


    public Either<HospitalError, Map<Patient, Double>> getTotalAmountPaidByPatient() {
        Either<HospitalError, List<Payment>> data = paymentDaoImpl.getAll();
        if (data.isLeft()) {
            return Either.left(data.getLeft());
        }
        Map<Patient, Double> payments = data.get().stream()
                .collect(Collectors.groupingBy(Payment::getPatient, Collectors.summingDouble(Payment::getQuantity)));

        Map<Patient, Double> sortedPayments = payments.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return Either.right(sortedPayments);
    }

    public Either<HospitalError, Patient> getPatientWithMostMedicalRecords() {
        return queriesHibernate.getPatientWithMostMedicalRecords();
    }

    public Either<HospitalError, Map<Patient, Long>> getPatientsWithNumberOfMedications() {
        return queriesHibernate.getPatientsWithNumberOfMedications();
    }

    public Either<HospitalError, List<Patient>> getAll() {
        return patientsDaoHibernate.getAll();
    }

    public Either<HospitalError, Integer> addPatient(String name, LocalDate date, String phone, String username, String password) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setDob(Date.valueOf(date));
        patient.setPhone(phone);
        patient.setCredential(new Credential(0, username, password, patient));
        return patientsDaoHibernate.add(patient);

    }

    public Either<HospitalError, Integer> updatePatient(Patient patient) {
        return patientsDaoHibernate.update(patient);
    }

    public Either<HospitalError, Integer> deletePatient(int id) {
        Either<HospitalError, Patient> patientResult = patientsDaoHibernate.get(id);
        if (patientResult.isRight()) {
            return patientsDaoHibernate.delete(patientResult.get());
        } else {
            return Either.left(patientResult.getLeft());
        }
    }

    public Either<HospitalError, Integer> importDataFromMySQLToMongo() {
        Either<HospitalError, List<model.hibernate.Patient>> data = patientsDaoHibernate.getAll();
        List<model.mongo.Patient> patients = new ArrayList<>();
        if (data.isLeft()) {
            return Either.left(data.getLeft());
        }
        for (model.hibernate.Patient patient : data.get()) {
            patients.add(new model.mongo.Patient(patient));
            Either<HospitalError, List<model.hibernate.Appointment>> data2 = appointmentsDao.getAll();
            if (data2.isLeft()) {
                return Either.left(data2.getLeft());
            }
            for (model.hibernate.Appointment appointment : data2.get()) {
                if (appointment.getPatient().getIdPatient() == patient.getIdPatient()) {
                    patients.get(patients.size() - 1).getAppointments().add(new model.mongo.Appointment(appointment));
                }
            }
            Either<HospitalError, List<model.hibernate.MedicalRecord>> data3 = medicalRecordsDao.getAll();
            if (data3.isLeft()) {
                return Either.left(data3.getLeft());
            }
            for (model.hibernate.MedicalRecord medicalRecord : data3.get()) {
                if (medicalRecord.getPatient().getIdPatient() == patient.getIdPatient()) {
                    patients.get(patients.size() - 1).getMedicalRecords().add(new model.mongo.MedicalRecord(medicalRecord));
                    Either<HospitalError, List<model.hibernate.Prescribe>> data4 = prescribeDao.getAll();
                    if (data4.isLeft()) {
                        return Either.left(data4.getLeft());
                    }
                    for (model.hibernate.Prescribe prescribe : data4.get()) {
                        if (prescribe.getMedicalRecord().getIdMedicalRecords() == medicalRecord.getIdMedicalRecords()) {
                            patients.get(patients.size() - 1).getMedicalRecords().get(patients.get(patients.size() - 1).getMedicalRecords().size() - 1).getPrescribes().add(new model.mongo.Prescribe(prescribe));
                        }
                    }
                }
            }

        }
        return patientsDaoMongo.add(patients);
    }

    public Either<HospitalError, List<model.mongo.Patient>> getAllMongo() {
        return patientsDaoMongo.getAll();
    }

    public Either<HospitalError, Integer> addPatientMongo(model.mongo.Patient patient, String username, String password) {
        List<model.mongo.Patient> patients = new ArrayList<>();
        patients.add(patient);
        if (patientsDaoMongo.add(patients).isRight()) {
            return credentialsDao.add(new Credential(0, username, password, null));
        } else return Either.left(new HospitalError(1, "Error adding patient"));
    }

    public Either<HospitalError, Integer> updatePatientMongo(model.mongo.Patient patient) {
        return patientsDaoMongo.update(patient);

    }

    public Either<HospitalError, model.mongo.Patient> getPatientMongo(String name) {
        return patientsDaoMongo.get(name);
    }

    public Either<HospitalError, Integer> deletePatientMongo(String name, boolean confirm) {
        Either<HospitalError, model.mongo.Patient> patientResult = patientsDaoMongo.get(name);
        if (patientResult.isRight()) {
            if (confirm) {
                credentialsDao.delete(credentialsDao.get(name).get());
                return patientsDaoMongo.delete(patientResult.get());
            }
            if (!patientResult.get().getMedicalRecords().isEmpty()) {
                return Either.left(new HospitalError(33, "Patient has medical records"));
            } else {
                credentialsDao.delete(credentialsDao.get(name).get());
                return patientsDaoMongo.delete(patientResult.get());
            }
        } else {
            return Either.left(patientResult.getLeft());
        }
    }

    public Either<HospitalError, model.mongo.Patient> getMedicationsOfPatient(String name) {
        return patientsDaoMongo.get(name);
    }

    public Either<HospitalError, Set<Doctor>> getDoctorsOfPatient(String name) {
        Set<Doctor> doctors = new HashSet<>();
        Either<HospitalError, model.mongo.Patient> patientResult = patientsDaoMongo.get(name);
        if (patientResult.isRight()) {
            for (model.mongo.MedicalRecord medicalRecord : patientResult.get().getMedicalRecords()) {
                doctors.add(doctordao.get(Integer.parseInt(medicalRecord.getIdDoctor())).get());
            }
            for (model.mongo.Appointment appointment : patientResult.get().getAppointments()) {
                doctors.add(doctordao.get(Integer.parseInt(appointment.getIdDoctor())).get());
            }
            return Either.right(doctors);
        } else {
            return Either.left(patientResult.getLeft());
        }
    }
}