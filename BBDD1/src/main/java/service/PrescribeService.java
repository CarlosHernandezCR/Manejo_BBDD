package service;

import dao.MedicalRecordsDao;
import dao.PatientsDao;
import dao.PrescribeDao;
import dao.impl.Spring.MedicalRecordsDaoImpl;
import dao.impl.TXT.PatientsDaoImpl;
import dao.impl.Spring.PrescribeDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.MedicalRecord;
import model.Prescribe;
import model.error.HospitalError;

import java.util.List;

public class PrescribeService {
    PrescribeDao prescribeDao;
    PatientsDao patientsDao;
    MedicalRecordsDao medicalRecordDao;
    @Inject
    public PrescribeService(PrescribeDaoImpl prescribeDao, PatientsDaoImpl patientsDao, MedicalRecordsDaoImpl medicalRecordDao){
        this.prescribeDao = prescribeDao;
        this.patientsDao = patientsDao;
        this.medicalRecordDao = medicalRecordDao;
    }
    public void save(Prescribe prescribe) {
        prescribeDao.add(prescribe);
    }

    public Either<HospitalError, List<Prescribe>> getByMedicalRecordId(int id) {
        return prescribeDao.getAll(id);
    }

    public Either<HospitalError, Integer> deleteByMedicalRecord(MedicalRecord medicalRecord) {
        return prescribeDao.delete(medicalRecord);
    }

    public Either<HospitalError,List<Prescribe>> getMedicationsOfPatient(int id) {
        return prescribeDao.getAll(id);
    }

    public Either<HospitalError,Boolean> addMedication(int id, String name, String dosage) {
        Either<HospitalError,Boolean> result;
        Either<HospitalError,MedicalRecord> newestMedicalRecord = medicalRecordDao.get(id);
        if (newestMedicalRecord.isLeft()) {
            result = Either.right(false);
        } else {
            Prescribe prescribe = new Prescribe(0, name, dosage,newestMedicalRecord.get().getId());
            Either<HospitalError,Integer>data=prescribeDao.add(prescribe);
            if(data.isLeft()) {
                result = Either.left(data.getLeft());
            } else {
                result = Either.right(true);
            }
        }return result;
    }

    public Either<HospitalError, Integer> modifyDosage(int id, String dosage) {
        Either<HospitalError,Prescribe> prescribe = prescribeDao.get(id);
        if (prescribe.isLeft()) {
            return Either.left(prescribe.getLeft());
        } else {
            prescribe.get().setDossages(dosage);
            return prescribeDao.update(prescribe.get());
        }
    }
}
