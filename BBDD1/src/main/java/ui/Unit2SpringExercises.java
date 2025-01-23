package ui;

import common.constants.CommonConstants;
import io.vavr.control.Either;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import model.MedicalRecord;
import model.Patient;
import model.PatientUIV2;
import model.Prescribe;
import model.error.HospitalError;
import service.MedicalRecordService;
import service.PatientService;
import service.PrescribeService;

import java.util.List;
import java.util.Scanner;

//Using the Hospital database, implement the following functionality with Spring template:
//1. Show the medications of each medical record
//2. Add a new medication to the newest medical record of a given patient.
//3. Modify the dosage of a prescribed medication
//4. Backup all medical records older than the current year into an XML file, including the
//information about the medications, and delete them from the database.
//5. Add some of the queries we have been working on:
// Show the name of the patients with prescribed with more than 400mg of Ibuprofen
// Show the name and the number of medications of each patient
public class Unit2SpringExercises {
    public static void main(String[] args) {
        int option = 0;
        while (option != 7) {
            option = getOption();
            menu(option);
            System.out.println(CommonConstants.CONTINUE);
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        }
    }

    private static void exercise1() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            PrescribeService prescribeService = container.select(PrescribeService.class).get();
            Either<HospitalError, List<MedicalRecord>> medicalRecord = medicalRecordService.getAllMedications();
            if (medicalRecord.isLeft()) {
                System.out.println(medicalRecord.getLeft().getMessage());
            } else {
                for (MedicalRecord record : medicalRecord.get()) {
                    System.out.println(record);
                    Either<HospitalError, List<Prescribe>> prescribes = prescribeService.getByMedicalRecordId(record.getId());
                    if (prescribes.isLeft()) {
                        System.out.println("No medications in this Medical Record");
                    } else {
                        for (Prescribe prescribe : prescribes.get()) {
                            System.out.println(prescribe);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void exercise2() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            PrescribeService prescribeService = container.select(PrescribeService.class).get();
            Either<HospitalError, Patient> patient;
            Scanner scanner = new Scanner(System.in);
            boolean added = false;
            while (!added) {
                do {
                    Either<HospitalError, List<Patient>> patients = patientService.getAllPatients();
                    if (patients.isLeft()) {
                        System.out.println(patients.getLeft().getMessage());
                        return;
                    } else {
                        for (Patient p : patients.get()) {
                            System.out.println(p);
                        }
                    }
                    System.out.println("Enter the id of the patient you want to add a medication to: ");
                    int idPatient = scanner.nextInt();
                    scanner.nextLine();
                    patient = patientService.get(idPatient);
                } while (patient.isLeft());
                System.out.println("Enter the name of the medication: ");
                String name = scanner.nextLine();
                System.out.println("Enter the dosage of the medication: ");
                String dosage = scanner.nextLine();
                Either<HospitalError,Boolean> data=prescribeService.addMedication(patient.get().getId(), name, dosage);
                if(data.isLeft()) {
                    System.out.println(data.getLeft().getMessage());
                } else {
                    added = data.get();
                    if (added) {
                        System.out.println("Medication added successfully");
                    } else {
                        System.out.println("The patient does not have a medical record to add the medication to.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void exercise3(){
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PrescribeService prescribeService = container.select(PrescribeService.class).get();
            Scanner scanner = new Scanner(System.in);
            boolean modified = false;
            while(!modified){
                System.out.println("Enter the id of the medication you want to modify: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter the new dosage: ");
                String dosage = scanner.nextLine();
                Either<HospitalError,Integer> data=prescribeService.modifyDosage(id, dosage);
                if(data.isLeft()) {
                    System.out.println(data.getLeft().getMessage());
                } else {
                    modified=true;
                    System.out.println("Dosage modified successfully");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void exercise4() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            Either<HospitalError, Integer> medicalRecord = medicalRecordService.backupDecrTime();
            if(medicalRecord.isLeft()) {
                System.out.println(medicalRecord.getLeft().getMessage());
            } else {
                System.out.println(medicalRecord.get() + " medical records and prescriptions backed up successfully and deleted from the database.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void exercise5(){
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, List<Patient>> patients = patientService.getPatientsByMedication("Ibuprofen", "400mg");
            if(patients.isLeft()) {
                System.out.println(patients.getLeft().getMessage());
            } else {
                for (Patient p : patients.get()) {
                    System.out.println(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void exercise6(){
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            Either<HospitalError, List<PatientUIV2>> patients = patientService.getPatientsAndMedications();
            if(patients.isLeft()) {
                System.out.println(patients.getLeft().getMessage());
            } else {
                for (PatientUIV2 p : patients.get()) {
                    System.out.println(p.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            default:
                System.out.println(CommonConstants.INVALID_PARAMETER);
                break;
        }
    }

    private static int getOption() {
        System.out.println("Unit 2 SPRING Exercises");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the exercise you want to run: ");
        System.out.println("1. Show the medications of each medical record");
        System.out.println("2. Add a new medication to the newest medical record of a given patient");
        System.out.println("3. Modify the dosage of a prescribed medication");
        System.out.println("4. Backup all medical records older than the current year into an XML file, including the information about the medications, and delete them from the database");
        System.out.println("5. Show the name of the patients with prescribed with more than 400mg of Ibuprofen");
        System.out.println("6. Show the name and the number of medications of each patient");
        System.out.println("7. Exit");
        System.out.print("Write an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }
}
