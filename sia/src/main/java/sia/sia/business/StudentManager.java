package sia.sia.business;

import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sia.sia.data.Student;
import sia.sia.data.RandomNumbersManager;

public class StudentManager {
    
    private final static String USER_FILE_PATH = "src\\main\\resources\\dataBase\\usersCSV.csv";
    private final static String STUDENT_FILE_PATH = "src\\main\\resources\\dataBase\\studentCSV.csv";

    // Cargar una sola vez
    private static List<String[]> students = loadStudents();

    private static List<String[]> loadStudents() {
        try {
            CSVReader reader = new CSVReader(new FileReader(STUDENT_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<String[]> getStudents() {
        return students;
    }

    public static void createStudent(String user, String password,
            String firstName, String lastName, String birthDate) {

        Student existing = findStudent(user);
        if (existing != null) {
            System.out.println("❌ Error: Ese usuario ya existe.");
            return;
        }

        RandomNumbersManager idManager = new RandomNumbersManager();
        long id = idManager.createNewId();

        // Crear estudiante
        Student s = new Student(user, password, id, firstName, lastName, birthDate);

        // Convertir a fila CSV
        students.add(s.toArray());

        updateStudentCSV();
        updateUserCSV();
        
        System.out.println("✔ Estudiante creado correctamente.");
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
            System.out.println("❌ No existe el estudiante.");
            return;
        }

        students.remove(index);
        updateStudentCSV();
        updateUserCSV();

        System.out.println("✔ Estudiante eliminado correctamente.");
    }

    public static void listStudents() {
        for (String[] row : students) {
            System.out.println(String.join(", ", row));
        }
    }

    public static Student findStudent(String username) {

        for (String[] row : students) {
            if (row[0].equals(username)) {
                // user, password, role, id, firstName, lastName, birthDate, attends
                return new Student(
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
    
    public static void printFindStudent(String username) {
        
        System.out.println("✔ Estudiante encontrado correctamente.");
        System.out.println(Arrays.toString(findStudent(username).toArray())); 
    }

    public static void updateStudent(String username, String newFirst, String newLast) {

        for (String[] row : students) {
            if (row[0].equals(username)) {
                row[4] = newFirst;
                row[5] = newLast;
                updateStudentCSV();
                updateUserCSV();
                System.out.println("✔ Estudiante actualizado.");
                return;
            }
        }

        System.out.println("❌ No existe el estudiante.");
    }

    public static void updateStudentCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_FILE_PATH))) {

            for (String[] row : students) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void updateUserCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {

            for (String[] row : students) {
                writer.write(String.join(",", row[0]+row[1]+row[2]));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
