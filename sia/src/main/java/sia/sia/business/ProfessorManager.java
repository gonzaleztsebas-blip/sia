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
import java.util.List;
import sia.sia.data.Professor;
import sia.sia.business.CodeNumbersManager;

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
    
    // MÉTODO NUEVO: Limpiar cache (requerido por el test)
    public static void clearCache() {
        professors = new ArrayList<>(); // Limpiar la lista en memoria
        // También limpiar archivos si es necesario
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROFESSOR_FILE_PATH))) {
            writer.write(""); // Archivo vacío
        } catch (Exception e) {
            // Ignore
        }
    }
    
    public static List<String[]> getProfessors() {
        return professors;
    }

    public static void createProfessor(String user, String password,
            String firstName, String lastName, String birthDate) {

        Professor existing = findProfessor(user);
        if (existing != null) {
            return;
        }

        CodeNumbersManager idManager = new CodeNumbersManager();
        long id = idManager.createNewId();

        // Crear estudiante
        Professor s = new Professor(user, password, id, firstName, lastName, birthDate);

        // Convertir a fila CSV
        professors.add(s.toArray());

        updateProfessorCSV();
        updateUserCSV();
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
            return;
        }

        professors.remove(index);
        updateProfessorCSV();
        updateUserCSV();
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
        findProfessor(username);
    }

    public static void updateProfessor(String username, String newFirst, String newLast, String newBirthDate) {

        for (String[] row : professors) {
            if (row[0].equals(username)) {
                row[4] = newFirst;
                row[5] = newLast;
                row[6] = newBirthDate;
                updateProfessorCSV();
                updateUserCSV();
                return;
            }
        }

        // Professor not found
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
         try {
            List<String[]> allUsers = new ArrayList<>();

            try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
                allUsers = reader.readAll();
            } catch (Exception e) {
                allUsers = new ArrayList<>();
            }

            // 1. Eliminar todos los profesores existentes
            List<String[]> usersToKeep = new ArrayList<>();
            for (String[] user : allUsers) {
                if (user.length >= 3 && !user[2].equals("professor")) {
                    usersToKeep.add(user); // Mantener solo no-profesores
                }
            }

            // 2. Agregar solo los profesores actuales
            for (String[] professor : professors) {
                usersToKeep.add(new String[]{professor[0], professor[1], professor[2]});
            }

            // 3. Escribir
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
                for (String[] user : usersToKeep) {
                    writer.write(user[0] + "," + user[1] + "," + user[2]);
                    writer.newLine();
                }
            }

        } catch (Exception e) {
            // Error updating users CSV
        }
    }
    
    // MÉTODO NUEVO: Forzar recarga de estudiantes
    public static void reload() {
        professors = loadProfessors();
    }
}
