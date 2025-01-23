package dao.impl.TXT;

import common.config.ConfigurationTXT;
import common.constants.CommonConstants;
import common.constants.PatientsConstants;
import dao.PatientsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import model.Patient;
import model.XML.ListPatientsXML;
import model.XML.MedicalRecordXML;
import model.error.HospitalError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientsDaoImpl implements PatientsDao {
    private final Path file;
    @Inject
    public PatientsDaoImpl() {
        file = Paths.get(ConfigurationTXT.getInstance().getProperty(PatientsConstants.PATIENTS_FILE));
    }
    @Override
    public Either<HospitalError, List<Patient>> getAll() {
        Either<HospitalError, List<Patient>> result;
        List<Patient> list = new ArrayList<>();
        List<String> fileList;
        try {
            fileList = Files.readAllLines(file);
            fileList.forEach(linea -> list.add(new Patient(linea)));
            result = Either.right(list);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Patient> get(int id) {
        Either<HospitalError, Patient> result;
        try {
            List<String> lines = Files.readAllLines(file);
            boolean found = false;
            Patient patient = null;
            for (String line : lines) {
                Patient patient1 = new Patient(line);
                if (patient1.getId() == id) {
                    patient = patient1;
                    found = true;
                    break;
                }
            }
            if (found) {
                result = Either.right(patient);
            } else {
                result = Either.left(new HospitalError(1, PatientsConstants.PATIENT_NOT_FOUND, LocalDateTime.now()));
            }
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE, LocalDateTime.now()));
        } catch (NumberFormatException e) {
            result = Either.left(new HospitalError(1, CommonConstants.INVALID_PARAMETER, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, List<Patient>> get(String medication) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(Patient d) {
        Either<HospitalError, Integer> result;
        try {
            Files.write(file,(d.toFileLine()+"\n").getBytes(), StandardOpenOption.APPEND);
            result=Either.right(1);
        } catch (IOException e) {
            result= Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(ListPatientsXML list) {
        return null;
    }

    @Override
    public Either<HospitalError,Integer> add(List<Patient> list) {
        Either<HospitalError,Integer> result;
        try {
            Files.write(file, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
            for (int i = 0; i < list.size(); i++) {
                String write;
                if (i == list.size() - 1) {
                    write = list.get(i).toFileLine();
                } else {
                    write = list.get(i).toFileLine() + "\n";
                }
                Files.write(file, write.getBytes(), StandardOpenOption.APPEND);
            }
            result = Either.right(list.size());
        } catch (IOException e) {
            result= Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(Patient d) {
        List<Patient> list = getAll().get();
        list.remove(d);
        Either<HospitalError, Integer> result=add(list);
        if(result.isRight()){
            if(list.size()==result.get()-1)
                return Either.right(1);
            else
                return Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }else return Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
    }

    @Override
    public Either<HospitalError, Integer> delete(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(int id, MedicalRecordXML medicalRecordXML) {
        return null;
    }
}
