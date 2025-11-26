package sia.sia.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sia.sia.data.Course;

public class CourseManager {

    private static final String COURSES_FILE_PATH
            = "src/main/resources/dataBase/courseCSV.csv";

    private static List<String[]> courses = loadCourses();

    // ---------------------------------------------------------
    // ---------------------- LOAD CSV -------------------------
    // ---------------------------------------------------------
    private static List<String[]> loadCourses() {
        try (CSVReader reader = new CSVReader(new FileReader(COURSES_FILE_PATH))) {
            List<String[]> rows = reader.readAll();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void reload() {
        courses = loadCourses();
        System.out.println("Cursos recargados: " + courses.size());
    }

    public static List<String[]> getCourses() {
        return courses;
    }

    // ---------------------------------------------------------
    // ---------------------- CREATE ---------------------------
    // ---------------------------------------------------------
    public static void createCourse(String name, int[] credits, List<String> requisites) {

        if (findCourse(name) != null) {
            System.out.println("Error: Ese curso ya existe.");
            return;
        }

        CodeNumbersManager codeManager = new CodeNumbersManager();
        long code = codeManager.createNewCode();

        Course course = new Course(code, name, credits, requisites);
        String[] row = course.toArray(); // Usa el formato correcto

        courses.add(row);
        saveCSV();

        System.out.println("Curso creado correctamente.");
    }

    // ---------------------------------------------------------
    // ---------------------- UPDATE ---------------------------
    // ---------------------------------------------------------
    public static void updateCourse(long code, String newName,
            int[] newCredits, List<String> newRequisites) {

        for (String[] row : courses) {
            if (Long.parseLong(row[0]) == code) {

                row[1] = newName;
                row[2] = encodeCredits(newCredits);
                row[3] = encodeRequisites(newRequisites);

                saveCSV();
                System.out.println("Curso actualizado.");
                return;
            }
        }

        System.out.println("No existe el curso.");
    }

    // ---------------------------------------------------------
    // ---------------------- DELETE ---------------------------
    // ---------------------------------------------------------
    public static void deleteCourse(long code) {

        for (int i = 0; i < courses.size(); i++) {
            if (Long.parseLong(courses.get(i)[0]) == code) {
                courses.remove(i);
                saveCSV();
                System.out.println("Curso eliminado correctamente.");
                return;
            }
        }

        System.out.println("No existe el curso.");
    }

    // ---------------------------------------------------------
    // ------------------- FINDERS -----------------------------
    // ---------------------------------------------------------
    public static Course findCourse(String name) {
        for (String[] row : courses) {
            if (row[1].equalsIgnoreCase(name)) {
                return new Course(row);
            }
        }
        return null;
    }

    public static Course findCourse(long code) {
        for (String[] row : courses) {
            if (Long.parseLong(row[0]) == code) {
                return new Course(row);
            }
        }
        return null;
    }

    public static List<Course> getCoursesAsObjects() {
        List<Course> list = new ArrayList<>();
        for (String[] row : courses) {
            list.add(new Course(row));
        }
        return list;
    }

    // ---------------------------------------------------------
    // -------------------- REQUISITES -------------------------
    // ---------------------------------------------------------
    public static void addRequisite(long code, List<String> newReq) {

        for (String[] row : courses) {
            if (Long.parseLong(row[0]) == code) {

                List<String> reqList = new ArrayList<>();

                if (row[3] != null && !row[3].isBlank()) {
                    reqList.addAll(Arrays.asList(row[3].split(";")));
                }

                reqList.addAll(newReq);

                row[3] = encodeRequisites(reqList);

                saveCSV();
                System.out.println("Requisitos agregados.");
                return;
            }
        }

        System.out.println("No existe el curso.");
    }

    public static void listRequisites(long code) {
        Course c = findCourse(code);
        if (c == null) {
            System.out.println("Curso no encontrado.");
            return;
        }
        System.out.println(String.join(", ", c.getRequisites()));
    }

    // ---------------------------------------------------------
    // ----------------------- CSV -----------------------------
    // ---------------------------------------------------------
    private static void saveCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(COURSES_FILE_PATH))) {
            for (String[] row : courses) {
                writer.writeNext(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // ----------------- ENCODER HELPERS -----------------------
    // ---------------------------------------------------------
    private static String encodeCredits(int[] arr) {
        return arr[0] + ";" + arr[1] + ";" + arr[2] + ";" + arr[3];
    }

    private static String encodeRequisites(List<String> list) {
        return String.join(";", list);
    }

    public static void listCourses() {
        List<Course> courseObjects = getCoursesAsObjects();

        if (courseObjects.isEmpty()) {
            System.out.println("No hay cursos registrados.");
            return;
        }

        System.out.println("\n=== LISTA DE CURSOS ===");
        System.out.printf("%-10s %-30s %-15s %-15s %-15s %-15s%n",
                "CÓDIGO", "NOMBRE", "FUNDAMENTACIÓN", "DISCIPLINAR", "LIBRE ELEC.", "NIVELACIÓN");
        System.out.println("---------------------------------------------------------------------------------------------------");

        for (Course course : courseObjects) {
            int[] credits = course.getCredits();
            System.out.printf("%-10d %-30s %-15d %-15d %-15d %-15d%n",
                    course.getCode(),
                    course.getName().length() > 28 ? course.getName().substring(0, 25) + "..." : course.getName(),
                    credits[0], credits[1], credits[2], credits[3]);
        }
    }
}
