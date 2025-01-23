package service;

import dao.DoctorsDao;
import dao.MedicalRecordsDao;
import dao.PatientsDao;
import dao.PrescribeDao;
import dao.impl.hibernate.DoctorsDaoImpl;
import dao.impl.hibernate.MedicalRecordsDaoImpl;
import dao.impl.hibernate.PatientsDaoImpl;
import dao.impl.hibernate.PrescribeDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.hibernate.Doctor;
import model.hibernate.MedicalRecord;
import model.hibernate.Prescribe;

import java.time.LocalDate;
import java.util.List;

public class MedicalRecordService {
    MedicalRecordsDao medicalRecordsDao;
    PatientsDao patientsDao;
    DoctorsDao doctorDao;
    PrescribeDao prescribesDao;

    @Inject
    public MedicalRecordService(MedicalRecordsDaoImpl medicalRecordsDao, PatientsDaoImpl patientsDao,
                                DoctorsDaoImpl doctorDao, PrescribeDaoImpl prescribesDao) {
        this.medicalRecordsDao = medicalRecordsDao;
        this.patientsDao = patientsDao;
        this.doctorDao = doctorDao;
        this.prescribesDao = prescribesDao;
    }


    public Either<HospitalError, Integer> addMedicalRecord(String doctor, String patient, String diagnoses, LocalDate date, String name1, String dossages1, String name2, String dossages2) {
        Either<HospitalError, Doctor> dataDoctor = doctorDao.get(doctor);
        if (dataDoctor.isLeft()) {
            return Either.left(dataDoctor.getLeft());
        }
        Either<HospitalError, model.hibernate.Patient> dataPatient = patientsDao.get(patient);
        if (dataPatient.isLeft()) {
            return Either.left(dataPatient.getLeft());
        }
        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setDoctor(dataDoctor.get());
        medicalRecord.setPatient(dataPatient.get());
        medicalRecord.setDiagnoses(diagnoses);
        medicalRecord.setAdmissionDate(date);
        List<Prescribe> prescribes = List.of(new Prescribe(name1, dossages1), new Prescribe(name2, dossages2));
        medicalRecord.setPrescribes(prescribes);
        return medicalRecordsDao.add(medicalRecord);
    }

    public Either<HospitalError, List<MedicalRecord>> getAll() {
        return medicalRecordsDao.getAll();
    }

    public Either<HospitalError, Integer> deleteOldMedicalRecords(int year, boolean b) {
        LocalDate date = LocalDate.of(year, 1, 1);
        Either<HospitalError, List<MedicalRecord>> data = medicalRecordsDao.getAll(date, -1);
        if (data.isLeft())
            return Either.left(data.getLeft());
        return medicalRecordsDao.delete(data.get(), b);
    }
}
