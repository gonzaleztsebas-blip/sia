package sia.sia.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sia.sia.data.Course;
import sia.sia.data.Group;
import sia.sia.data.Student;

public class EnrollmentManager {

    private final static String ENROLLMENT_FILE_PATH = "src\\main\\resources\\dataBase\\enrollmentCSV.csv";
    private final static int MAX_CREDITS_PER_SEMESTER = 20;

    private static List<String[]> enrollments = loadEnrollments();

    // =========================================================
    // LOAD / RELOAD
    // =========================================================
    public static List<String[]> loadEnrollments() {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(ENROLLMENT_FILE_PATH))) {
            List<String[]> r = reader.readAll();
            if (r != null) rows = r;
        } catch (Exception e) {
            // archivo no existe, devolver vacío
        }
        return rows;
    }

    public static void reload() {
        enrollments = loadEnrollments();
    }

    public static void clearCache() {
        enrollments = new ArrayList<>();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(ENROLLMENT_FILE_PATH))) {
            w.write("");
        } catch (Exception ignored) {}
    }

    // =========================================================
    // ENROLL
    // =========================================================
    public static boolean enrollStudent(String studentUser, String groupNumber) {

        reload();

        System.out.println("\n=== INICIANDO INSCRIPCIÓN ===");

        // 1) Grupo existe
        Group group = GroupManager.findGroup(groupNumber);
        if (group == null) {
            System.out.println("El grupo no existe.");
            return false;
        }

        // 2) Estudiante existe
        Student student = StudentManager.findStudent(studentUser);
        if (student == null) {
            System.out.println("El estudiante no existe.");
            return false;
        }

        // 3) Ya inscrito
        if (isStudentEnrolled(studentUser, groupNumber)) {
            System.out.println("El estudiante YA está inscrito en este grupo.");
            return false;
        }

        // 4) Cupos
        int spots = GroupManager.getAvailableSpots(groupNumber);
        if (spots <= 0) {
            System.out.println("El grupo está lleno.");
            return false;
        }

        // 5) Cruce de horarios
        if (ScheduleValidator.hasScheduleConflict(studentUser, group)) {
            System.out.println("El estudiante tiene cruce de horario.");
            return false;
        }

        // 6) Prerrequisitos
        Course course = group.getRepresents();
        if (!student.meetsPrerequisites(course)) {
            System.out.println("NO cumple prerrequisitos.");
            return false;
        }

        // 7) Ya aprobó antes
        String codeStr = String.valueOf(course.getCode());
        List<String> passed = GradeManager.getPassedCourses(studentUser);
        if (passed.contains(codeStr)) {
            System.out.println("El estudiante ya aprobó este curso.");
            return false;
        }

        // 8) Créditos
        int[] current = calculateCurrentCredits(studentUser, group.getSemester());
        int[] newC = course.getCredits();
        int sum = 0;

        for (int i = 0; i < 4; i++) sum += current[i] + newC[i];

        if (sum > MAX_CREDITS_PER_SEMESTER) {
            System.out.println("Excede límite de créditos (" + sum + "/" + MAX_CREDITS_PER_SEMESTER + ")");
            return false;
        }

        // =============================
        // SI TODO PASÓ, HACER INSCRIPCIÓN
        // =============================
        boolean added = GroupManager.addStudent(groupNumber, studentUser);
        if (!added) {
            System.out.println("Error al añadir estudiante al grupo.");
            return false;
        }

        String[] row = {
            studentUser,
            groupNumber,
            group.getSemester(),
            java.time.LocalDate.now().toString(),
            "ACTIVE"
        };

        enrollments.add(row);

        if (!updateEnrollmentCSV()) {
            System.out.println("Error guardando CSV.");
            return false;
        }

        System.out.println("Inscripción EXITOSA.");
        return true;
    }

    // =========================================================
    // UNENROLL
    // =========================================================
    public static boolean unenrollStudent(String studentUser, String groupNumber) {

        reload();

        if (!isStudentEnrolled(studentUser, groupNumber)) {
            System.out.println("El estudiante NO está inscrito.");
            return false;
        }

        GroupManager.removeStudent(groupNumber, studentUser);

        for (String[] row : enrollments) {
            if (row.length >= 5 &&
                row[0].equals(studentUser) &&
                row[1].equals(groupNumber) &&
                "ACTIVE".equals(row[4])) {

                row[4] = "WITHDRAWN";
                break;
            }
        }

        if (updateEnrollmentCSV()) {
            System.out.println("Retiro exitoso.");
            return true;
        }

        System.out.println("Error guardando CSV.");
        return false;
    }

    // =========================================================
    // HELPERS
    // =========================================================
    public static boolean isStudentEnrolled(String studentUser, String groupNumber) {
        for (String[] row : enrollments) {
            if (row.length >= 5 &&
                row[0].equals(studentUser) &&
                row[1].equals(groupNumber) &&
                "ACTIVE".equals(row[4])) {

                return true;
            }
        }
        return false;
    }

    public static int[] calculateCurrentCredits(String studentUser, String semester) {
        int[] total = new int[4];

        for (String[] row : enrollments) {
            if (row.length >= 5 &&
                row[0].equals(studentUser) &&
                row[2].equals(semester) &&
                "ACTIVE".equals(row[4])) {

                Group g = GroupManager.findGroup(row[1]);
                if (g != null && g.getRepresents() != null) {
                    int[] c = g.getRepresents().getCredits();
                    for (int i = 0; i < 4; i++) total[i] += c[i];
                }
            }
        }

        return total;
    }

    // =========================================================
    // LIST
    // =========================================================
    public static List<Group> getCurrentEnrollments(String studentUser) {
        reload();
        List<Group> result = new ArrayList<>();

        for (String[] row : enrollments) {
            if (row.length >= 5 &&
                row[0].equals(studentUser) &&
                "ACTIVE".equals(row[4])) {

                Group g = GroupManager.findGroup(row[1]);
                if (g != null) result.add(g);
            }
        }

        return result;
    }

    // =========================================================
    // SAVE CSV
    // =========================================================
    private static boolean updateEnrollmentCSV() {
        File original = new File(ENROLLMENT_FILE_PATH);
        File tmp = new File(ENROLLMENT_FILE_PATH + ".tmp");

        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] row : enrollments) {
                if (row == null) continue;

                String[] safe = Arrays.copyOf(row, 5);
                for (int i = 0; i < safe.length; i++)
                    if (safe[i] == null) safe[i] = "";

                writer.writeNext(safe);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static List<String[]> getEnrollments() {
        return enrollments;
    }
}
