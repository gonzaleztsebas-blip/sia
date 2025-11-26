package sia.sia.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import sia.sia.data.Student;
import sia.sia.data.Course;
import sia.sia.data.Grade;

/**
 * StudentManager - versión profesional
 *
 * Schema studentCSV.csv (11 cols):
 * 0 user,1 password,2 role,3 id,4 firstName,5 lastName,
 * 6 approvedCredits ("a,b,c,d"),7 birthDate,8 attends ("g1;g2"),9 papa,10 pa
 *
 * Notas:
 * - approvedCredits está en row[6] (compatibilidad con GradeManager).
 * - clearCache() NO borra archivos; usar clearFilesForTests() para tests.
 * - Uso de CSVWriter/CSVReader y guardado seguro (tmp -> replace).
 */
public class StudentManager {

    private final static String USER_FILE_PATH = "src\\main\\resources\\dataBase\\usersCSV.csv";
    private final static String STUDENT_FILE_PATH = "src\\main\\resources\\dataBase\\studentCSV.csv";
    private static final double PASSING_GRADE = 3.0;

    // cache en memoria
    private static List<String[]> students = loadStudents();

    // ------------------- CARGA -------------------
    private static List<String[]> loadStudents() {
        try (CSVReader reader = new CSVReader(new FileReader(STUDENT_FILE_PATH))) {
            List<String[]> rows = reader.readAll();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void reload() {
        students = loadStudents();
    }

    /**
     * Limpia solo la cache en memoria.
     */
    public static void clearCache() {
        students = new ArrayList<>();
    }

    /**
     * BORRA archivos físicos (USAR SOLO EN TESTS).
     */
    public static void clearFilesForTests() {
        try {
            Files.deleteIfExists(new File(STUDENT_FILE_PATH).toPath());
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de estudiantes (tests): " + e.getMessage());
        }
    }

    // ------------------- ACCESO -------------------
    /**
     * Devuelve copia de la lista raw (evita modificaciones externas).
     */
    public static List<String[]> getStudents() {
        return new ArrayList<>(students);
    }

    // ------------------- CRUD -------------------
    public static boolean createStudent(String user, String password,
            String firstName, String lastName, String birthDate) {

        if (user == null || user.isBlank()) {
            System.out.println("ERROR: username inválido.");
            return false;
        }

        // recargar para evitar race conditions
        reload();

        // evitar duplicado
        if (findStudent(user) != null) {
            System.out.println("Error: Ese usuario ya existe.");
            return false;
        }

        CodeNumbersManager idManager = new CodeNumbersManager();
        long id = idManager.createNewId();

        // approvedCredits inicial
        String creditsInit = "0,0,0,0";

        // construir fila: asegúrate de respetar el schema
        String[] studentRow = new String[] {
            user,
            password,
            "student",
            String.valueOf(id),
            firstName == null ? "" : firstName,
            lastName == null ? "" : lastName,
            creditsInit,                 // index 6: approvedCredits
            birthDate == null ? "" : birthDate,
            "",                          // attends (index 8)
            "0.0",                       // papa (index 9)
            "0.0"                        // pa   (index 10)
        };

        students.add(studentRow);

        if (!saveStudentsCSV()) {
            System.out.println("ERROR: No se pudo guardar studentCSV.");
            return false;
        }

        if (!appendOrUpdateUserRow(user, password, "student")) {
            System.out.println("WARNING: estudiante creado pero fallo al actualizar usersCSV.");
        }

        System.out.println("Estudiante " + user + " creado correctamente.");
        return true;
    }

    public static boolean deleteStudent(String user) {
        reload();
        int idx = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).length > 0 && students.get(i)[0].equals(user)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            System.out.println("No existe el estudiante.");
            return false;
        }
        students.remove(idx);
        if (!saveStudentsCSV()) {
            System.out.println("ERROR: No se pudo actualizar studentCSV al eliminar.");
            return false;
        }
        // eliminar usuario de usersCSV también
        removeUserFromUsersCSV(user);
        System.out.println("Estudiante eliminado correctamente.");
        return true;
    }

    public static void listStudents() {
        reload();
        if (students.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
            return;
        }
        for (String[] row : students) {
            System.out.println(String.join(", ", normalizeRow(row, 11)));
        }
    }

    /**
     * Construye un objeto Student desde la fila CSV (usa el constructor Student(row) si existe).
     * Asume que la clase Student tiene un constructor: Student(String user, String password, long id, String firstName, String lastName, String birthDate, int[] approvedCredits)
     */
    public static Student findStudent(String user) {
        // no recargamos automáticamente (pero para compatibilidad hacemos reload primero)
        reload();
        for (String[] row : students) {
            if (row.length > 0 && row[0].equals(user)) {
                try {
                    String username = row[0];
                    String password = row.length > 1 ? row[1] : "";
                    long id = row.length > 3 && !row[3].isBlank() ? Long.parseLong(row[3]) : -1L;
                    String first = row.length > 4 ? row[4] : "";
                    String last = row.length > 5 ? row[5] : "";
                    // approvedCredits en index 6
                    int[] credits = new int[] {0,0,0,0};
                    if (row.length > 6 && row[6] != null && !row[6].isBlank()) {
                        String[] parts = row[6].split(",");
                        for (int i = 0; i < 4 && i < parts.length; i++) {
                            try { credits[i] = Integer.parseInt(parts[i].trim()); } catch (NumberFormatException ex) { credits[i] = 0; }
                        }
                    }
                    String birth = row.length > 7 ? row[7] : "";

                    // Preferimos usar constructor que recibe row si existe:
                    try {
                        return new Student(username, password, id, first, last, birth, credits);
                    } catch (NoSuchMethodError | Exception e) {
                        // fallback a constructor por fila si tu clase lo implementa
                        try {
                            return new Student(new String[] {username, password, "student", String.valueOf(id), first, last, row[6], birth, "", "0.0", "0.0"});
                        } catch (Exception ex) {
                            // si no existe, retornar null (pero informativo)
                            System.out.println("ERROR: No se pudo construir Student (verifica constructor).");
                            return null;
                        }
                    }
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static void printFindStudent(String username) {
        Student s = findStudent(username);
        if (s == null) {
            System.out.println("Estudiante no encontrado: " + username);
        } else {
            try {
                System.out.println(Arrays.toString(s.toArray()));
            } catch (Exception e) {
                System.out.println("Estudiante encontrado: " + s.getUser());
            }
        }
    }

    // ------------------- CRÉDITOS -------------------
    /**
     * Suma creditos aprobados (courseCredits: int[4]) al registro del estudiante.
     * Recalcula y fija el vector en row[6].
     */
    public static boolean updateApprovedCredits(String studentUser, int[] courseCredits) {
        if (courseCredits == null || courseCredits.length < 4) return false;

        reload();

        for (int i = 0; i < students.size(); i++) {
            String[] row = students.get(i);
            if (row.length > 0 && row[0].equals(studentUser)) {

                int[] currentCredits = new int[] {0,0,0,0};
                if (row.length > 6 && row[6] != null && !row[6].isBlank()) {
                    String[] parts = row[6].split(",");
                    for (int j = 0; j < 4 && j < parts.length; j++) {
                        try { currentCredits[j] = Integer.parseInt(parts[j].trim()); } catch (NumberFormatException ex) { currentCredits[j] = 0; }
                    }
                }

                for (int j = 0; j < 4; j++) currentCredits[j] += courseCredits[j];

                String creditsStr = Arrays.stream(currentCredits).mapToObj(String::valueOf).collect(Collectors.joining(","));
                if (row.length < 7) row = Arrays.copyOf(row, 11);
                row[6] = creditsStr;
                students.set(i, row);
                if (!saveStudentsCSV()) {
                    System.out.println("ERROR: No se pudo guardar studentCSV tras actualizar créditos.");
                    return false;
                }
                System.out.println("✓ Créditos aprobados actualizados para " + studentUser);
                System.out.println("  Nuevos créditos: " + Arrays.toString(currentCredits));
                return true;
            }
        }

        System.out.println("ERROR: Estudiante no encontrado: " + studentUser);
        return false;
    }

    /**
     * Retorna los créditos aprobados desde el CSV (int[4]).
     */
    public static int[] getApprovedCredits(String studentUser) {
        Student s = findStudent(studentUser);
        if (s != null) return s.getApprovedCredits();
        return new int[] {0,0,0,0};
    }

    // ------------------- ACTUALIZACIONES GENERALES -------------------
    public static boolean updateStudent(String username, String newPassword, String newFirst, String newLast, String newBirth) {
        reload();
        boolean updated = false;
        for (int i = 0; i < students.size(); i++) {
            String[] row = students.get(i);
            if (row.length > 0 && row[0].equals(username)) {
                if (row.length < 11) row = Arrays.copyOf(row, 11);
                if (newPassword != null) row[1] = newPassword;
                if (newFirst != null) row[4] = newFirst;
                if (newLast != null) row[5] = newLast;
                if (newBirth != null) row[7] = newBirth;
                students.set(i, row);
                updated = true;
                break;
            }
        }
        if (!updated) {
            System.out.println("No existe el estudiante.");
            return false;
        }
        if (!saveStudentsCSV()) {
            System.out.println("ERROR: No se pudo guardar cambios en studentCSV.");
            return false;
        }
        System.out.println("Estudiante actualizado.");
        return true;
    }

    // ------------------- CSV Helpers -------------------
    static boolean saveStudentsCSV() {
        File original = new File(STUDENT_FILE_PATH);
        File tmp = new File(STUDENT_FILE_PATH + ".tmp");

        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] row : students) {
                if (row == null) continue;
                String[] safe = normalizeRow(row, 11);
                writer.writeNext(safe);
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    private static String[] normalizeRow(String[] row, int cols) {
        String[] res = Arrays.copyOf(row, Math.max(row.length, cols));
        for (int i = 0; i < res.length; i++) if (res[i] == null) res[i] = "";
        return res;
    }

    // ------------------- usersCSV Helpers (sync) -------------------
    private static boolean appendOrUpdateUserRow(String username, String password, String role) {
        List<String[]> allUsers = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
            List<String[]> r = reader.readAll();
            if (r != null) allUsers = r;
        } catch (Exception e) {
            allUsers = new ArrayList<>();
        }

        boolean found = false;
        for (int i = 0; i < allUsers.size(); i++) {
            String[] u = allUsers.get(i);
            if (u.length > 0 && u[0].equals(username)) {
                allUsers.set(i, new String[] { username, password, role });
                found = true;
                break;
            }
        }
        if (!found) allUsers.add(new String[] { username, password, role });

        File original = new File(USER_FILE_PATH);
        File tmp = new File(USER_FILE_PATH + ".tmp");
        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] u : allUsers) writer.writeNext(normalizeRow(u, 3));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    private static void removeUserFromUsersCSV(String username) {
        List<String[]> allUsers = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
            List<String[]> r = reader.readAll();
            if (r != null) allUsers = r;
        } catch (Exception e) {
            return;
        }

        boolean changed = false;
        for (int i = 0; i < allUsers.size(); i++) {
            String[] u = allUsers.get(i);
            if (u.length > 0 && u[0].equals(username)) {
                allUsers.remove(i);
                changed = true;
                break;
            }
        }
        if (!changed) return;

        File original = new File(USER_FILE_PATH);
        File tmp = new File(USER_FILE_PATH + ".tmp");
        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] u : allUsers) writer.writeNext(normalizeRow(u, 3));
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // ------------------- PROMEDIOS -------------------
    public static void calculateAndUpdatePAPA(String studentUser) {
        List<Grade> allGrades = GradeManager.getGradesByStudent(studentUser);

        if (allGrades.isEmpty()) {
            System.out.println("El estudiante no tiene calificaciones registradas.");
            return;
        }

        double sumaPonderada = 0.0;
        int creditosTotales = 0;

        for (Grade grade : allGrades) {
            Course course = grade.getGroup().getRepresents();
            int[] courseCredits = course.getCredits();
            int totalCourseCredits = Arrays.stream(courseCredits).sum();

            sumaPonderada += grade.getGrade() * totalCourseCredits;
            creditosTotales += totalCourseCredits;
        }

        double papa = creditosTotales > 0 ? sumaPonderada / creditosTotales : 0.0;

        // Actualizar en el CSV
        updateStudentPAPA(studentUser, papa);

        System.out.println("✓ PAPA actualizado: " + String.format("%.2f", papa));
    }

    public static void calculateAndUpdatePA(String studentUser) {
        List<Grade> allGrades = GradeManager.getGradesByStudent(studentUser);

        if (allGrades.isEmpty()) {
            System.out.println("El estudiante no tiene calificaciones registradas.");
            return;
        }

        double sumaPonderada = 0.0;
        int creditosAprobados = 0;

        for (Grade grade : allGrades) {
            if (grade.getGrade() >= PASSING_GRADE) {
                Course course = grade.getGroup().getRepresents();
                int[] courseCredits = course.getCredits();
                int totalCourseCredits = Arrays.stream(courseCredits).sum();

                sumaPonderada += grade.getGrade() * totalCourseCredits;
                creditosAprobados += totalCourseCredits;
            }
        }

        double pa = creditosAprobados > 0 ? sumaPonderada / creditosAprobados : 0.0;

        // Actualizar en el CSV
        updateStudentPA(studentUser, pa);

        System.out.println("✓ PA actualizado: " + String.format("%.2f", pa));
    }

    public static void updateAllAverages(String studentUser) {
        List<Grade> allGrades = GradeManager.getGradesByStudent(studentUser);

        if (allGrades.isEmpty()) {
            System.out.println("El estudiante no tiene calificaciones registradas.");
            return;
        }

        double sumaPonderadaTotal = 0.0;
        int creditosTotales = 0;

        double sumaPonderadaAprobadas = 0.0;
        int creditosAprobados = 0;

        for (Grade grade : allGrades) {
            Course course = grade.getGroup().getRepresents();
            int[] courseCredits = course.getCredits();
            int totalCourseCredits = Arrays.stream(courseCredits).sum();

            sumaPonderadaTotal += grade.getGrade() * totalCourseCredits;
            creditosTotales += totalCourseCredits;

            if (grade.getGrade() >= PASSING_GRADE) {
                sumaPonderadaAprobadas += grade.getGrade() * totalCourseCredits;
                creditosAprobados += totalCourseCredits;
            }
        }

        double papa = creditosTotales > 0 ? sumaPonderadaTotal / creditosTotales : 0.0;
        double pa = creditosAprobados > 0 ? sumaPonderadaAprobadas / creditosAprobados : 0.0;

        updateStudentAverages(studentUser, papa, pa);

        System.out.println("✓ Promedios actualizados:");
        System.out.println("  PAPA (todas): " + String.format("%.2f", papa));
        System.out.println("  PA (aprobadas): " + String.format("%.2f", pa));
    }

    private static void updateStudentPAPA(String studentUser, double papa) {
        reload();
        for (int i = 0; i < students.size(); i++) {
            String[] row = students.get(i);
            if (row.length > 0 && row[0].equals(studentUser)) {
                if (row.length < 11) row = Arrays.copyOf(row, 11);
                row[9] = String.valueOf(papa);
                students.set(i, row);
                saveStudentsCSV();
                return;
            }
        }
    }

    private static void updateStudentPA(String studentUser, double pa) {
        reload();
        for (int i = 0; i < students.size(); i++) {
            String[] row = students.get(i);
            if (row.length > 0 && row[0].equals(studentUser)) {
                if (row.length < 11) row = Arrays.copyOf(row, 11);
                row[10] = String.valueOf(pa);
                students.set(i, row);
                saveStudentsCSV();
                return;
            }
        }
    }

    private static void updateStudentAverages(String studentUser, double papa, double pa) {
        reload();
        for (int i = 0; i < students.size(); i++) {
            String[] row = students.get(i);
            if (row.length > 0 && row[0].equals(studentUser)) {
                if (row.length < 11) row = Arrays.copyOf(row, 11);
                row[9] = String.valueOf(papa);
                row[10] = String.valueOf(pa);
                students.set(i, row);
                saveStudentsCSV();
                return;
            }
        }
    }

    // ------------------- REPORTES -------------------
    public static void printStudentReport(String studentUser) {
        Student student = findStudent(studentUser);
        if (student == null) {
            System.out.println("Estudiante no encontrado.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║        REPORTE ACADÉMICO DEL ESTUDIANTE                ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println("Nombre: " + student.getFirstName() + " " + student.getLastName());
        System.out.println("Usuario: " + student.getUser());
        System.out.println("ID: " + student.getId());

        System.out.println("\n--- CRÉDITOS APROBADOS ---");
        int[] credits = student.getApprovedCredits();
        System.out.println("Fundamentación: " + credits[Course.FUNDAMENTACION]);
        System.out.println("Disciplinar: " + credits[Course.DISCIPLINAR]);
        System.out.println("Libre elección: " + credits[Course.LIBRE_ELECCION]);
        System.out.println("Nivelación: " + credits[Course.NIVELACION]);
        System.out.println("TOTAL: " + student.getTotalApprovedCredits());

        System.out.println("\n--- PROMEDIOS ---");
        System.out.println("PAPA (todas las materias): " + String.format("%.2f", student.getPapa()));
        System.out.println("PA (solo aprobadas): " + String.format("%.2f", student.getPa()));

        List<Grade> grades = GradeManager.getGradesByStudent(studentUser);
        if (!grades.isEmpty()) {
            System.out.println("\n--- CALIFICACIONES ---");
            for (Grade grade : grades) {
                Course course = grade.getGroup().getRepresents();
                String status = grade.getGrade() >= PASSING_GRADE ? "APROBADO" : "REPROBADO";
                System.out.printf("%-30s | Nota: %.2f | %s%n",
                        course.getName(),
                        grade.getGrade(),
                        status);
            }
        }

        System.out.println("════════════════════════════════════════════════════════\n");
    }

    // ------------------- DIAGNÓSTICO -------------------
    public static void debugStudentsInMemory() {
        System.out.println("=== STUDENTS IN MEMORY ===");
        for (String[] r : students) {
            System.out.println(Arrays.toString(normalizeRow(r, 11)));
        }
    }
}
