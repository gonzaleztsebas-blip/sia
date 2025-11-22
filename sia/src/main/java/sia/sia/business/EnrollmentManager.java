package sia.sia.business;

import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import sia.sia.data.Group;
import sia.sia.data.Student;
import sia.sia.data.Course;

/**
 * Manager para gestionar las inscripciones de estudiantes a grupos Incluye
 * todas las validaciones necesarias
 *
 * @author luzel
 */
public class EnrollmentManager {

    private final static String ENROLLMENT_FILE_PATH = "src\\main\\resources\\dataBase\\enrollmentCSV.csv";
    private final static int MAX_CREDITS_PER_SEMESTER = 20;

    private static List<String[]> enrollments = loadEnrollments();

    public static List<String[]> loadEnrollments() {
        try {
            CSVReader reader = new CSVReader(new FileReader(ENROLLMENT_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void clearCache() {
        enrollments = new ArrayList<>(); // Limpiar la lista en memoria
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENROLLMENT_FILE_PATH))) {
            writer.write(""); // Archivo vacío
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de inscripciones: " + e.getMessage());
        }
    }

    public static boolean enrollStudent(String studentUser, String groupNumber) {

        enrollments = loadEnrollments();

        System.out.println("\nIniciando proceso de inscripcion...");
        System.out.println("Estudiante: " + studentUser + " | Grupo: " + groupNumber);
        System.out.println("----------------------------------------");

        Group group = GroupManager.findGroup(groupNumber);
        if (group == null) {
            System.out.println("El grupo no existe.");
            return false;
        }

        Student student = StudentManager.findStudent(studentUser);
        if (student == null) {
            System.out.println("El estudiante no existe.");
            return false;
        }

        if (isStudentEnrolled(studentUser, groupNumber)) {
            System.out.println("El estudiante ya esta inscrito en este grupo.");
            return false;
        }

        int availableSpots = GroupManager.getAvailableSpots(groupNumber);
        if (availableSpots <= 0) {
            System.out.println("El grupo esta lleno. No hay cupos disponibles.");
            return false;
        }
        System.out.println("Cupos disponibles: " + availableSpots);

        if (ScheduleValidator.hasScheduleConflict(studentUser, group)) {
            System.out.println("El estudiante tiene cruce de horarios.");
            return false;
        }
        System.out.println("No hay cruce de horarios.");

        Course course = group.getRepresents();
        String courseCode = String.valueOf(course.getCode());

        if (!GradeManager.meetsPrerequisites(studentUser, courseCode)) {
            System.out.println("El estudiante no cumple con los requisitos del curso.");
            return false;
        }
        System.out.println("Cumple con los requisitos.");

        List<String> passedCourses = GradeManager.getPassedCourses(studentUser);
        if (passedCourses.contains(courseCode)) {
            System.out.println("El estudiante ya aprobo este curso.");
            return false;
        }
        System.out.println("No ha aprobado este curso anteriormente.");

        String semester = group.getSemester();
        int currentCredits = calculateCurrentCredits(studentUser, semester);
        int newCredits = currentCredits + course.getCredits();

        if (newCredits > MAX_CREDITS_PER_SEMESTER) {
            System.out.println("Excede el limite de creditos por semestre.");
            System.out.println("Creditos actuales: " + currentCredits);
            System.out.println("Creditos del curso: " + course.getCredits());
            System.out.println("Total: " + newCredits + " (maximo: " + MAX_CREDITS_PER_SEMESTER + ")");
            return false;
        }
        System.out.println("Creditos permitidos: " + newCredits + "/" + MAX_CREDITS_PER_SEMESTER);

        System.out.println("\nTodas las validaciones pasaron.");

        boolean addedToGroup = GroupManager.addStudent(groupNumber, studentUser);
        if (!addedToGroup) {
            System.out.println("Error al agregar estudiante al grupo.");
            return false;
        }

        String enrollmentDate = java.time.LocalDate.now().toString();
        String[] enrollmentRow = {
            studentUser,
            groupNumber,
            semester,
            enrollmentDate,
            "ACTIVE"
        };
        enrollments.add(enrollmentRow);
        updateEnrollmentCSV();

        System.out.println("----------------------------------------");
        System.out.println("Inscripcion exitosa.");
        System.out.println("----------------------------------------\n");

        return true;
    }

    public static boolean unenrollStudent(String studentUser, String groupNumber) {

        System.out.println("\nIniciando proceso de retiro...");

        if (!isStudentEnrolled(studentUser, groupNumber)) {
            System.out.println("El estudiante no esta inscrito en este grupo.");
            return false;
        }

        GroupManager.removeStudent(groupNumber, studentUser);

        for (String[] row : enrollments) {
            if (row[0].equals(studentUser) && row[1].equals(groupNumber) && row[4].equals("ACTIVE")) {
                row[4] = "WITHDRAWN";
                break;
            }
        }
        updateEnrollmentCSV();

        System.out.println("Estudiante retirado del grupo exitosamente.\n");
        return true;
    }

    public static boolean isStudentEnrolled(String studentUser, String groupNumber) {
        for (String[] row : enrollments) {
            if (row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                return true;
            }
        }
        return false;
    }

    public static int calculateCurrentCredits(String studentUser, String semester) {
        int totalCredits = 0;

        for (String[] row : enrollments) {
            if (row[0].equals(studentUser) && row[2].equals(semester) && row[4].equals("ACTIVE")) {

                Group group = GroupManager.findGroup(row[1]);
                if (group != null && group.getRepresents() != null) {
                    totalCredits += group.getRepresents().getCredits();
                }
            }
        }

        return totalCredits;
    }

    public static List<Group> getCurrentEnrollments(String studentUser) {
        List<Group> currentGroups = new ArrayList<>();

        for (String[] row : enrollments) {
            if (row[0].equals(studentUser) && row[4].equals("ACTIVE")) {

                Group group = GroupManager.findGroup(row[1]);
                if (group != null) {
                    currentGroups.add(group);
                }
            }
        }

        return currentGroups;
    }

    public static List<Group> getEnrollmentsBySemester(String studentUser, String semester) {
        List<Group> semesterGroups = new ArrayList<>();

        for (String[] row : enrollments) {
            if (row[0].equals(studentUser) && row[2].equals(semester)) {

                Group group = GroupManager.findGroup(row[1]);
                if (group != null) {
                    semesterGroups.add(group);
                }
            }
        }

        return semesterGroups;
    }

    public static List<Course> getAvailableCoursesForStudent(String studentUser) {
        List<Course> availableCourses = new ArrayList<>();
        List<String[]> allCourses = CourseManager.getCourses();

        for (String[] courseRow : allCourses) {
            String courseCode = courseRow[0];

            if (GradeManager.meetsPrerequisites(studentUser, courseCode)) {

                List<String> passedCourses = GradeManager.getPassedCourses(studentUser);
                if (!passedCourses.contains(courseCode)) {

                    Course course = CourseManager.findCourse(Long.parseLong(courseCode));
                    if (course != null) {
                        availableCourses.add(course);
                    }
                }
            }
        }

        return availableCourses;
    }

    public static void listCurrentEnrollments(String studentUser) {
        List<Group> enrollments = getCurrentEnrollments(studentUser);

        if (enrollments.isEmpty()) {
            System.out.println("El estudiante no tiene inscripciones activas.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("INSCRIPCIONES ACTUALES DE: " + studentUser);
        System.out.println("========================================");

        int totalCredits = 0;
        for (Group g : enrollments) {
            Course c = g.getRepresents();
            totalCredits += c.getCredits();

            System.out.printf("Grupo %d | %s (%s) | %d creditos | Semestre: %s%n",
                    g.getNumber(),
                    c.getName(),
                    c.getCode(),
                    c.getCredits(),
                    g.getSemester());

            String[] days = g.getDaysOfWeek();
            String[] times = g.getTimesOfDay();
            if (days != null && times != null) {
                System.out.print("   Horario: ");
                for (int i = 0; i < days.length && i < times.length; i++) {
                    System.out.print(days[i] + " " + times[i]);
                    if (i < days.length - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        }

        System.out.println("----------------------------------------");
        System.out.printf("TOTAL DE CREDITOS INSCRITOS: %d / %d%n", totalCredits, MAX_CREDITS_PER_SEMESTER);
        System.out.println("========================================\n");
    }

    public static void listAvailableCourses(String studentUser) {
        List<Course> available = getAvailableCoursesForStudent(studentUser);

        if (available.isEmpty()) {
            System.out.println("No hay cursos disponibles para inscribir en este momento.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("CURSOS DISPONIBLES PARA: " + studentUser);
        System.out.println("========================================");

        for (Course c : available) {
            System.out.printf("Codigo: %s | %s | %d creditos%n",
                    c.getCode(), c.getName(), c.getCredits());
        }

        System.out.println("========================================\n");
    }

    public static void listAllEnrollments() {
        if (enrollments.isEmpty()) {
            System.out.println("No hay inscripciones registradas.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("TODAS LAS INSCRIPCIONES");
        System.out.println("========================================");

        for (String[] row : enrollments) {
            System.out.printf("Estudiante: %s | Grupo: %s | Semestre: %s | Fecha: %s | Estado: %s%n",
                    row[0], row[1], row[2], row[3], row[4]);
        }

        System.out.println("========================================\n");
    }

    private static void updateEnrollmentCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ENROLLMENT_FILE_PATH))) {

            for (String[] row : enrollments) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> getEnrollments() {
        return enrollments;
    }

    // Para cada manager, agrega este método:
    public static void reload() {
        enrollments = loadEnrollments(); 
    }
}
