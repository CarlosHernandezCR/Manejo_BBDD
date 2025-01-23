package ui;

import common.config.ConfigurationTXT;
import common.config.ConfigurationXML;
import common.staticData.HospitalData;
import io.vavr.control.Either;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import model.Doctor;
import model.MedicalRecord;
import model.Patient;
import model.Prescribe;
import model.error.HospitalError;
import service.DoctorService;
import service.MedicalRecordService;
import service.PatientService;
import service.PrescribeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Unit1TXTExercises {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Unit 1 Exercises");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (username.equals(ConfigurationXML.getInstance().getProperty("username")) && password.equals(ConfigurationXML.getInstance().getProperty("password"))) {
            System.out.println("Welcome " + username);
            System.out.println("Cleaning all the files...");
            String[] names = {
                    "doctors",
                    "patients",
                    "medications",
                    "medicalRecords"
            };
            for (String files : names) {
                String path = ConfigurationTXT.getInstance().getProperty(files);
                Path filePath = Paths.get(path);
                try {
                    if (Files.exists(filePath)) {
                        Files.write(filePath, "".getBytes());
                        System.out.println("Content of file " + path + " has been deleted");
                    } else {
                        System.out.println("File " + path + " does not exist");
                    }
                } catch (IOException e) {
                    System.out.println("Error deleting content of file " + path);
                    e.printStackTrace();
                }
            }
            System.out.println("Press enter to continue...");
            scanner.nextLine();
            System.out.println("Writing in the files...");
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            MedicalRecordService medicalRecordService;
            try (SeContainer container = initializer.initialize()) {
                DoctorService doctorService = container.select(DoctorService.class).get();
                PatientService patientService = container.select(PatientService.class).get();
                PrescribeService prescribeService = container.select(PrescribeService.class).get();
                medicalRecordService = container.select(MedicalRecordService.class).get();
                for (Doctor doctor : HospitalData.doctors) {
                    doctorService.save(doctor);
                }
                for (Patient patient : HospitalData.patients) {
                    patientService.save(patient);
                }
                for (Prescribe prescribe : HospitalData.prescriptions) {
                    prescribeService.save(prescribe);
                }
                for (MedicalRecord medicalRecord : HospitalData.medicalRecords) {
                    medicalRecordService.save(medicalRecord);
                }
                System.out.println("Press enter to continue...");
                scanner.nextLine();
                System.out.println("Show medical records by Patient ID");
                System.out.println("Enter the Patient ID: ");
                int patientId = scanner.nextInt();
                scanner.nextLine();
                Either<HospitalError, List<MedicalRecord>> result = medicalRecordService.getByPatientId(patientId);
                if (result.isLeft()) {
                    System.out.println(result.getLeft().getMessage());
                } else {
                    for (MedicalRecord medicalRecord : result.get()) {
                        System.out.println("Patient with id " + patientId + ":");
                        System.out.println(medicalRecord);
                    }
                }
                System.out.println("Press enter to continue...");
                scanner.nextLine();
                System.out.println("Append a new medical record with two medications\nData will be: " +
                        "\n ID: 5" +
                        "\n Patient ID: 4" +
                        "\n Doctor ID: 2" +
                        "\n Diagnoses: semiLuxacion" +
                        "\n Admission Date: 2024-03-19");
                MedicalRecord newMedicalRecord = new MedicalRecord(5, 4, 2, "semiLuxacion", LocalDate.of(2024, 3, 19),new ArrayList<>());
                Either<HospitalError, Integer> result2 = medicalRecordService.save(newMedicalRecord);
                if (result2.isRight()) {
                    System.out.println("Medical record added successfully");
                } else {
                    System.out.println("Error adding medical record");
                }
                System.out.println("Press enter to continue...");
                scanner.nextLine();
                System.out.println("Delete a patient");
                System.out.println("Enter the Patient ID: ");
                int patientId2 = scanner.nextInt();
                scanner.nextLine();
                Either<HospitalError, List<MedicalRecord>> result3 = medicalRecordService.getByPatientId(patientId2);
                if (result3.isRight()) {
                    boolean hasMedicine = false;
                    for (MedicalRecord medicalRecord : result3.get()) {
                        if (prescribeService.getByMedicalRecordId(medicalRecord.getId()).isRight()) {
                            hasMedicine = true;
                            break;
                        }
                    }
                    if (hasMedicine) {
                        System.out.println("The patient has medicine, do you want to delete them? (y/n)");
                        String answer = scanner.next();
                        if (answer.equals("y")) {
                            if(patientService.get(patientId2).isRight()) {
                                medicalRecordService.delete(patientService.get(patientId2).get());
                            }
                            for (MedicalRecord medicalRecord : result3.get()) {
                                prescribeService.deleteByMedicalRecord(medicalRecord).isRight();
                            }
                        }
                    }
                    if(patientService.get(patientId2).isRight()) {
                        medicalRecordService.delete(patientService.get(patientId2).get());
                    }                }
                patientService.delete(patientId2);
            }

        } else {
            System.out.println("Incorrect username or password");
        }
    }
}
