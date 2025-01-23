package ui;

import common.constants.CommonConstants;
import io.vavr.control.Either;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import model.error.HospitalError;
import model.mongo.Appointment;
import model.mongo.Doctor;
import model.mongo.MedicalRecord;
import model.mongo.Prescribe;
import service.AggregationsService;
import service.DoctorService;
import service.PatientService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static ui.Unit3HibernateExercises.pressEnterToContinue;

public class Unit4MongoExercises {
    public static void main(String[] args) {
        int option = 0;
        while (option != 10) {
            option = getOption();
            menu(option);
            Scanner scanner = new Scanner(System.in);
            pressEnterToContinue(scanner);
        }
    }

    private static int getOption() {
        System.out.println("Unit 4 Mongo Exercises");
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Import the data from the MySQL database (use Hibernate) to the Mongo database");
        System.out.println("2. CRUD of patients (If they have medical records, ask before deleting)");
        System.out.println("3. Get information of the medications (including dossage) of a specific patient");
        System.out.println("4. Get the name of the doctors of a given patient");
        System.out.println("5. Append a new medical record with two medications, including the dossage. The doctor will be “Doctor1”");
        System.out.println("6. Show all medical records by patient");
        System.out.println("7. Update a medical record: Add a new medication");
        System.out.println("8. Delete a medication from a medical record");
        System.out.println("9. Aggregations");
        System.out.println("10. Exit");
        System.out.print("Write an option: ");
        int option = 0;
        try {
            option = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid option");
        }
        scanner.nextLine();
        return option;
    }

    private static void menu(int option) {
        switch (option) {
            case 1:
                exercise1();
                break;
            case 2:
                exercise2();
                break;
            case 3:
                exercise3();
                break;
            case 4:
                exercise4();
                break;
            case 5:
                exercise5();
                break;
            case 6:
                exercise6();
                break;
            case 7:
                exercise7();
                break;
            case 8:
                exercise8();
                break;
            case 9:
                exercise9();
                break;
            case 10:
                System.out.println("Goodbye!");
                return;
            default:
                System.out.println(CommonConstants.INVALID_PARAMETER);
                break;
        }
    }

    private static void exercise1() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            DoctorService doctorService = container.select(DoctorService.class).get();
            Either<HospitalError, Integer> data1 = patientService.importDataFromMySQLToMongo();
            Either<HospitalError, Integer> data2 = doctorService.importDataFromMySQLToMongo();
            if (data1.isRight() && data2.isRight()) {
                System.out.println("Data imported successfully");
            } else if (data1.isLeft()) {
                System.out.println(data1.getLeft().getMessage());
            } else if (data2.isLeft()) {
                System.out.println(data2.getLeft().getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void exercise2() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println("CRUD of Patients");
            System.out.println("1. Create a new patient");
            System.out.println("2. Read all patients");
            System.out.println("3. Update a patient");
            System.out.println("4. Delete a patient");
            System.out.println("Write an option: ");
            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1:
                    createPatient(patientService);
                    break;
                case 2:
                    readAllPatients(patientService);
                    break;
                case 3:
                    updatePatient(patientService);
                    break;
                case 4:
                    deletePatient(patientService);
                    break;
                default:
                    System.out.println(CommonConstants.INVALID_PARAMETER);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void deletePatient(PatientService patientService) {
        System.out.println("Write the name of the patient you want to delete: ");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Either<HospitalError, Integer> data = patientService.deletePatientMongo(name, false);
        if (data.isLeft()) {
            if (data.getLeft().getCode() == 33) {
                System.out.println("The patient has medical records, do you want to delete it anyway? (y/n)");
                String answer = scanner.nextLine();
                if (answer.equals("y")) {
                    data = patientService.deletePatientMongo(name, true);
                    if (data.isLeft()) {
                        System.out.println(data.getLeft().getMessage());
                    } else {
                        System.out.println("Patient deleted successfully");
                    }
                }
            } else
                System.out.println(data.getLeft().getMessage());

        } else {
            System.out.println("Patient deleted successfully");
        }
    }

    private static void updatePatient(PatientService patientService) {
        Scanner scanner = new Scanner(System.in);
        boolean repeat = true;
        Either<HospitalError, model.mongo.Patient> patientData = null;
        while (repeat) {
            System.out.println("Write the name of the patient you want to update: ");
            String name = scanner.nextLine();
            patientData = patientService.getPatientMongo(name);
            if (patientData.isLeft()) {
                System.out.println(patientData.getLeft().getMessage());
            } else {
                repeat = false;
            }
        }
        model.mongo.Patient patient = patientData.get();
        System.out.println("Do you want to update the patient's date of birth? (y/n)");
        String answer = scanner.nextLine();
        if (answer.equals("y")) {
            repeat = true;
            while (repeat) {
                System.out.println("Write the new date of birth of the patient(yyyy-mm-dd): ");
                String dob = scanner.nextLine();
                try {
                    LocalDate.parse(dob);
                    repeat = false;
                    patient.setDob(dob);
                } catch (Exception e) {
                    System.out.println("Invalid date");
                }
            }
        }

        System.out.println("Do you want to update the patient's phone? (y/n)");
        answer = scanner.nextLine();
        if (answer.equals("y")) {
            System.out.println("Write the new phone of the patient: ");
            String phone = scanner.nextLine();
            patient.setPhone(phone);
        }

        System.out.println("Do you want to add an appointment? (y/n)");
        answer = scanner.nextLine();
        if (answer.equals("y")) {
            List<Appointment> appointments = new ArrayList<>();
            boolean moreAppointments = true;
            while (moreAppointments) {
                Appointment appointment = new Appointment();
                System.out.println("Write the id of the doctor: ");
                String idDoctor = scanner.nextLine();
                appointment.setIdDoctor(idDoctor);
                repeat = true;
                while (repeat) {
                    System.out.println("What was the appointment date? (yyyy-mm-dd)");
                    String appointmentDateStr = scanner.nextLine();
                    try {
                        LocalDate.parse(appointmentDateStr);
                        repeat = false;
                        appointment.setDate(appointmentDateStr);
                    } catch (Exception e) {
                        System.out.println("Invalid date");
                    }
                }
                appointments.add(appointment);
                System.out.println("Do you want to add another appointment? (y/n)");
                answer = scanner.nextLine();
                if (answer.equals("n")) {
                    moreAppointments = false;
                }
            }
            patient.setAppointments(appointments);
        }

        System.out.println("Do you want to add a medical record? (y/n)");
        answer = scanner.nextLine();
        if (answer.equals("y")) {
            List<MedicalRecord> medicalRecords = new ArrayList<>();
            boolean moreMedicalRecords = true;
            while (moreMedicalRecords) {
                MedicalRecord medicalRecord = new MedicalRecord();
                System.out.println("Write the id of the doctor: ");
                String idDoctor = scanner.nextLine();
                medicalRecord.setIdDoctor(idDoctor);
                System.out.println("Write the diagnoses: ");
                String diagnoses = scanner.nextLine();
                medicalRecord.setDiagnoses(diagnoses);
                repeat = true;
                while (repeat) {
                    System.out.println("What was the admission date? (yyyy-mm-dd)");
                    String admissionDateStr = scanner.nextLine();
                    try {
                        LocalDate.parse(admissionDateStr);
                        repeat = false;
                        medicalRecord.setAdmissionDate(admissionDateStr);
                    } catch (Exception e) {
                        System.out.println("Invalid date");
                    }
                }
                System.out.println("Do you want to add a medication? (y/n)");
                answer = scanner.nextLine();
                if (answer.equals("y")) {
                    List<model.mongo.Prescribe> prescribes = new ArrayList<>();
                    boolean moreMedications = true;
                    while (moreMedications) {
                        Prescribe prescribe = new Prescribe();
                        System.out.println("Write the name of the medication: ");
                        String nameMedication = scanner.nextLine();
                        prescribe.setName(nameMedication);
                        System.out.println("Write the dosage of the medication: ");
                        String dosage = scanner.nextLine();
                        prescribe.setDosage(dosage);
                        prescribes.add(prescribe);
                        System.out.println("Do you want to add another medication? (y/n)");
                        answer = scanner.nextLine();
                        if (answer.equals("n")) {
                            moreMedications = false;
                        }
                    }
                    medicalRecord.setPrescribes(prescribes);
                }
                medicalRecords.add(medicalRecord);
                System.out.println("Do you want to add another medical record? (y/n)");
                answer = scanner.nextLine();
                if (answer.equals("n")) {
                    moreMedicalRecords = false;
                }
            }
            patient.setMedicalRecords(medicalRecords);
        }

        Either<HospitalError, Integer> data = patientService.updatePatientMongo(patient);
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            System.out.println("Patient updated successfully");
        }
    }

    private static void createPatient(PatientService patientService) {
        Scanner scanner = new Scanner(System.in);
        model.mongo.Patient patient = new model.mongo.Patient();
        System.out.println("Write the name of the patient: ");
        String name = scanner.nextLine();
        patient.setName(name);
        boolean repeat = true;
        while (repeat) {
            System.out.println("Write the date of birth of the patient(yyyy-mm-dd): ");
            String dob = scanner.nextLine();
            try {
                LocalDate.parse(dob);
                repeat = false;
                patient.setDob(dob);
            } catch (Exception e) {
                System.out.println("Invalid date");
            }
        }
        System.out.println("Write the phone of the patient: ");
        String phone = scanner.nextLine();
        patient.setPhone(phone);
        System.out.println("Write the password of the patient: ");
        String password = scanner.nextLine();
        System.out.println("Do you want to add an appointment? (y/n)");
        String answer = scanner.nextLine();
        if (answer.equals("y")) {
            List<Appointment> appointments = new ArrayList<>();
            boolean moreAppointments = true;
            while (moreAppointments) {
                Appointment appointment = new Appointment();
                System.out.println("Write the id of the doctor: ");
                String idDoctor = scanner.nextLine();
                appointment.setIdDoctor(idDoctor);
                repeat = true;
                while (repeat) {
                    System.out.println("What was the appointment date? (yyyy-mm-dd)");
                    String admissionDateStr = scanner.nextLine();
                    try {
                        LocalDate.parse(admissionDateStr);
                        repeat = false;
                        appointment.setDate(admissionDateStr);
                    } catch (Exception e) {
                        System.out.println("Invalid date");
                    }
                }
                appointments.add(appointment);
                System.out.println("Do you want to add another appointment? (y/n)");
                answer = scanner.nextLine();
                if (answer.equals("n")) {
                    moreAppointments = false;
                }
            }
            patient.setAppointments(appointments);
        }
        System.out.println("Do you want to add a medical record? (y/n)");
        answer = scanner.nextLine();
        boolean moreMedicalRecords = true;
        if (answer.equals("y")) {
            List<MedicalRecord> medicalRecords = new ArrayList<>();
            MedicalRecord medicalRecord = new MedicalRecord();
            while (moreMedicalRecords) {
                System.out.println("Write the id of the doctor: ");
                String idDoctor = scanner.nextLine();
                medicalRecord.setIdDoctor(idDoctor);
                System.out.println("Write the diagnoses: ");
                String diagnoses = scanner.nextLine();
                medicalRecord.setDiagnoses(diagnoses);
                repeat = true;
                while (repeat) {
                    System.out.println("What was the admission date? (yyyy-mm-dd)");
                    String admissionDateStr = scanner.nextLine();
                    try {
                        LocalDate.parse(admissionDateStr);
                        repeat = false;
                        medicalRecord.setAdmissionDate(admissionDateStr);
                    } catch (Exception e) {
                        System.out.println("Invalid date");
                    }
                }
                System.out.println("Do you want to add a medication? (y/n)");
                answer = scanner.nextLine();
                boolean moreMedications = true;
                if (answer.equals("y")) {
                    List<model.mongo.Prescribe> prescribes = new ArrayList<>();
                    Prescribe prescribe = new Prescribe();
                    while (moreMedications) {
                        System.out.println("Write the name of the medication: ");
                        String nameMedication = scanner.nextLine();
                        prescribe.setName(nameMedication);
                        System.out.println("Write the dosage of the medication: ");
                        String dosage = scanner.nextLine();
                        prescribe.setDosage(dosage);
                        System.out.println("Do you want to add another medication? (y/n)");
                        answer = scanner.nextLine();
                        if (answer.equals("n")) {
                            moreMedications = false;
                        }
                        prescribes.add(prescribe);
                    }
                    medicalRecord.setPrescribes(prescribes);
                }
                medicalRecords.add(medicalRecord);
                System.out.println("Do you want to add another medical record? (y/n)");
                answer = scanner.nextLine();
                if (answer.equals("n")) {
                    moreMedicalRecords = false;
                }
            }
            patient.setMedicalRecords(medicalRecords);
        }
        Either<HospitalError, Integer> data = patientService.addPatientMongo(patient, patient.getName(), password);
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            System.out.println("Patient added successfully");
        }

    }

    private static void readAllPatients(PatientService patientService) {
        Either<HospitalError, List<model.mongo.Patient>> data = patientService.getAllMongo();
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            List<model.mongo.Patient> patients = data.get();
            for (model.mongo.Patient patient : patients) {
                System.out.println("Patient: " + patient.getName() + " dob: " + patient.getDob() + " phone: " + patient.getPhone());
                for (model.mongo.Appointment appointment : patient.getAppointments()) {
                    System.out.println("    idDoctor: " + appointment.getIdDoctor() + " date: " + appointment.getDate());
                }
                for (model.mongo.MedicalRecord medicalRecord : patient.getMedicalRecords()) {
                    System.out.println("    idDoctor: " + medicalRecord.getIdDoctor() + " diagnoses: " + medicalRecord.getDiagnoses() + " admissionDate: " + medicalRecord.getAdmissionDate());
                    for (model.mongo.Prescribe prescribe : medicalRecord.getPrescribes()) {
                        System.out.println("        name: " + prescribe.getName() + " dosage: " + prescribe.getDosage());
                    }
                }
            }
        }
    }

    private static void exercise3() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println("Write the name of the patient: ");
            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine();
            Either<HospitalError, model.mongo.Patient> data = patientService.getMedicationsOfPatient(name);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                List<model.mongo.MedicalRecord> medicalRecords = data.get().getMedicalRecords();
                if (medicalRecords == null || medicalRecords.isEmpty()) {
                    System.out.println("The patient doesn't have medical records");
                } else {
                    for (model.mongo.MedicalRecord medicalRecord : medicalRecords) {
                        System.out.println("Medical Record: ");
                        System.out.println("    idDoctor: " + medicalRecord.getIdDoctor() + " diagnoses: " + medicalRecord.getDiagnoses() + " admissionDate: " + medicalRecord.getAdmissionDate());
                        for (model.mongo.Prescribe prescribe : medicalRecord.getPrescribes()) {
                            System.out.println("        name: " + prescribe.getName() + " dosage: " + prescribe.getDosage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise4() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println("Write the name of the patient: ");
            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine();
            Either<HospitalError, Set<Doctor>> data = patientService.getDoctorsOfPatient(name);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                Set<Doctor> doctors = data.get();
                for (Doctor doctor : doctors) {
                    System.out.println("Doctor: " + doctor);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise5() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Write the name of the patient: ");
            String name = scanner.nextLine();
            Either<HospitalError, model.mongo.Patient> data = patientService.getPatientMongo(name);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                model.mongo.Patient patient = data.get();
                model.mongo.MedicalRecord medicalRecord = new MedicalRecord();
                DoctorService doctorService = container.select(DoctorService.class).get();
                Either<HospitalError, model.mongo.Doctor> doctorData = doctorService.get(1);
                medicalRecord.setIdDoctor(doctorData.get().getId());
                System.out.println("Write the diagnoses: ");
                medicalRecord.setDiagnoses(scanner.nextLine());
                boolean repeat = true;
                while (repeat) {
                    System.out.println("Write the admission date(yyyy-mm-dd): ");
                    String admissionDateStr = scanner.nextLine();
                    try {
                        LocalDate.parse(admissionDateStr);
                        repeat = false;
                        medicalRecord.setAdmissionDate(admissionDateStr);
                    } catch (Exception e) {
                        System.out.println("Invalid date");
                    }
                }
                List<Prescribe> prescribes = new ArrayList<>();
                Prescribe prescribe1 = new Prescribe();
                System.out.println("Write the name of the medication 1: ");
                prescribe1.setName(scanner.nextLine());
                System.out.println("Write the dosage of the medication 1: ");
                prescribe1.setDosage(scanner.nextLine());
                prescribes.add(prescribe1);
                Prescribe prescribe2 = new Prescribe();
                System.out.println("Write the name of the medication 2: ");
                prescribe2.setName(scanner.nextLine());
                System.out.println("Write the dosage of the medication 2: ");
                prescribe2.setDosage(scanner.nextLine());
                prescribes.add(prescribe2);
                medicalRecord.setPrescribes(prescribes);
                List<MedicalRecord> medicalRecords = patient.getMedicalRecords();
                medicalRecords.add(medicalRecord);
                patient.setMedicalRecords(medicalRecords);
                Either<HospitalError, Integer> data2 = patientService.updatePatientMongo(patient);
                if (data2.isLeft()) {
                    System.out.println(data2.getLeft().getMessage());
                } else {
                    System.out.println("Medical record added successfully");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise6() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, List<model.mongo.Patient>> data = patientService.getAllMongo();
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                List<model.mongo.Patient> patients = data.get();
                for (model.mongo.Patient patient : patients) {
                    System.out.println("Patient: " + patient.getName());
                    for (model.mongo.MedicalRecord medicalRecord : patient.getMedicalRecords()) {
                        System.out.println("    Medical Record: ");
                        System.out.println("        idDoctor: " + medicalRecord.getIdDoctor() + " diagnoses: " + medicalRecord.getDiagnoses() + " admissionDate: " + medicalRecord.getAdmissionDate());
                        for (model.mongo.Prescribe prescribe : medicalRecord.getPrescribes()) {
                            System.out.println("            name: " + prescribe.getName() + " dosage: " + prescribe.getDosage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise7() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Write the name of the patient: ");
            String name = scanner.nextLine();
            Either<HospitalError, model.mongo.Patient> data = patientService.getPatientMongo(name);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                model.mongo.Patient patient = data.get();
                System.out.println("Write the name of the diagnoses of the Medical Record: ");
                String diagnose = scanner.nextLine();
                if (patient.getMedicalRecords().stream().noneMatch(medicalRecord1 -> medicalRecord1.getDiagnoses().equals(diagnose))) {
                    System.out.println("The patient doesn't have a medical record with that diagnoses");
                } else {
                    model.mongo.MedicalRecord medicalRecord = patient.getMedicalRecords().stream().filter(medicalRecord1 -> medicalRecord1.getDiagnoses().equals(diagnose)).findFirst().get();
                    System.out.println("add a medication");
                    Prescribe prescribe = new Prescribe();
                    System.out.println("Write the name of the medication: ");
                    prescribe.setName(scanner.nextLine());
                    System.out.println("Write the dosage of the medication: ");
                    prescribe.setDosage(scanner.nextLine());
                    medicalRecord.getPrescribes().add(prescribe);
                    Either<HospitalError, Integer> data2 = patientService.updatePatientMongo(patient);
                    if (data2.isLeft()) {
                        System.out.println(data2.getLeft().getMessage());
                    } else {
                        System.out.println("Medical record added successfully");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise8() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Write the name of the patient: ");
            String name = scanner.nextLine();
            Either<HospitalError, model.mongo.Patient> data = patientService.getPatientMongo(name);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                model.mongo.Patient patient = data.get();
                System.out.println("Write the name of the diagnoses of the Medical Record: ");
                String diagnose = scanner.nextLine();
                if (patient.getMedicalRecords().stream().noneMatch(medicalRecord1 -> medicalRecord1.getDiagnoses().equals(diagnose))) {
                    System.out.println("The patient doesn't have a medical record with that diagnoses");
                } else {
                    model.mongo.MedicalRecord medicalRecord = patient.getMedicalRecords().stream().filter(medicalRecord1 -> medicalRecord1.getDiagnoses().equals(diagnose)).findFirst().get();
                    System.out.println("Write the name of the medication you want to delete: ");
                    String nameMedication = scanner.nextLine();
                    if (medicalRecord.getPrescribes().stream().noneMatch(prescribe -> prescribe.getName().equals(nameMedication))) {
                        System.out.println("The medical record doesn't have that medication");
                    } else {
                        medicalRecord.getPrescribes().removeIf(prescribe -> prescribe.getName().equals(nameMedication));
                        Either<HospitalError, Integer> data2 = patientService.updatePatientMongo(patient);
                        if (data2.isLeft()) {
                            System.out.println(data2.getLeft().getMessage());
                        } else {
                            System.out.println("Medication deleted successfully");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9() {
        while (true) {
            char option = getOptionAggregations();
            if (option == 'l') {
                break;
            }
            menuAggregations(option);
            Scanner scanner = new Scanner(System.in);
            pressEnterToContinue(scanner);
        }
    }

    private static void menuAggregations(char option) {
        switch (option) {
            case 'a':
                exercise9a();
                break;
            case 'b':
                exercise9b();
                break;
            case 'c':
                exercise9c();
                break;
            case 'd':
                exercise9d();
                break;
            case 'e':
                exercise9e();
                break;
            case 'f':
                exercise9f();
                break;
            case 'g':
                exercise9g();
                break;
            case 'h':
                exercise9h();
                break;
            case 'i':
                exercise9i();
                break;
            case 'j':
                exercise9j();
                break;
            case 'k':
                exercise9k();
                break;
            case 'l':
                break;
            default:
                System.out.println(CommonConstants.INVALID_PARAMETER);
                break;
        }
    }

    private static char getOptionAggregations() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("a. Get the name of the medication with the highest dossage");
        System.out.println("b. Get the medical records of a given patient, showing the name of the patient and the number of appointments");
        System.out.println("c. Get the number of medications of each patient");
        System.out.println("d. Get the name of the patients prescribed with Amoxicilina");
        System.out.println("e. Get the average number of medications per patient");
        System.out.println("f. Get the most prescribed medication");
        System.out.println("g. Show a list with the medications of a selected patient");
        System.out.println("h. Get the most prescribed medication per patient");
        System.out.println("i. Get name of the doctors that don’t have any patient");
        System.out.println("j. Get the name of the doctor with more patients");
        System.out.println("k. Get the name of the patient with more medical records");
        System.out.println("l. Exit");
        System.out.print("Write an option: ");
        return scanner.nextLine().charAt(0);
    }

    private static void exercise9a() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getMedicationWithHighestDosage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9b() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getMedicalRecordsOfPatient("Marcos M"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9c() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getNumberOfMedications());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9d() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getPatientsPrescribedWithAmoxicilina());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9e() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getAverageNumberOfMedications());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9f() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getMostPrescribedMedication());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9g() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getMedicationsOfPatients());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9h() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getMostPrescribedMedicationPerPatient());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9i() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getDoctorsWithoutPatients());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9j() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getDoctorsWithMostPatients());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9k() {
        try {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                AggregationsService aggregationsService = container.select(AggregationsService.class).get();
                System.out.println(aggregationsService.getPatientsWithoutMedications());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
