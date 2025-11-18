/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.business;

import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sia.sia.data.Professor;
import sia.sia.data.RandomNumbersManager;

public class ProfessorManager {
    
    private final static String USER_FILE_PATH = "src\\main\\resources\\dataBase\\usersCSV.csv";
    private final static String PROFESSOR_FILE_PATH = "src\\main\\resources\\dataBase\\professorCSV.csv";

    // Cargar una sola vez
    private static List<String[]> professors = loadProfessors();

    private static List<String[]> loadProfessors() {
        try {
            CSVReader reader = new CSVReader(new FileReader(PROFESSOR_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<String[]> getProfessors() {
        return professors;
    }

    public static void createProfessor(String user, String password,
            String firstName, String lastName, String birthDate) {

        Professor existing = findProfessor(user);
        if (existing != null) {
            System.out.println("❌ Error: Ese usuario ya existe.");
            return;
        }

        RandomNumbersManager idManager = new RandomNumbersManager();
        long id = idManager.createNewId();

        // Crear estudiante
        Professor s = new Professor(user, password, id, firstName, lastName, birthDate);

        // Convertir a fila CSV
        professors.add(s.toArray());

        updateProfessorCSV();
        updateUserCSV();
        
        System.out.println("✔ Profesor creado correctamente.");
    }

    public static void deleteProfessor(String user) {

        int index = -1;

        for (int i = 0; i < professors.size(); i++) {
            if (professors.get(i)[0].equals(user)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("❌ No existe el profesor.");
            return;
        }

        professors.remove(index);
        updateProfessorCSV();
        updateUserCSV();

        System.out.println("✔ Profesor eliminado correctamente.");
    }

    public static void listProfessors() {
        for (String[] row : professors) {
            System.out.println(String.join(", ", row));
        }
    }

    public static Professor findProfessor(String username) {

        for (String[] row : professors) {
            if (row[0].equals(username)) {
                // user, password, role, id, firstName, lastName, birthDate, attends
                return new Professor(
                        row[0],     // user
                        row[1],     // password
                        Long.parseLong(row[3]), 
                        row[4], 
                        row[5], 
                        row[6]      // birthDate STRING (tu constructor debe soportar esto)
                );
            }
        }

        return null;
    }
    
    public static void printFindProfessor(String username) {
        
        System.out.println("✔ Profesor encontrado correctamente.");
        System.out.println(Arrays.toString(findProfessor(username).toArray())); 
    }

    public static void updateProfessor(String username, String newFirst, String newLast) {

        for (String[] row : professors) {
            if (row[0].equals(username)) {
                row[4] = newFirst;
                row[5] = newLast;
                updateProfessorCSV();
                updateUserCSV();
                System.out.println("✔ Profesor actualizado.");
                return;
            }
        }

        System.out.println("❌ No existe el profesor.");
    }

    public static void updateProfessorCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFESSOR_FILE_PATH))) {

            for (String[] row : professors) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateUserCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {

            for (String[] row : professors) {
                writer.write(String.join(",", row[0]+row[1]+row[2]));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
