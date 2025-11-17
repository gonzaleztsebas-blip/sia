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
    
    public static boolean login(String username, String password) {
        try {
            CSVReader reader = new CSVReader(new FileReader("src\\main\\resources\\dataBase\\usersCSV.csv"));
            List<String[]> rows = reader.readAll();
            reader.close();

            for (String[] row : rows) {
                if (row[0].equals(username) && row[1].equals(password)) {
                    return true; // login correcto
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // usuario/contraseña incorrectos
    }
    
    public static boolean signUp(String username, String password) {

        File file = new File("src/main/resources/dataBase/usersCSV.csv");

        try {
            // Validar si existe
            if (userExists(username)) {
                return false;
            }

            // try-with-resources -> cierre automático y seguro
            try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
                String[] data = { username, password };
                writer.writeNext(data);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Ver si un usuario ya está registrado
    public static boolean userExists(String username) {
        try {
            CSVReader reader = new CSVReader(new FileReader("src\\main\\resources\\dataBase\\usersCSV.csv"));
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

    public static void addStudentToCSV(Student student) {

        File file = new File("src\\main\\resources\\dataBase\\studentsCSV.csv");
        try {
            FileWriter outputfile = new FileWriter(file, true);

            try (CSVWriter writer = new CSVWriter(outputfile)) {

                String[] data1 = student.toArray();
                writer.writeNext(data1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addProfessorToCSV(Professor professor) {

        File file = new File("src\\main\\resources\\dataBase\\professorsCSV.csv");
        try {
            FileWriter outputfile = new FileWriter(file, true);

            try (CSVWriter writer = new CSVWriter(outputfile)) {

                String[] data1 = professor.toArray();
                writer.writeNext(data1);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
