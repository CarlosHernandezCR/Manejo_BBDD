package ui;

import common.config.ConfigurationXML;
import common.constants.CommonConstants;
import common.constants.PatientsConstants;
import io.vavr.control.Either;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import model.Patient;
import model.Prescribe;
import model.XML.MedicalRecordXML;
import model.XML.PrescribeXML;
import model.error.HospitalError;
import service.MedicalRecordService;
import service.PatientService;
import service.PrescribeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Unit1XMLExercises {
    public static void main(String[] args) {
        int option = 0;
        while(option!=6){
            option = getOption();
            menu(option);
            System.out.println(CommonConstants.CONTINUE);
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
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
            default:
                System.out.println(CommonConstants.INVALID_PARAMETER);
                break;
        }
    }

    private static int getOption() {
        System.out.println("Unit 1 XML Exercises");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of the exercise you want to run: ");
        System.out.println("1. Create a XML file with the information of a Patient");
        System.out.println("2. Medications of a Patient");
        System.out.println("3. Patients medicated with Amoxicilina");
        System.out.println("4. Append a new medical order to a given patient");
        System.out.println("5. Delete a patient");
        System.out.println("6. Exit");
        System.out.print("Write an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        return option;
    }
    private static void exercise1() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            String path = ConfigurationXML.getInstance().getProperty(PatientsConstants.PATIENTS_FILE);
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
            System.out.println("Press enter to start saving in XML...");
            if(patientService.transformTXTintoXML().isRight()){
                System.out.println("The information has been saved in XML");
            }else{
                System.out.println("Error saving the information in XML");
            }
        }
    }
    private static void exercise2() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PrescribeService prescribeService = container.select(PrescribeService.class).get();
            System.out.println("Enter the id of the patient: ");
            Scanner scanner = new Scanner(System.in);
            int id = scanner.nextInt();
            Either<HospitalError, List<Prescribe>> data= prescribeService.getMedicationsOfPatient(id);
            if(data.isRight()){
                List<Prescribe> prescriptions = data.get();
                for (Prescribe prescription : prescriptions) {
                    System.out.println(prescription);
                }
            }else{
                System.out.println(data.getLeft());
            }
        }
    }
    private static void exercise3() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println("Patients medicated with Amoxicilina");
            Either<HospitalError, List<Patient>> data = patientService.getPatientWithMedication("Amoxicilina");
            if (data.isRight()) {
                List<Patient> patients = data.get();
                for (Patient patient : patients) {
                    System.out.println(patient);
                }
            } else {
                System.out.println(data.getLeft());
            }
        }
    }
    private static void exercise4() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            MedicalRecordService medicalRecordService = container.select(MedicalRecordService.class).get();
            System.out.println("Append a new medical order to a given patient");
            System.out.println("Enter the id of the patient: ");
            Scanner scanner = new Scanner(System.in);
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter the doctor's name: ");
            String doctor = scanner.nextLine();
            System.out.println("Enter the diagnosis: ");
            String diagnosis = scanner.nextLine();
            List<PrescribeXML> prescriptions = new ArrayList<>();
            boolean addMore = true;
            while (addMore) {
                System.out.println("Enter the medication: ");
                String name = scanner.nextLine();
                System.out.println("Enter the dose: ");
                String dosage = scanner.nextLine();
                PrescribeXML prescribe = new PrescribeXML(name, dosage);
                prescriptions.add(prescribe);
                System.out.println("Do you want to add another medication? (Y/N)");
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("N")) {
                    addMore = false;
                }
            }

            Either<HospitalError, Integer> data =  medicalRecordService.addMedicalRecordXML(id,new MedicalRecordXML(doctor,diagnosis,prescriptions));
            if (data.isRight()) {
                System.out.println("Prescription added successfully");
            } else {
                System.out.println(data.getLeft());
            }
        }
    }
    private static void exercise5() {
        SeContainerInitializer initializer = SeContainerInitializer.newInstance();
        try (SeContainer container = initializer.initialize()) {
            PatientService patientService = container.select(PatientService.class).get();
            System.out.println("Delete a patient");
            System.out.println("Enter the id of the patient: ");
            Scanner scanner = new Scanner(System.in);
            int id = scanner.nextInt();
            Either<HospitalError, Integer> data = patientService.deletePatient(id);
            if (data.isRight()) {
                System.out.println("Patient deleted successfully");
            } else {
                System.out.println(data.getLeft());
            }
        }
    }
}