package service;

import dao.DoctorsDao;
import dao.MedicalRecordsDao;
import dao.PatientsDao;
import dao.PrescribeDao;
import dao.impl.Spring.DoctorDaoImpl;
import dao.impl.Spring.MedicalRecordsDaoImpl;
import dao.impl.Spring.PrescribeDaoImpl;
import dao.impl.TXT.PatientsDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Doctor;
import model.MedicalRecord;
import model.Patient;
import model.Prescribe;
import model.XML.ListMedicalRecordsXML;
import model.XML.MedicalRecordXML;
import model.XML.PrescribeXML;
import model.error.HospitalError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordService {
    MedicalRecordsDao medicalRecordsDao;
    PatientsDao patientsDao;
    DoctorsDao doctorDao;
    PrescribeDao prescribesDao;
    MedicalRecordsDao medicalRecordsDaoXML;

    @Inject
    public MedicalRecordService(MedicalRecordsDaoImpl medicalRecordsDao, PatientsDaoImpl patientsDao,
                                DoctorDaoImpl doctorDao, PrescribeDaoImpl prescribesDao, dao.impl.XML.MedicalRecordsDaoImpl medicalRecordsDaoXML) {
        this.medicalRecordsDao = medicalRecordsDao;
        this.patientsDao = patientsDao;
        this.doctorDao = doctorDao;
        this.prescribesDao = prescribesDao;
        this.medicalRecordsDaoXML = medicalRecordsDaoXML;
    }
    public Either<HospitalError, Integer> addMedicalRecordXML(int id, MedicalRecordXML medicalRecordXML) {
        return patientsDao.update(id, medicalRecordXML);
    }
    public Either<HospitalError, Integer> save(MedicalRecord medicalRecord) {
        return medicalRecordsDao.add(medicalRecord);
    }

    public Either<HospitalError, List<MedicalRecord>> getByPatientId(int patientId) {
        return medicalRecordsDao.getAll(patientId);
    }

    public Either<HospitalError, Integer> delete(Patient patient) {
        return medicalRecordsDao.delete(patient);
    }

    public Either<HospitalError, Integer> addMedicalRecord(MedicalRecord medicalRecord) {
        Either<HospitalError, Integer> data = medicalRecordsDao.add(medicalRecord);
        if (data.isRight()) {
            return data;
        } else {
            return Either.left(new HospitalError(0, "Error adding medical record"));
        }
    }

    public Either<HospitalError, List<MedicalRecord>> getAllMedications() {
        return medicalRecordsDao.getAll();
    }

    public Either<HospitalError, Integer> backupDecrTime() {
        ListMedicalRecordsXML listMedicalRecordsXML = new ListMedicalRecordsXML();
        Either<HospitalError, List<Prescribe>> prescribesList = null;
        Either<HospitalError, List<MedicalRecord>> orderedMedicalRecords = medicalRecordsDao.getAll(LocalDate.now());
        if (orderedMedicalRecords.isLeft()) {
            return Either.left(orderedMedicalRecords.getLeft());
        }
        for (MedicalRecord medicalRecord : orderedMedicalRecords.get()) {
            Either<HospitalError, Doctor> doctor = doctorDao.get(medicalRecord.getIdDoctor());
            if (doctor.isLeft()) {
                return Either.left(doctor.getLeft());
            }
            List<PrescribeXML> prescribes = new ArrayList<>();
            prescribesList = prescribesDao.getAll(medicalRecord.getId());
            if (prescribesList.isLeft()) {
                return Either.left(prescribesList.getLeft());
            }
            for (Prescribe prescribe : prescribesList.get()) {
                prescribes.add(new PrescribeXML(prescribe.getName(), prescribe.getDossages()));
            }
            listMedicalRecordsXML.getMedicalRecordXMLS().add(new MedicalRecordXML(doctor.get().getName(), medicalRecord.getDiagnoses(), prescribes));
        }
        Either<HospitalError, Integer> result = medicalRecordsDaoXML.add(listMedicalRecordsXML);
        Either<HospitalError, Integer> resultDelete = medicalRecordsDao.delete(orderedMedicalRecords.get());
        if (result.isLeft() || resultDelete.isLeft()) {
            if (result.isLeft()) {
                return Either.left(result.getLeft());
            } else {
                return Either.left(resultDelete.getLeft());
            }
        } else {
            result = Either.right(listMedicalRecordsXML.getMedicalRecordXMLS().size() + prescribesList.get().size());
        }
        return result;
    }
}
