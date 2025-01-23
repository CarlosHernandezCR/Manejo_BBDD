package dao.impl.XML;

import common.config.ConfigurationXML;
import common.constants.MedicalRecordConstants;
import common.constants.PatientsConstants;
import dao.MedicalRecordsDao;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import model.MedicalRecord;
import model.Patient;
import model.XML.ListMedicalRecordsXML;
import model.error.HospitalError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordsDaoImpl implements MedicalRecordsDao {
    private final Marshaller marshaller;
    private final Path xmlfile;

    @Inject
    public MedicalRecordsDaoImpl() {
        try {
            xmlfile = Paths.get(ConfigurationXML.getInstance().getProperty(MedicalRecordConstants.MR_FILE));
            JAXBContext context = JAXBContext.newInstance(ListMedicalRecordsXML.class);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll() {
        return null;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(int idPatient) {
        return null;
    }

    @Override
    public Either<HospitalError, List<MedicalRecord>> getAll(LocalDate date) {
        return null;
    }

    @Override
    public Either<HospitalError, MedicalRecord> get(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(MedicalRecord o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> add(ListMedicalRecordsXML list) {
        Either<HospitalError, Integer> result;
        try {
            marshaller.marshal(list, Files.newOutputStream(xmlfile));
            result = Either.right(list.getMedicalRecordXMLS().size());
        } catch (Exception e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;    }

    @Override
    public Either<HospitalError, Integer> add(List<MedicalRecord> list) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(MedicalRecord o) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(int id) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(Patient patient) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> delete(List<MedicalRecord> medicalRecords) {
        return null;
    }

    @Override
    public Either<HospitalError, Integer> update(MedicalRecord o) {
        return null;
    }
}
