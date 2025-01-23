package dao.impl.TXT;
import common.config.ConfigurationTXT;
import common.constants.CommonConstants;
import common.constants.MedicalRecordConstants;
import dao.MedicalRecordsDao;
import io.vavr.control.Either;
import model.MedicalRecord;
import model.Patient;
import model.XML.ListMedicalRecordsXML;
import model.error.HospitalError;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordsDaoImpl implements MedicalRecordsDao {
    private final Path file;

    public MedicalRecordsDaoImpl() {
        file = Paths.get(ConfigurationTXT.getInstance().getProperty(MedicalRecordConstants.MEDICALRECORDS_FILE));
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll() {
        Either<HospitalError, List<MedicalRecord>> result;
        List<MedicalRecord> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(new MedicalRecord(line));
            }
            result = Either.right(list);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, MedicalRecord> get(int id) {
        Either<HospitalError, MedicalRecord> result;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            boolean found = false;
            MedicalRecord medicalRecord = null;
            while ((line = reader.readLine()) != null) {
                MedicalRecord medicalRecord1 = new MedicalRecord(line);
                if (medicalRecord1.getId() == id) {
                    medicalRecord = medicalRecord1;
                    found = true;
                    break;
                }
            }
            if (found) {
                result = Either.right(medicalRecord);
            } else {
                result = Either.left(new HospitalError(1, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND, LocalDateTime.now()));
            }
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_READING_FILE, LocalDateTime.now()));
        } catch (NumberFormatException e) {
            result = Either.left(new HospitalError(1, CommonConstants.INVALID_PARAMETER, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(int idPatient) {
        Either<HospitalError, List<MedicalRecord>> result;
        List<MedicalRecord> medicalRecordsList = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                MedicalRecord medicalRecord = new MedicalRecord(line);
                if (medicalRecord.getIdPatient() == idPatient) {
                    medicalRecordsList.add(medicalRecord);
                }
            }
            if (!medicalRecordsList.isEmpty()) {
                result = Either.right(medicalRecordsList);
            } else {
                result = Either.left(new HospitalError(1, MedicalRecordConstants.MEDICALRECORD_NOT_FOUND, LocalDateTime.now()));
            }
        } catch (IOException e) {
            result = Either.left(new HospitalError(2, MedicalRecordConstants.ERROR_READING_FILE, LocalDateTime.now()));
        } catch (NumberFormatException e) {
            result = Either.left(new HospitalError(3, CommonConstants.INVALID_PARAMETER, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(LocalDate date) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(MedicalRecord d) {
        Either<HospitalError, Integer> result;
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(d.toFileLine());
            writer.newLine();
            result = Either.right(1);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(ListMedicalRecordsXML list) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(List<MedicalRecord> list) {
        Either<HospitalError, Integer> result;
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (MedicalRecord medicalRecord : list) {
                writer.write(medicalRecord.toFileLine());
                writer.newLine();
            }
            result = Either.right(list.size());
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, MedicalRecordConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(MedicalRecord d) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Patient patient) {
        Either<HospitalError, Integer> result;
        List<MedicalRecord> list = getAll().get();
        List<MedicalRecord> list2 = new ArrayList<>();
        for (MedicalRecord medicalRecord : list) {
            if (medicalRecord.getIdPatient() != patient.getId()) {
                list2.add(medicalRecord);
            }
        }
        result = add(list2);
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(List<MedicalRecord> medicalRecords) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(MedicalRecord d) {
        return null;
    }
}
