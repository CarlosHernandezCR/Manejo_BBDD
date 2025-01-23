package service;

import dao.DoctorsDao;
import dao.impl.JDBC.DoctorsDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Doctor;
import model.error.HospitalError;

import java.util.List;

public class DoctorService {
    DoctorsDao doctorsDao;
    @Inject
    public DoctorService(DoctorsDaoImpl doctorsDao) {
        this.doctorsDao = doctorsDao;
    }
    public int save(Doctor doctor) {
        Either<HospitalError,Integer> result = doctorsDao.add(doctor);
        if(result.isLeft())
            return 0;
        else return 1;
    }

    public Either<HospitalError, Doctor> get(int idDoctor) {
        return doctorsDao.get(idDoctor);
    }

    public Either<HospitalError, List<Doctor>> getAll() {
        return doctorsDao.getAll();
    }
}
