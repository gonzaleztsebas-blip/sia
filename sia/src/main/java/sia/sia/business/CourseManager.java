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
import sia.sia.data.Course;
import sia.sia.data.RandomNumbersManager;

/**
 *
 * @author luzel
 */
public class CourseManager {

    private final static String COURSES_FILE_PATH = "src\\main\\resources\\dataBase\\courseCSV.csv";

    // Cargar una sola vez
    private static List<String[]> courses = loadCourses();

    private static List<String[]> loadCourses() {
        try {
            CSVReader reader = new CSVReader(new FileReader(COURSES_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<String[]> getCourses() {
        return courses;
    }

    public static void createCourse(String name, int credits, List<String> requisites) {
        Course existing = findCourse(name);
        if (existing != null) {
            System.out.println("❌ Error: Ese curso ya existe.");
            return;
        }

        RandomNumbersManager codeManager = new RandomNumbersManager();
        long code = codeManager.createNewCode();

        Course course = new Course(code, name, credits, requisites);

        // Convertir a fila CSV
        courses.add(course.toArray());

        updateCourseCSV();

        System.out.println("✔ Curso creado correctamente.");
    }

    public static void updateCourse(long code, String newName, int newCredits, List<String> newRequisites) {
        for (String[] row : courses) {
            if (row[0].equals(String.valueOf(code))) {
                row[1] = newName;
                row[2] = "" + newCredits;
                row[3] = String.join(", ", newRequisites);
                updateCourseCSV();
                System.out.println("✔ Curso actualizado.");
                return;
            }
        }

        System.out.println("❌ No existe el estudiante.");
    }

    public static Course findCourse(String name) {
        for (String[] row : courses) {

            if (row.length < 4) {
                continue; // <-- evita reventar
            }
            if (row[1].equals(name)) {
                return new Course(
                        Long.parseLong(row[0]),
                        row[1],
                        Integer.parseInt(row[2]),
                        Arrays.asList(row[3].trim().split("\\s*;\\s*"))
                );
            }
        }
        return null;
    }

    public static Course findCourse(Long code) {
        for (String[] row : courses) {

            if (row.length < 4) {
                continue; // <-- evita reventar
            }
            if (row[0].equals(String.valueOf(code))) {
                return new Course(
                        Long.parseLong(row[0]),
                        row[1],
                        Integer.parseInt(row[2]),
                        Arrays.asList(row[3].trim().split("\\s*;\\s*"))
                );
            }
        }
        return null;
    }

    public static void listCourses() {
        for (String[] row : courses) {
            System.out.println(String.join(", ", row));
        }
    }

    public static void deleteCourse(Long code) {

        int index = -1;

        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i)[0].equals(String.valueOf(code))) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            System.out.println("❌ No existe el estudiante.");
            return;
        }

        courses.remove(index);
        updateCourseCSV();

        System.out.println("✔ Estudiante eliminado correctamente.");
    }

    public static void addRequisite(long code, String requisiteCode) {
        for (String[] row : courses) {
            if (row[0].equals(String.valueOf(code))) {

                List<String> updated = new ArrayList<>();

                if (row[3] != null && !row[3].isBlank()) {
                    updated.addAll(Arrays.asList(row[3].split(",")));
                }

                updated.add(requisiteCode.trim());

                row[3] = String.join(",", updated);

                updateCourseCSV();

                System.out.println("✔ Requisito agregado correctamente.");
                return;
            }
        }
    }

    public static void listRequisites(String code) {
        Course c = findCourse(code);
        System.out.println(String.join(", ", c.getRequisites()));
    }

    public static void updateCourseCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COURSES_FILE_PATH))) {

            for (String[] row : courses) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
