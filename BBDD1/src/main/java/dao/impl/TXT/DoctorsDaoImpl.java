package dao.impl.TXT;

import common.config.ConfigurationTXT;
import common.constants.CommonConstants;
import common.constants.DoctorConstants;
import dao.DoctorsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Doctor;
import model.error.HospitalError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorsDaoImpl implements DoctorsDao {
    private final Path file;
    @Inject
    public DoctorsDaoImpl() {
        file = Paths.get(ConfigurationTXT.getInstance().getProperty(DoctorConstants.DOCTORS_FILE));
    }
    @Override
    public Either<HospitalError, List<Doctor>> getAll() {
        Either<HospitalError, List<Doctor>> result;
        List<Doctor> list = new ArrayList<>();
        List<String> fileList;
        try {
            fileList = Files.readAllLines(file);
            fileList.forEach(linea -> list.add(new Doctor(linea)));
            result = Either.right(list);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, DoctorConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Doctor> get(int id) {
        Either<HospitalError, Doctor> result;
        try {
            List<String> lines = Files.readAllLines(file);
            boolean found = false;
            Doctor doctor = null;
            for (String line : lines) {
                Doctor doctor1 = new Doctor(line);
                if (doctor1.getId() == id) {
                    doctor = doctor1;
                    found = true;
                    break;
                }
            }
            if (found) {
                result = Either.right(doctor);
            } else {
                result = Either.left(new HospitalError(1, DoctorConstants.DOCTOR_NOT_FOUND, LocalDateTime.now()));
            }
        } catch (IOException e) {
            result = Either.left(new HospitalError(2, DoctorConstants.ERROR_READING_FILE, LocalDateTime.now()));
        } catch (NumberFormatException e) {
            result = Either.left(new HospitalError(3, CommonConstants.INVALID_PARAMETER, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(Doctor d) {
        Either<HospitalError, Integer> result;
        try {
            Files.write(file,(d.toFileLine()+"\n").getBytes(), StandardOpenOption.APPEND);
            result=Either.right(1);
        } catch (IOException e) {
            result= Either.left(new HospitalError(1, DoctorConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(Doctor d) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(Doctor d) {
        return null;
    }
}
