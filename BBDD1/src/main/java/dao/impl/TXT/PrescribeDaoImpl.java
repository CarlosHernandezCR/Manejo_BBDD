package dao.impl.TXT;
import common.config.ConfigurationTXT;
import common.constants.CommonConstants;
import common.constants.PrescribeConstants;
import dao.PrescribeDao;
import io.vavr.control.Either;
import model.MedicalRecord;
import model.Prescribe;
import model.error.HospitalError;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescribeDaoImpl implements PrescribeDao {
    private final Path file;

    public PrescribeDaoImpl() {
        file = Paths.get(ConfigurationTXT.getInstance().getProperty(PrescribeConstants.PRESCRIBE_FILE));
    }

    @Override
    public Either<HospitalError, List<Prescribe>> getAll() {
        Either<HospitalError, List<Prescribe>> result;
        List<Prescribe> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(new Prescribe(line));
            }
            result = Either.right(list);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PrescribeConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Prescribe> get(int id) {
        Either<HospitalError, Prescribe> result = Either.left(new HospitalError(1, PrescribeConstants.PRESCRIBE_NOT_FOUND, LocalDateTime.now()));

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Prescribe prescribe = new Prescribe(line);
                if (prescribe.getId() == id) {
                    result = Either.right(prescribe);
                    break;
                }
            }
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PrescribeConstants.ERROR_READING_FILE, LocalDateTime.now()));
        } catch (NumberFormatException e) {
            result = Either.left(new HospitalError(1, CommonConstants.INVALID_PARAMETER, LocalDateTime.now()));
        }

        return result;
    }

    @Override
    public Either<HospitalError, List<Prescribe>> getAll(int id) {
        Either<HospitalError, List<Prescribe>> result;
        List<Prescribe> list = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Prescribe prescribe = new Prescribe(line);
                if (prescribe.getIdMedicalRecords() == id) {
                    list.add(prescribe);
                }
            }
            if (list.isEmpty())
                result = Either.left(new HospitalError(1, PrescribeConstants.PRESCRIBE_NOT_FOUND, LocalDateTime.now()));
            else
                result = Either.right(list);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PrescribeConstants.ERROR_READING_FILE, LocalDateTime.now()));
        } catch (NumberFormatException e) {
            result = Either.left(new HospitalError(1, CommonConstants.INVALID_PARAMETER, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> add(Prescribe d) {
        Either<HospitalError, Integer> result;
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(d.toFileLine());
            writer.newLine();
            result = Either.right(1);
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PrescribeConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }
    @Override
    public Either<HospitalError, Integer> add(List<Prescribe> list) {
        Either<HospitalError, Integer> result;
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Prescribe prescribe : list) {
                writer.write(prescribe.toFileLine());
                writer.newLine();
            }
            result = Either.right(list.size());
        } catch (IOException e) {
            result = Either.left(new HospitalError(1, PrescribeConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    @Override
    public Either<HospitalError, Integer> delete(Prescribe d) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(MedicalRecord d) {
        List<Prescribe> list = getAll().get();
        list.removeIf(prescribe -> prescribe.getIdMedicalRecords() == d.getId());
        return add(list);
    }

    @Override
    public Either<HospitalError, Integer> update(Prescribe d) {
        return null;
    }
}
