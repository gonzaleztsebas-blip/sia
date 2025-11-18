/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import sia.sia.data.*;

public class CSVManager {

    private final static String USER_FILE_PATH = "src\\main\\resources\\dataBase\\usersCSV.csv";
    private final static String STUDENT_FILE_PATH = "src\\main\\resources\\dataBase\\studentCSV.csv";
    private final static String PROFESSOR_FILE_PATH = "src\\main\\resources\\dataBase\\professorCSV.csv";
    private final static String ADMIN_FILE_PATH = "src\\main\\resources\\dataBase\\adminCSV.csv";

    public static User login(String username, String password) {
        try {
            CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();

            for (String[] row : rows) {
                if (row[0].equals(username) && row[1].equals(password)) {
                    String role = row[2];
                    switch (role) {
                        case "admin":
                            return new Admin(username, password);
                        case "professor":
                            return new Professor(username, password);
                        case "student":
                            return new Student(username, password);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // usuario/contraseña incorrectos
    }

    public static boolean signUp(String username, String password, String role) {

        File file = new File(USER_FILE_PATH);

        try {
            // Validar si existe
            if (userExists(username)) {
                return false;
            }

            // try-with-resources -> cierre automático y seguro
            try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
                String[] data = {username, password, role};
                writer.writeNext(data);
            }
            addRoleToCSV();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Ver si un usuario ya está registrado
    public static boolean userExists(String username) {
        try {
            CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();

            for (String[] row : rows) {
                if (row[0].equals(username)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addRoleToCSV() {
        File fileS = new File(STUDENT_FILE_PATH);
        File fileP = new File(PROFESSOR_FILE_PATH);
        File fileA = new File(ADMIN_FILE_PATH);
        try {
            List<String[]> rows;
            try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
                rows = reader.readAll();
            }

            for (String[] row : rows) {
                if (row[2].equals("student")) {
                    try {
                        FileWriter outputfile = new FileWriter(fileS, true);

                        try (CSVWriter writer = new CSVWriter(outputfile)) {

                            String[] data1 = row;
                            writer.writeNext(data1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (row[2].equals("professor")) {
                    try {
                        FileWriter outputfile = new FileWriter(fileP, true);

                        try (CSVWriter writer = new CSVWriter(outputfile)) {

                            String[] data1 = row;
                            writer.writeNext(data1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (row[2].equals("admin")) {
                    try {
                        FileWriter outputfile = new FileWriter(fileA, true);

                        try (CSVWriter writer = new CSVWriter(outputfile)) {

                            String[] data1 = row;
                            writer.writeNext(data1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
