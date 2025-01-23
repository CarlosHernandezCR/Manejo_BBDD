package ui;

import common.constants.CommonConstants;
import io.vavr.control.Either;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import model.error.HospitalError;
import model.hibernate.MedicalRecord;
import model.hibernate.Patient;
import model.hibernate.Prescribe;
import service.AppointmentService;
import service.MedicalRecordService;
import service.PatientService;
import service.PrescribeService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class Unit3HibernateExercises {
    public static void main(String[] args) {
        int option = 0;
        while (option != 11) {
            option = getOption();
            menu(option);
            Scanner scanner = new Scanner(System.in);
            pressEnterToContinue(scanner);
        }
    }

    private static void exercise1() {
        boolean repeat = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Get information of the medications of a specific medical record");
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        while (repeat) {
            try (SeContainer container = initializer.initialize()) {
                PrescribeService prescribeService = container.select(PrescribeService.class).get();
                System.out.println("Write the id of the medical record: ");
                int id;
                try {
                    id = scanner.nextInt();
                } catch (Exception e) {
                    System.out.println("Invalid input");
                    scanner.nextLine();
                    pressEnterToContinue(scanner);
                    continue;
                }
                scanner.nextLine();
                Either<HospitalError, List<Prescribe>> data = prescribeService.getMedicationsByMedicalRecord(id);
                if (data.isLeft()) {
                    System.out.println(data.getLeft().getMessage());
                    pressEnterToContinue(scanner);
                } else {
                    List<Prescribe> prescriptions = data.get();
                    for (Prescribe prescription : prescriptions) {
                        System.out.println(prescription);
                    }
                    repeat = false;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.println(CommonConstants.CONTINUE);
        scanner.nextLine();
    }

    private static void exercise2() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, Map<Patient, Double>> data = patientService.getTotalAmountPaidByPatient();
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
                Scanner scanner = new Scanner(System.in);
                pressEnterToContinue(scanner);
            } else {
                Map<Patient, Double> patients = data.get();
                for (Map.Entry<Patient, Double> entry : patients.entrySet()) {
                    System.out.println(entry.getKey().getName() + " " + entry.getValue() + "€");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise3() {
        System.out.println("New medical record with two medications:");
        System.out.println("Who was the doctor?");
        Scanner scanner = new Scanner(System.in);
        String doctor = scanner.nextLine();
        System.out.println("Who was the patient?");
        String patient = scanner.nextLine();
        System.out.println("What was the diagnoses?");
        String diagnoses = scanner.nextLine();
        boolean repeat = true;
        LocalDate date = null;
        while (repeat) {
            System.out.println("What was the admission date? (yyyy-mm-dd)");
            String admissionDate = scanner.nextLine();
            try {
                date = LocalDate.parse(admissionDate);
                repeat = false;
            } catch (Exception e) {
                System.out.println("Invalid date");
            }
        }
        System.out.println("What was the first medication name?");
        String name1 = scanner.nextLine();
        System.out.println("What was the first medication dossages?");
        String dossages1 = scanner.nextLine();
        System.out.println("What was the second medication name?");
        String name2 = scanner.nextLine();
        System.out.println("What was the second medication dossages?");
        String dossages2 = scanner.nextLine();
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            Either<HospitalError, Integer> data = medicalRecordService.addMedicalRecord(doctor, patient, diagnoses, date, name1, dossages1, name2, dossages2);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
                pressEnterToContinue(scanner);
            } else {
                System.out.println("Medical record added successfully");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise4() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try {
            SeContainer container = initializer.initialize();
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            Either<HospitalError, List<MedicalRecord>> data = medicalRecordService.getAll();
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                List<MedicalRecord> medicalRecords = data.get();
                Map<String, List<MedicalRecord>> recordsByPatient = new HashMap<>();
                for (MedicalRecord record : medicalRecords) {
                    String patientName = record.getPatient().getName();
                    if (!recordsByPatient.containsKey(patientName)) {
                        recordsByPatient.put(patientName, new ArrayList<>());
                    }
                    recordsByPatient.get(patientName).add(record);
                }

                for (Map.Entry<String, List<MedicalRecord>> entry : recordsByPatient.entrySet()) {
                    System.out.println("Patient: " + entry.getKey());
                    for (MedicalRecord record : entry.getValue()) {
                        System.out.println("    Doctor: " + record.getDoctor().getName());
                        System.out.println("    Diagnoses: " + record.getDiagnoses());
                        System.out.println("    Admission date: " + record.getAdmissionDate());
                        if (record.getPrescribes() != null) {
                            System.out.println("        Medications: ");
                            for (Prescribe prescribe : record.getPrescribes()) {
                                System.out.println(prescribe);
                            }
                        } else {
                            System.out.println("        No medications");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise5() {
        System.out.println("5. Append a new medication");
        System.out.println("What is the name of the medication?");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("What is the dossages of the medication?");
        String dossages = scanner.nextLine();
        System.out.println("What is the id of the medical record?");
        int id = scanner.nextInt();
        scanner.nextLine();
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PrescribeService prescribeService = container.select(PrescribeService.class).get();
            Either<HospitalError, Integer> data = prescribeService.addPrescribe(name, dossages, id);
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                System.out.println("Medication added successfully");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise6() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            Either<HospitalError, Integer> data = medicalRecordService.deleteOldMedicalRecords(2024, false);
            if (data.isLeft()) {
                if (data.getLeft().getCode() == 33) {
                    System.out.println(data.getLeft().getMessage());
                    System.out.println("Do you want to delete them? (y/n)");
                    Scanner scanner = new Scanner(System.in);
                    String answer = scanner.nextLine();
                    if (answer.equals("y")) {
                        data = medicalRecordService.deleteOldMedicalRecords(2024, true);
                        if (data.isLeft()) {
                            System.out.println(data.getLeft().getMessage());
                        } else {
                            System.out.println("Medical records with their medications were deleted successfully");
                        }
                    }
                } else {
                    System.out.println(data.getLeft().getMessage());
                }
            } else {
                System.out.println("Medical records deleted successfully");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise7() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, Patient> data = patientService.getPatientWithMostMedicalRecords();
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                Patient patient = data.get();
                System.out.println("Patient with most medical records: " + patient.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise8() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            AppointmentService appointmentService = container.select(AppointmentService.class).get();
            Either<HospitalError, LocalDate> data = appointmentService.getDateWithMorePatients();
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                LocalDate date = data.get();
                System.out.println("Date with more patients: " + date);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise9() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, Map<Patient, Long>> data = patientService.getPatientsWithNumberOfMedications();
            if (data.isLeft()) {
                System.out.println(data.getLeft().getMessage());
            } else {
                Map<Patient, Long> patients = data.get();
                for (Map.Entry<Patient, Long> entry : patients.entrySet()) {
                    System.out.println(entry.getKey().getName() + " " + entry.getValue() + " medications");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void exercise10() {
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
        System.out.println("Write the id of the patient you want to delete: ");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        scanner.nextLine();
        Either<HospitalError, Integer> data = patientService.deletePatient(id);
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            System.out.println("Patient deleted successfully");
        }
    }

    private static void updatePatient(PatientService patientService) {
        System.out.println("Write the id of the patient you want to update: ");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.println("What do you want to update?");
        Patient patient = new Patient();
        patient.setIdPatient(id);
        System.out.println("Do you want to update the patient's name? (y/n)");
        String answer = scanner.nextLine();
        if (answer.equals("y")) {
            System.out.println("Write the new name of the patient: ");
            String name = scanner.nextLine();
            patient.setName(name);
        }
        System.out.println("Do you want to update the patient's date of birth? (y/n)");
        answer = scanner.nextLine();
        if (answer.equals("y")) {
            boolean repeat = true;
            LocalDate date = null;
            while (repeat) {
                System.out.println("What was the admission date? (yyyy-mm-dd)");
                String admissionDate = scanner.nextLine();
                try {
                    date = LocalDate.parse(admissionDate);
                    repeat = false;
                } catch (Exception e) {
                    System.out.println("Invalid date");
                }
            }
            patient.setDob(Date.valueOf(date));
        }
        System.out.println("Do you want to update the patient's phone? (y/n)");
        answer = scanner.nextLine();
        if (answer.equals("y")) {
            System.out.println("Write the new phone of the patient: ");
            String phone = scanner.nextLine();
            patient.setPhone(phone);
        }
        Either<HospitalError, Integer> data = patientService.updatePatient(patient);
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            System.out.println("Patient updated successfully");
        }
    }

    private static void createPatient(PatientService patientService) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write the name of the patient: ");
        String name = scanner.nextLine();
        System.out.println("Write the date of birth of the patient(yyyy-mm-dd): ");
        boolean repeat = true;
        LocalDate date = null;
        while (repeat) {
            System.out.println("What was the admission date? (yyyy-mm-dd)");
            String admissionDate = scanner.nextLine();
            try {
                date = LocalDate.parse(admissionDate);
                repeat = false;
            } catch (Exception e) {
                System.out.println("Invalid date");
            }
        }
        System.out.println("Write the phone of the patient: ");
        String phone = scanner.nextLine();
        System.out.println("Write the username of the patient: ");
        String username = scanner.nextLine();
        System.out.println("Write the password of the patient: ");
        String password = scanner.nextLine();
        Either<HospitalError, Integer> data = patientService.addPatient(name, date, phone, username, password);
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            System.out.println("Patient added successfully");
        }
    }

    private static void readAllPatients(PatientService patientService) {
        Either<HospitalError, List<Patient>> data = patientService.getAll();
        if (data.isLeft()) {
            System.out.println(data.getLeft().getMessage());
        } else {
            List<Patient> patients = data.get();
            for (Patient patient : patients) {
                System.out.println(patient);
            }
        }
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
                exercise10();
                break;
            case 11:
                System.out.println("Goodbye!");
                return;
            default:
                System.out.println(CommonConstants.INVALID_PARAMETER);
                break;
        }
    }

    private static int getOption() {
        System.out.println("Unit 3 Hibernate Exercises");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the exercise you want to run: ");
        System.out.println("1. Get information of the medications of a specific medical record");
        System.out.println("2. Get the total amount paid by each patient, ordered by amount paid");
        System.out.println("3. Append a new medical record with two medications");
        System.out.println("4. Show all medical records by patient");
        System.out.println("5. Append a new medication");
        System.out.println("6. Delete all medical records older than year 2024. If they have medications, ask the user first.");
        System.out.println("7. Show the patient with the most medical records");
        System.out.println("8. Show the date with more patients");
        System.out.println("9. Show the name and the number of medications of each patient");
        System.out.println("10. CRUD of Patients");
        System.out.println("11. Exit");
        System.out.print("Write an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }
}