package service;

import dao.MedicalRecordsDao;
import dao.PatientsDao;
import dao.PrescribeDao;
import dao.impl.hibernate.MedicalRecordsDaoImpl;
import dao.impl.hibernate.PatientsDaoImpl;
import dao.impl.hibernate.PrescribeDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.hibernate.MedicalRecord;
import model.hibernate.Prescribe;

import java.util.List;

public class PrescribeService {
    PrescribeDao prescribeDao;
    PatientsDao patientsDao;
    MedicalRecordsDao medicalRecordDao;

    @Inject
    public PrescribeService(PrescribeDaoImpl prescribeDao, PatientsDaoImpl patientsDao, MedicalRecordsDaoImpl medicalRecordDao) {
        this.prescribeDao = prescribeDao;
        this.patientsDao = patientsDao;
        this.medicalRecordDao = medicalRecordDao;
    }
//    public Either<HospitalError,Boolean> addMedication(int id, String name, String dosage) {
//        Either<HospitalError,Boolean> result;
//        Either<HospitalError,MedicalRecord> newestMedicalRecord = medicalRecordDao.get(id);
//        if (newestMedicalRecord.isLeft()) {
//            result = Either.right(false);
//        } else {
//            Prescribe prescribe = new Prescribe(0, name, dosage,newestMedicalRecord.get());
//            Either<HospitalError,Integer>data=prescribeDao.add(prescribe);
//            if(data.isLeft()) {
//                result = Either.left(data.getLeft());
//            } else {
//                result = Either.right(true);
//            }
//        }return result;
//    }

    public Either<HospitalError, Integer> modifyDosage(int id, String dosage) {
        Either<HospitalError, Prescribe> prescribe = prescribeDao.get(id);
        if (prescribe.isLeft()) {
            return Either.left(prescribe.getLeft());
        } else {
            prescribe.get().setDossages(dosage);
            return prescribeDao.update(prescribe.get());
        }
    }

    public Either<HospitalError, List<Prescribe>> getMedicationsByMedicalRecord(int id) {
        Either<HospitalError, MedicalRecord> data = medicalRecordDao.get(id);
        if (data.isLeft()) {
            return Either.left(data.getLeft());
        } else {
            return prescribeDao.getAll(data.get().getIdMedicalRecords());
        }
    }

    public Either<HospitalError, Integer> addPrescribe(String name, String dossages, int id) {
        return prescribeDao.add(new Prescribe(0, name, dossages, id));
    }
}
