package service;

import dao.DoctorsDao;
import dao.impl.hibernate.DoctorsDaoImpl;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.error.HospitalError;
import model.hibernate.Doctor;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorService {
    DoctorsDao doctorsDaoHibernate;
    dao.impl.mongo.DoctorsDaoImpl doctorsDaoMongo;

    @Inject
    public DoctorService(DoctorsDaoImpl doctorsDaoHibernate, dao.impl.mongo.DoctorsDaoImpl doctorsDaoMongo) {
        this.doctorsDaoHibernate = doctorsDaoHibernate;
        this.doctorsDaoMongo = doctorsDaoMongo;
    }

    public int save(Doctor doctor) {
        Either<HospitalError, Integer> result = doctorsDaoHibernate.add(doctor);
        if (result.isLeft())
            return 0;
        else return 1;
    }

    public Either<HospitalError, model.mongo.Doctor> get(int idDoctor) {
        return doctorsDaoMongo.get(idDoctor);
    }

    public Either<HospitalError, List<Doctor>> getAll() {
        return doctorsDaoHibernate.getAll();
    }

    public Either<HospitalError, Integer> importDataFromMySQLToMongo() {
        Either<HospitalError, List<Doctor>> data = doctorsDaoHibernate.getAll();
        if (data.isLeft())
            return Either.left(data.getLeft());
        return doctorsDaoMongo.add(data.get().stream().map(Doctor::toMongo).toList());
    }
}
