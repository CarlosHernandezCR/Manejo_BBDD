package dao.impl.XML;

import common.config.ConfigurationXML;
import common.constants.PatientsConstants;
import io.vavr.control.Either;
import jakarta.inject.Inject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import model.Patient;
import model.XML.ListPatientsXML;
import model.XML.MedicalRecordXML;
import model.XML.PatientXML;
import model.XML.PrescribeXML;
import model.error.HospitalError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientsXMLDaoImpl {
    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;
    private final Path xmlfile;

    @Inject
    public PatientsXMLDaoImpl() {
        try {
            xmlfile = Paths.get(ConfigurationXML.getInstance().getProperty(PatientsConstants.PATIENTS_FILE));
            JAXBContext context = JAXBContext.newInstance(ListPatientsXML.class);
            unmarshaller = context.createUnmarshaller();
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public Either<HospitalError, List<Patient>> getAll() {
        Either<HospitalError, List<Patient>> result;
        try {
            result = Either.right(parsePatients((ListPatientsXML) unmarshaller.unmarshal(Files.newInputStream(xmlfile))));
        } catch (JAXBException | IOException e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    private List<Patient> parsePatients(ListPatientsXML listPatientsXML) {
        List<Patient> patients = new ArrayList<>();
        for (PatientXML p : listPatientsXML.getPatients()) {
            int day = Integer.parseInt(p.getDob().substring(8, 9));
            int month = Integer.parseInt(p.getDob().substring(5, 6));
            int year = Integer.parseInt(p.getDob().substring(0, 3));

            patients.add(new Patient(p.getId(), p.getName(), p.getPhone(), LocalDate.of(year, month, day)));
        }
        return patients;
    }

    public Either<HospitalError, PatientXML> get(int id) {
        ListPatientsXML list;
        Either<HospitalError, PatientXML> result = null;
        try {
            list = (ListPatientsXML) unmarshaller.unmarshal(Files.newInputStream(xmlfile));
            for (PatientXML patientXML : list.getPatients()) {
                if (patientXML.getId() == id) {
                    result = Either.right(patientXML);
                    break;
                } else result = Either.left(new HospitalError(1, PatientsConstants.PATIENT_NOT_FOUND, LocalDateTime.now()));
            }
        } catch (JAXBException | IOException e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }


    public Either<HospitalError, List<Patient>> get(String medication) {
        Either<HospitalError, List<Patient>> result;
        List<Patient> patients = new ArrayList<>();
        try {
            List<PatientXML> patientXMLList = ((ListPatientsXML) unmarshaller.unmarshal(Files.newInputStream(xmlfile))).getPatients();
            for (PatientXML patientXML : patientXMLList)
                for (MedicalRecordXML medicationXML : patientXML.getMedicalRecords())
                    if (medicationXML.getPrescribes() != null)
                        for (PrescribeXML prescribeXML : medicationXML.getPrescribes())
                            if (prescribeXML.getName().equals(medication))
                                patients.add(new Patient(patientXML.getId(), patientXML.getName(), patientXML.getPhone(), LocalDate.parse(patientXML.getDob())));
            if (patients.isEmpty())
                result = Either.left(new HospitalError(1, PatientsConstants.PATIENT_NOT_FOUND, LocalDateTime.now()));
            else result = Either.right(patients);
        } catch (JAXBException | IOException e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_READING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    public Either<HospitalError, Integer> add(ListPatientsXML list) {
        Either<HospitalError, Integer> result;
        try {
            marshaller.marshal(list, Files.newOutputStream(xmlfile));
            result = Either.right(list.getPatients().size());
        } catch (Exception e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    public Either<HospitalError, Integer> delete(int id) {
        Either<HospitalError, Integer> result;
        try {
            ListPatientsXML list = (ListPatientsXML) unmarshaller.unmarshal(Files.newInputStream(xmlfile));
            list.getPatients().removeIf(patientXML -> patientXML.getId() == id);
            marshaller.marshal(list, Files.newOutputStream(xmlfile));
            result = Either.right(1);
        } catch (JAXBException | IOException e) {
            result = Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
        }
        return result;
    }

    public Either<HospitalError, Integer> update(int id, MedicalRecordXML medicalRecordXML) {
        get(id).map(patientXML -> {
            patientXML.getMedicalRecords().add(medicalRecordXML);
            return patientXML;
        }).map(patientXML -> {
            ListPatientsXML list = new ListPatientsXML();
            list.getPatients().add(patientXML);
            return list;
        }).map(list -> {
            try {
                marshaller.marshal(list, Files.newOutputStream(xmlfile));
                return Either.right(1);
            } catch (JAXBException | IOException e) {
                return Either.left(new HospitalError(1, PatientsConstants.ERROR_WRITING_FILE, LocalDateTime.now()));
            }
        });
        return null;
    }

}
