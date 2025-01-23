package ui;

import common.constants.CommonConstants;
import io.vavr.control.Either;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import model.*;
import model.error.HospitalError;
import service.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Unit2JDBCExercises {
    public static void main(String[] args) {
        Credential credential = login();
        int option = 0;
        while (option != 7) {
            option = getOption(credential.getIdPatient() == 0);
            menu(option, credential.getIdPatient());
            continueConsole();
        }
    }

    private static Credential login() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            try (SeContainer container = initializer.initialize()) {
                CredentialService credentialService = container.select(CredentialService.class).get();
                Either<HospitalError, Credential> result = credentialService.login(username, password);
                if (result.isRight()) {
                    return result.get();
                } else {
                    System.out.println("Error: " + result.getLeft().getMessage());
                    continueConsole();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                continueConsole();
            }
        }
    }

    private static void exercise1(int idPatient) {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            if (idPatient == 0) {
                int idPatientSelected;
                Either<HospitalError, List<Patient>> data = patientService.getAllPatients();
                if (data.isRight()) {
                    for (Patient patient : data.get()) {
                        System.out.println(patient);
                    }
                    Either<HospitalError, Patient> patientExist;
                    do {
                        System.out.println("Select a patient by id: ");
                        Scanner scanner = new Scanner(System.in);
                        idPatientSelected = scanner.nextInt();
                        scanner.nextLine();
                        patientExist = patientService.get(idPatientSelected);
                        if (patientExist.isLeft())
                            System.out.println(patientExist.getLeft().getMessage());
                    } while (patientExist.isLeft());

                    System.out.println(patientService.getAllMedicalRecordsWithPrescribes(idPatientSelected));
                }

            } else {
                System.out.println(patientService.getAllMedicalRecordsWithPrescribes(idPatient));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exercise2() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println(patientService.getAllPatientsWithTotalPayment());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exercise3() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            PatientService patientService = container.select(PatientService.class).get();
            DoctorService doctorService = container.select(DoctorService.class).get();
            Either<HospitalError, List<Patient>> data = patientService.getAllPatients();
            if (data.isRight()) {
                for (Patient patient : data.get()) {
                    System.out.println(patient);
                }
                Scanner scanner = new Scanner(System.in);

                Either<HospitalError, Patient> existPatient;
                int idPatient;
                do {
                    System.out.println("Enter the id of the patient: ");
                    idPatient = scanner.nextInt();
                    scanner.nextLine();
                    existPatient = patientService.get(idPatient);
                } while (existPatient.isLeft());
                Either<HospitalError, Doctor> existDoctor;
                int idDoctor;
                do {
                    Either<HospitalError, List<Doctor>> data2 = doctorService.getAll();
                    if (data2.isRight()) {
                        for (Doctor doctor : data2.get()) {
                            System.out.println(doctor);
                        }
                    } else {
                        System.out.println(data2.getLeft().getMessage());
                    }
                    System.out.println("Enter the id of the doctor: ");
                    idDoctor = scanner.nextInt();
                    scanner.nextLine();
                    existDoctor = doctorService.get(idDoctor);
                } while (existDoctor.isLeft());
                LocalDate date = null;
                while (date == null) {
                    try {
                        System.out.println("Enter the admissionDate of the medical record(AAAA-MM-DD): ");
                        String dateString = scanner.nextLine();
                        date = LocalDate.parse(dateString);
                    } catch (DateTimeParseException e) {
                        System.out.println("Error: The date you entered is not valid. Please enter the date in the format AAAA-MM-DD.");
                    }
                }
                System.out.println("Enter the diagnosis of the medical record: ");
                String diagnosis = scanner.nextLine();
                List<Prescribe> prescriptions = new ArrayList<>();
                System.out.println("Enter the medication: ");
                String name = scanner.nextLine();
                System.out.println("Enter the dose: ");
                String dosage = scanner.nextLine();
                Prescribe prescribe = new Prescribe(name, dosage);
                prescriptions.add(prescribe);
                System.out.println("Enter the medication: ");
                String name2 = scanner.nextLine();
                System.out.println("Enter the dose: ");
                String dosage2 = scanner.nextLine();
                Prescribe prescribe2 = new Prescribe(name2, dosage2);
                prescriptions.add(prescribe2);
                MedicalRecord medicalRecord = new MedicalRecord(0, idPatient, idDoctor, diagnosis, date, prescriptions);
                System.out.println("Items added: " + medicalRecordService.addMedicalRecord(medicalRecord));
            } else {
                System.out.println("Error: The patient does not exist");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exercise4() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, List<Patient>> data = patientService.getAllPatients();
            if (data.isRight()) {
                for (Patient patient : data.get()) {
                    System.out.println(patient);
                }
                Scanner scanner = new Scanner(System.in);
                Either<HospitalError, Patient> existPatient;
                int idPatient;
                do {
                    System.out.println("Enter the id of the patient: ");
                    idPatient = scanner.nextInt();
                    scanner.nextLine();
                    existPatient = patientService.get(idPatient);
                } while (existPatient.isLeft());
                Either<HospitalError, Integer> deletePatient = patientService.deletePatient(existPatient.get(), false);
                if (deletePatient.isRight()) {
                    System.out.println("Patient deleted");
                } else {
                    if (deletePatient.getLeft().getMessage().equals("The patient has medical records")) {
                        System.out.println("Do you want to delete the patient with all their data? (Y/N)");
                        String option = scanner.nextLine();
                        if (option.equalsIgnoreCase("Y"))
                            System.out.println(patientService.deletePatient(existPatient.get(), true));
                        else System.out.println("The patient was not deleted");
                    } else {
                        System.out.println(deletePatient.getLeft().getMessage());
                    }
                }
            } else {
                System.out.println(data.getLeft().getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exercise5(){
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println(patientService.getPatientWithMostMedicalRecords());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exercise6(){
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println(patientService.getDateWithMorePatients());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void continueConsole() {
        System.out.println(CommonConstants.CONTINUE);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private static void menu(int option, int idPatient) {
        switch (option) {
            case 1:
                exercise1(idPatient);
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
            default:
                System.out.println(CommonConstants.INVALID_PARAMETER);
                break;
        }
    }

    private static int getOption(boolean admin) {
        System.out.println("Unit 1 XML Exercises");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the exercise you want to run: ");
        if (!admin) {
            System.out.println("1. Show your medical records and all the prescribed medication");
        } else {
            System.out.println("1. Show medical records by patient and, when selecting one, show all the prescribed medication");
            System.out.println("2. Show the information of all patients, including the total amount paid");
            System.out.println("3. Append a new medical record with two medicines: Make sure that the patient and the medication exist");
            System.out.println("4. Delete a patient: If it has any medication, ask the user first, and if so, delete the patient with all their data");
            System.out.println("5. Find the patient with the most medical records");
            System.out.println("6. Find the date with more patients");
        }
        System.out.print("Write an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }
}
