package service;

import common.constants.MedicalRecordConstants;
import common.constants.PatientsConstants;
import dao.*;
import dao.impl.JDBC.PaymentDaoImpl;
import dao.impl.JDBC.QueriesDao;
import dao.impl.Spring.PatientsDaoImpl;
import dao.impl.TXT.DoctorsDaoImpl;
import dao.impl.TXT.MedicalRecordsDaoImpl;
import dao.impl.TXT.PrescribeDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.*;
import model.XML.ListPatientsXML;
import model.XML.MedicalRecordXML;
import model.XML.PatientXML;
import model.XML.PrescribeXML;
import model.error.HospitalError;

import java.util.ArrayList;
import java.util.List;

public class PatientService {
    PatientsDao patientsDaoImpl;
    PatientsDao patientsDaoTXT;
    PatientsDao patientsDaoXML;
    MedicalRecordsDao medicalRecordsDao;
    PrescribeDao prescribeDao;
    PaymentDao paymentDaoImpl;
    QueriesDao queriesDao;
    @Inject
    public PatientService(PatientsDaoImpl patientsDaoImpl, MedicalRecordsDaoImpl medicalRecordsDao, PrescribeDaoImpl prescribeDao, PaymentDaoImpl paymentDaoImpl, QueriesDao queriesDao, PatientsDao patientsDaoTXT, PatientsDao patientsDaoXML) {
        this.patientsDaoImpl = patientsDaoImpl;
        this.medicalRecordsDao = medicalRecordsDao;
        this.prescribeDao = prescribeDao;
        this.paymentDaoImpl = paymentDaoImpl;
        this.queriesDao=queriesDao;
        this.patientsDaoTXT = patientsDaoTXT;
        this.patientsDaoXML = patientsDaoXML;
    }

    public Either<HospitalError, List<Patient>> getAllPatients() {
        return patientsDaoImpl.getAll();
    }

    public Either<HospitalError,Patient> get(int idPatientSelected) {
        return null;
    }
    public Either<HospitalError,Integer> transformTXTintoXML(){
        Either<HospitalError,Integer> result;
        Either<HospitalError, List<Patient>> dataPatients = patientsDaoTXT.getAll();
        List<PatientXML> patientXMLList = new ArrayList<>();
        if(dataPatients.isRight()){
            for(Patient patient:dataPatients.get()){
                PatientXML patientXML = new PatientXML();
                patientXML.setId(patient.getId());
                patientXML.setName(patient.getName());
                patientXML.setPhone(patient.getPhone());
                patientXML.setDob(patient.getDob().toString());
                patientXML.setMedicalRecords(getMedicalRecords(patient.getId()));
                patientXMLList.add(patientXML);
            }
            ListPatientsXML listPatientsXML = new ListPatientsXML();
            listPatientsXML.setPatients(patientXMLList);
            result= patientsDaoImpl.add(listPatientsXML);
        }else {
            result= Either.left(dataPatients.getLeft());
        }
        return result;
    }
    private List<MedicalRecordXML> getMedicalRecords(int id) {
        MedicalRecordsDao medicalRecordsDao = new MedicalRecordsDaoImpl();
        Either<HospitalError, List<MedicalRecord>> dataMedicalRecord = medicalRecordsDao.getAll(id);
        List<MedicalRecordXML> medicalRecordXMLList= new ArrayList<>();
        if(dataMedicalRecord.isRight()){
            for(MedicalRecord medicalRecord:dataMedicalRecord.get()){
                MedicalRecordXML medicalRecordXML = new MedicalRecordXML();
                medicalRecordXML.setDiagnosis(medicalRecord.getDiagnoses());
                DoctorsDao doctorsDao = new DoctorsDaoImpl();
                Either<HospitalError, Doctor> dataDoctor = doctorsDao.get(medicalRecord.getIdDoctor());
                if(dataDoctor.isRight()){
                    medicalRecordXML.setNameDoctor(dataDoctor.get().getName());
                }
                medicalRecordXML.setPrescribes(getPrescribes(medicalRecord.getId()));
                medicalRecordXMLList.add(medicalRecordXML);
            }
        }
        return medicalRecordXMLList;
    }
    private List<PrescribeXML> getPrescribes(int id) {
        PrescribeDao prescribesDao = new PrescribeDaoImpl();
        Either<HospitalError, List<Prescribe>> dataPrescribe = prescribesDao.getAll(id);
        List<PrescribeXML> prescribeXMLList = new ArrayList<>();
        if (dataPrescribe.isRight()) {
            for (Prescribe prescribe : dataPrescribe.get()) {
                PrescribeXML prescribeXML = new PrescribeXML();
                prescribeXML.setName(prescribe.getName());
                prescribeXML.setDosage(prescribe.getDossages());
                prescribeXMLList.add(prescribeXML);
            }
        }
        return prescribeXMLList;
    }
    public Either<HospitalError,List<Patient>> getPatientWithMedication(String medication) {
        return patientsDaoXML.get(medication);
    }
    public Either<HospitalError, List<MedicalRecord>> getAllMedicalRecordsWithPrescribes(int idPatient) {
        Either<HospitalError, List<MedicalRecord>> medicalRecords = medicalRecordsDao.getAll(idPatient);
        if (medicalRecords.isLeft()) {
            return medicalRecords;
        } else {
            for (MedicalRecord medicalRecord : medicalRecords.get()) {
                Either<HospitalError, List<Prescribe>> prescribes = prescribeDao.getAll(medicalRecord.getId());
                if (prescribes.isLeft()) {
                    return null;
                } else {
                    medicalRecord.setPrescribes(prescribes.get());
                }
            }
        }
        return medicalRecords;
    }

    public Either<HospitalError, List<PatientUI>> getAllPatientsWithTotalPayment() {
        Either<HospitalError, List<Patient>> patients = getAllPatients();
        if (patients.isLeft()) {
            return Either.left(patients.getLeft());
        } else {
            List<PatientUI> patientUIList = patients.get().stream().map(patient -> {
                PatientUI patientUI = new PatientUI(patient);
                patientUI.setTotalPayments(sumPayments(patient.getId()));
                return patientUI;
            }).toList();
            return Either.right(patientUIList);
        }
    }

    private Double sumPayments(int id) {
        Either<HospitalError, List<Payment>> payments = paymentDaoImpl.getAll(id);
        if (payments.isLeft()) {
            return 0.0;
        } else {
            return payments.get().stream().mapToDouble(Payment::getQuantity).sum();
        }
    }
    public Either<HospitalError, Integer> deletePatient(int id) {
        return patientsDaoXML.delete(id);
    }
    public Either<HospitalError, Integer> deletePatient(Patient patient, boolean deleteMedicalRecords) {
        if (!deleteMedicalRecords) {
            Either<HospitalError, List<MedicalRecord>> medicalRecords = medicalRecordsDao.getAll(patient.getId());
            if (medicalRecords.isLeft()) {
                if (medicalRecords.getLeft().getMessage().equals(MedicalRecordConstants.IS_EMPTY)) {
                    return patientsDaoImpl.delete(patient.getId());
                } else {
                    return Either.left(medicalRecords.getLeft());
                }
            } else {
                return Either.left(new HospitalError(1, PatientsConstants.PATIENT_HAS_MEDICAL_RECORDS));
            }
        } else {
            int deleted = 0;
            Either<HospitalError, Integer> deletedMedicalRecords = medicalRecordsDao.delete(patient);
            if (deletedMedicalRecords.isLeft()) {
                return deletedMedicalRecords;
            } else {
                deleted += deletedMedicalRecords.get();
                Either<HospitalError, Integer> deletedPatient = patientsDaoImpl.delete(patient.getId());
                if (deletedPatient.isLeft()) {
                    return deletedPatient;
                } else {
                    deleted += deletedPatient.get();
                    return Either.right(deleted);
                }
            }
        }
    }

    public String getPatientWithMostMedicalRecords() {
        return queriesDao.getPatientWithMostMedicalRecords();
    }

    public String getDateWithMorePatients() {
        return queriesDao.getDateWithMorePatients();
    }

    public Either<HospitalError, List<Patient>> getPatientsByMedication(String name, String dosage) {
        return queriesDao.getPatientsByMedication(name, dosage);
    }

    public Either<HospitalError, List<PatientUIV2>> getPatientsAndMedications() {
        return queriesDao.getPatientsAndMedications();
    }

    public void delete(int patientId2) {
    }

    public void save(Patient patient) {
    }
}
