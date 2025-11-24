package sia.sia.business;

import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import sia.sia.data.Student;
import sia.sia.business.CodeNumbersManager;

public class StudentManager {
    
    private final static String USER_FILE_PATH = "src\\main\\resources\\dataBase\\usersCSV.csv";
    private final static String STUDENT_FILE_PATH = "src\\main\\resources\\dataBase\\studentCSV.csv";

    // Lista en memoria - debe actualizarse cuando cambien los datos
    private static List<String[]> students = loadStudents();

    private static List<String[]> loadStudents() {
        try {
            CSVReader reader = new CSVReader(new FileReader(STUDENT_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // MÉTODO NUEVO: Limpiar cache (requerido por el test)
    public static void clearCache() {
        students = new ArrayList<>(); // Limpiar la lista en memoria
        // También limpiar archivos si es necesario
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_FILE_PATH))) {
            writer.write(""); // Archivo vacío
        } catch (Exception e) {
            // Ignore
        }
    }

    public static List<String[]> getStudents() {
        return students;
    }

    public static void createStudent(String user, String password,
            String firstName, String lastName, String birthDate) {

        Student existing = findStudent(user);
        if (existing != null) {
            return;
        }

        CodeNumbersManager idManager = new CodeNumbersManager();
        long id = idManager.createNewId();

        // Convertir a fila CSV - formato: [user, password, "student", id, firstName, lastName, birthDate, ""]
        String[] studentRow = new String[]{
            user, 
            password, 
            "student", 
            String.valueOf(id), 
            firstName, 
            lastName, 
            birthDate, 
            "" // attends (vacío por defecto)
        };
        
        students.add(studentRow);
        updateStudentCSV();
        updateUserCSV();
    }

    public static void deleteStudent(String user) {
        int index = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i)[0].equals(user)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return;
        }

        students.remove(index);
        updateStudentCSV();
        updateUserCSV();
    }

    public static void listStudents() {
        for (String[] row : students) {
            System.out.println(String.join(", ", row));
        }
    }

    public static Student findStudent(String username) {
        // Recargar estudiantes para asegurar datos actualizados
        students = loadStudents();
        
        for (String[] row : students) {
            if (row[0].equals(username)) {
                try {
                    return new Student(
                            row[0],     // user
                            row[1],     // password
                            Long.parseLong(row[3]), // id
                            row[4],     // firstName
                            row[5],     // lastName
                            row[6]      // birthDate
                    );
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    public static void printFindStudent(String username) {
        findStudent(username);
        // Method now silent
    }

    public static void updateStudent(String username, String newFirst, String newLast, String newBirthDate) {
        boolean updated = false;
        
        for (String[] row : students) {
            if (row[0].equals(username)) {
                row[4] = newFirst;
                row[5] = newLast;
                row[6] = newBirthDate;
                updated = true;
                break;
            }
        }
        
        if (updated) {
            updateStudentCSV();
            updateUserCSV();
        }
    }

    public static void updateStudentCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_FILE_PATH))) {
            for (String[] row : students) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (Exception e) {
            // Error updating students CSV
        }
    }
    
    public static void updateUserCSV() {
        try {
            // Leer todos los usuarios actuales
            List<String[]> allUsers = new ArrayList<>();

            try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
                allUsers = reader.readAll();
            } catch (Exception e) {
                allUsers = new ArrayList<>();
            }

            // 1. Primero eliminar todos los estudiantes existentes
            List<String[]> usersToKeep = new ArrayList<>();
            for (String[] user : allUsers) {
                if (user.length >= 3 && !user[2].equals("student")) {
                    usersToKeep.add(user); // Mantener solo no-estudiantes
                }
            }

            // 2. Luego agregar solo los estudiantes actuales
            for (String[] student : students) {
                usersToKeep.add(new String[]{student[0], student[1], student[2]});
            }

            // 3. Escribir todo de vuelta
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
        students = loadStudents();
    }
}