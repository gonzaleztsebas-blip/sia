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
import sia.sia.data.Course;
import sia.sia.data.Grade;
import sia.sia.data.Group;
import sia.sia.data.Student;

public class GradeManager {

    private final static String GRADE_FILE_PATH = "src\\main\\resources\\dataBase\\gradeCSV.csv";
    private final static double PASSING_GRADE = 3.0;

    private static List<String[]> grades = loadGrades();

    // ------------------ CARGA / RELOAD ------------------
    public static List<String[]> loadGrades() {
        List<String[]> rows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(GRADE_FILE_PATH))) {
            List<String[]> r = reader.readAll();
            if (r != null) {
                rows = r;
            }
        } catch (Exception e) {
            // Archivo puede no existir aun; retornamos lista vacía
        }
        return rows;
    }

    public static void reload() {
        grades = loadGrades();
    }

    public static void clearCache() {
        grades = new ArrayList<>();
        try (FileWriter fw = new FileWriter(GRADE_FILE_PATH)) {
            fw.write("");
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de calificaciones: " + e.getMessage());
        }
    }

    // ------------------ CRUD de NOTAS ------------------
    /**
     * Crea una nueva calificación. - Valida existencia de estudiante y grupo. -
     * Evita duplicados (si ya existe una nota para student/group, la rechaza).
     * - Si la nota es aprobatoria y antes no existía, actualiza créditos
     * aprobados.
     */
    public static boolean createGrade(String studentUser, String groupNumber, double gradeValue) {
        // Validaciones básicas
        if (gradeValue < 0.0 || gradeValue > 5.0) {
            System.out.println("ERROR: La calificación debe estar entre 0.0 y 5.0");
            return false;
        }

        Student student = StudentManager.findStudent(studentUser);
        if (student == null) {
            System.out.println("ERROR: Estudiante no encontrado: " + studentUser);
            return false;
        }

        Group group = GroupManager.findGroup(groupNumber);
        if (group == null) {
            System.out.println("ERROR: Grupo no encontrado: " + groupNumber);
            return false;
        }

        // Evitar duplicado
        Grade existing = findGrade(studentUser, groupNumber);
        if (existing != null) {
            System.out.println("ERROR: Ya existe una calificación para ese estudiante en ese grupo. Use updateGrade.");
            return false;
        }

        // Agregar a memoria
        String[] row = new String[]{studentUser, groupNumber, String.valueOf(gradeValue)};
        grades.add(row);

        // Guardar
        if (!saveGradesToCSV()) {
            System.out.println("ERROR: No se pudo guardar la calificación en CSV.");
            return false;
        }

        // Si aprobó, agregar créditos al estudiante (recalcular y fijar)
        if (gradeValue >= PASSING_GRADE) {
            recomputeAndSetApprovedCredits(studentUser);
            System.out.println("✓ Curso aprobado - créditos recalculados para " + studentUser);
        }

        System.out.println("✓ Calificación creada correctamente.");
        return true;
    }

    /**
     * Actualiza una calificación existente. Maneja cambios aprobada reprobada y
     * recalcula créditos aprobados del estudiante.
     */
    public static boolean updateGrade(String studentUser, String groupNumber, double newGrade) {
        if (newGrade < 0.0 || newGrade > 5.0) {
            System.out.println("ERROR: La calificación debe estar entre 0.0 y 5.0");
            return false;
        }

        boolean found = false;
        double oldGradeValue = -1.0;

        for (String[] row : grades) {
            if (row.length >= 3 && row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                try {
                    oldGradeValue = Double.parseDouble(row[2]);
                } catch (NumberFormatException e) {
                    oldGradeValue = -1.0;
                }
                row[2] = String.valueOf(newGrade);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("ERROR: No existe calificación para actualizar (student/group).");
            return false;
        }

        if (!saveGradesToCSV()) {
            System.out.println("ERROR: No se pudo guardar la actualización en CSV.");
            return false;
        }

        // Si hubo cambio de estado (reprobado -> aprobado) o (aprobado -> reprobado)
        boolean wasPassing = oldGradeValue >= PASSING_GRADE;
        boolean isPassing = newGrade >= PASSING_GRADE;

        if (wasPassing != isPassing) {
            // Recalcular y fijar créditos aprobados del estudiante
            recomputeAndSetApprovedCredits(studentUser);
            System.out.println("✓ Créditos recalculados tras cambio de estado de la nota.");
        }

        System.out.println("✓ Calificación actualizada correctamente.");
        return true;
    }

    /**
     * Elimina una calificación. Tras la eliminación recalculamos créditos
     * aprobados.
     */
    public static boolean deleteGrade(String studentUser, String groupNumber) {
        boolean removed = false;

        for (int i = 0; i < grades.size(); i++) {
            String[] row = grades.get(i);
            if (row.length >= 3 && row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                grades.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            System.out.println("ERROR: No existe la calificación para eliminar.");
            return false;
        }

        if (!saveGradesToCSV()) {
            System.out.println("ERROR: No se pudo actualizar el CSV tras eliminar la nota.");
            return false;
        }

        // Recalcular créditos aprobados del estudiante
        recomputeAndSetApprovedCredits(studentUser);
        System.out.println("✓ Calificación eliminada y créditos recalculados.");

        return true;
    }

    // ------------------ BÚSQUEDAS / REPORTES ------------------
    /**
     * Busca una calificación concreta (objeto Grade) en memoria.
     */
    public static Grade findGrade(String studentUser, String groupNumber) {
        for (String[] row : grades) {
            if (row.length >= 3 && row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                try {
                    double val = Double.parseDouble(row[2]);
                    Student s = StudentManager.findStudent(row[0]);
                    Group g = GroupManager.findGroup(row[1]);
                    if (s != null && g != null) {
                        return new Grade(s, g, val);
                    }
                } catch (NumberFormatException e) {
                    // ignora fila corrupta
                }
            }
        }
        return null;
    }

    /**
     * Obtiene todas las calificaciones de un estudiante (lista de Grade).
     */
    public static List<Grade> getGradesByStudent(String studentUsername) {
        List<Grade> result = new ArrayList<>();
        for (String[] row : grades) {
            if (row.length >= 3 && row[0].equals(studentUsername)) {
                try {
                    double val = Double.parseDouble(row[2]);
                    Student s = StudentManager.findStudent(row[0]);
                    Group g = GroupManager.findGroup(row[1]);
                    if (s != null && g != null) {
                        result.add(new Grade(s, g, val));
                    }
                } catch (NumberFormatException e) {
                    // fila corrupta -> skip
                }
            }
        }
        return result;
    }

    /**
     * Obtiene todas las calificaciones de un grupo.
     */
    public static List<Grade> getGradesByGroup(String groupNumber) {
        List<Grade> result = new ArrayList<>();
        for (String[] row : grades) {
            if (row.length >= 3 && row[1].equals(groupNumber)) {
                try {
                    double val = Double.parseDouble(row[2]);
                    Student s = StudentManager.findStudent(row[0]);
                    Group g = GroupManager.findGroup(row[1]);
                    if (s != null && g != null) {
                        result.add(new Grade(s, g, val));
                    }
                } catch (NumberFormatException e) {
                    // skip
                }
            }
        }
        return result;
    }

    /**
     * Lista todas las calificaciones en consola.
     */
    public static void listAllGrades() {
        if (grades.isEmpty()) {
            System.out.println("❌ No hay calificaciones registradas.");
            return;
        }
        System.out.println("=== TODAS LAS CALIFICACIONES ===");
        for (String[] row : grades) {
            if (row.length >= 3) {
                System.out.printf("Estudiante: %s | Grupo: %s | Nota: %s%n", row[0], row[1], row[2]);
            }
        }
        System.out.println("================================");
    }

    // ------------------ CÁLCULOS ------------------
    public static double calculateStudentAverage(String studentUsername) {
        List<Grade> gs = getGradesByStudent(studentUsername);
        if (gs.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Grade g : gs) {
            sum += g.getGrade();
        }
        return sum / gs.size();
    }

    public static double calculateGroupAverage(String groupNumber) {
        List<Grade> gs = getGradesByGroup(groupNumber);
        if (gs.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Grade g : gs) {
            sum += g.getGrade();
        }
        return sum / gs.size();
    }

    public static boolean hasPassed(String studentUser, String groupNumber) {
        Grade g = findGrade(studentUser, groupNumber);
        if (g == null) {
            return false;
        }
        return g.getGrade() >= PASSING_GRADE;
    }

    /**
     * Devuelve los códigos de curso aprobados por el estudiante.
     */
    public static List<String> getPassedCourses(String studentUser) {
        List<String> passed = new ArrayList<>();
        for (Grade g : getGradesByStudent(studentUser)) {
            if (g.getGrade() >= PASSING_GRADE) {
                String courseCode = String.valueOf(g.getGroup().getRepresents().getCode());
                if (!passed.contains(courseCode)) {
                    passed.add(courseCode);
                }
            }
        }
        return passed;
    }

    // ------------------ UTIL: RECOMPUTAR Y FIJAR CRÉDITOS ------------------
    /**
     * Recalcula los créditos aprobados por un estudiante (sumando créditos de
     * cursos aprobados) y escribe ese valor en el CSV de estudiantes.
     *
     * Atención: este método ADAPTA el CSV del StudentManager. Asume que la
     * columna de créditos aprobados en studentCSV está en la posición que
     * maneja StudentManager (row[6] en su lógica actual).
     */
    private static void recomputeAndSetApprovedCredits(String studentUser) {
        try {
            // 1) obtener lista de cursos aprobados
            List<String> passed = getPassedCourses(studentUser);

            // 2) sumar créditos por tipo
            int[] totals = new int[]{0, 0, 0, 0};
            for (String courseCodeStr : passed) {
                try {
                    long code = Long.parseLong(courseCodeStr);
                    Course course = CourseManager.findCourse(code);
                    if (course != null) {
                        int[] c = course.getCredits();
                        for (int i = 0; i < 4 && i < c.length; i++) {
                            totals[i] += c[i];
                        }
                    }
                } catch (NumberFormatException ex) {
                    // skip
                }
            }

            // 3) actualizar StudentManager: fijar los créditos aprobados (como "a,b,c,d")
            String creditsStr = totals[0] + "," + totals[1] + "," + totals[2] + "," + totals[3];

            // Manipulamos la lista en StudentManager directamente (esa clase
            // expone getStudents() y updateStudentCSV() en tu proyecto).
            List<String[]> students = StudentManager.getStudents();
            boolean updated = false;
            for (int i = 0; i < students.size(); i++) {
                String[] row = students.get(i);
                if (row.length >= 1 && row[0].equals(studentUser)) {
                    // Asegurarnos que el array tiene al menos 7 posiciones
                    if (row.length < 7) {
                        row = Arrays.copyOf(row, 7);
                    }
                    row[6] = creditsStr; // index 6: créditos aprobados (según StudentManager)
                    students.set(i, row);
                    updated = true;
                    break;
                }
            }

            if (updated) {
                StudentManager.saveStudentsCSV();
            } else {
                // Si el estudiante no aparece en la lista (raro), forzamos recarga
                System.out.println("WARNING: No se encontró estudiante en StudentManager al recomputar créditos: " + studentUser);
            }

        } catch (Exception e) {
            System.out.println("ERROR al recalcular créditos aprobados: " + e.getMessage());
        }
    }

    // ------------------ GUARDADO SEGURO CSV ------------------
    /**
     * Guarda la lista 'grades' en el CSV usando archivo temporal y replace.
     */
    private static boolean saveGradesToCSV() {
        File original = new File(GRADE_FILE_PATH);
        File tmp = new File(GRADE_FILE_PATH + ".tmp");

        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] row : grades) {
                if (row == null) {
                    continue;
                }
                String[] safe = Arrays.copyOf(row, Math.max(row.length, 3));
                for (int i = 0; i < safe.length; i++) {
                    if (safe[i] == null) {
                        safe[i] = "";
                    }
                }
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

    // ------------------ DIAGNÓSTICO ------------------
    public static void debugFileContent() {
        System.out.println("=== CONTENIDO EN MEMORIA (grades) ===");
        for (String[] r : grades) {
            System.out.println(Arrays.toString(r));
        }
    }

    // ------------------ ACCESO ------------------
    public static List<String[]> getGradesRaw() {
        return grades;
    }

    // =========================================================
    // LIST GROUP GRADES
    // =========================================================
    public static void listGroupGrades(String groupNumber) {
        reload();

        // Verificar que el grupo existe
        Group group = GroupManager.findGroup(groupNumber);
        if (group == null) {
            System.out.println("ERROR: Grupo no encontrado: " + groupNumber);
            return;
        }

        List<Grade> groupGrades = getGradesByGroup(groupNumber);

        if (groupGrades.isEmpty()) {
            System.out.println("No hay calificaciones registradas para el grupo: " + groupNumber);
            return;
        }

        System.out.println("\n=== CALIFICACIONES DEL GRUPO " + groupNumber + " ===");
        System.out.printf("%-20s %-15s %-10s %-10s%n",
                "ESTUDIANTE", "CURSO", "NOTA", "ESTADO");
        System.out.println("----------------------------------------------------------");

        Course course = group.getRepresents();
        String courseInfo = course != null
                ? course.getCode() + " - " + course.getName() : "N/A";

        for (Grade grade : groupGrades) {
            String studentName = grade.getStudent().getFirstName();
            double gradeValue = grade.getGrade();
            String status = gradeValue >= PASSING_GRADE ? "APROBADO" : "REPROBADO";

            System.out.printf("%-20s %-15s %-10.2f %-10s%n",
                    studentName,
                    courseInfo.length() > 15 ? courseInfo.substring(0, 12) + "..." : courseInfo,
                    gradeValue,
                    status);
        }

        // Mostrar promedio del grupo
        double average = calculateGroupAverage(groupNumber);
        System.out.println("----------------------------------------------------------");
        System.out.printf("PROMEDIO DEL GRUPO: %.2f%n", average);
        System.out.printf("ESTUDIANTES EVALUADOS: %d%n", groupGrades.size());
    }

    // =========================================================
    // LIST STUDENT GRADES
    // =========================================================
    public static void listStudentGrades(String studentUser) {
        reload();

        // Verificar que el estudiante existe
        Student student = StudentManager.findStudent(studentUser);
        if (student == null) {
            System.out.println("ERROR: Estudiante no encontrado: " + studentUser);
            return;
        }

        List<Grade> studentGrades = getGradesByStudent(studentUser);

        if (studentGrades.isEmpty()) {
            System.out.println("El estudiante " + studentUser + " no tiene calificaciones registradas.");
            return;
        }

        System.out.println("\n=== CALIFICACIONES DE " + studentUser.toUpperCase() + " ===");
        System.out.println("Nombre: " + student.getFirstName());
        System.out.printf("%-10s %-20s %-10s %-10s %-15s%n",
                "GRUPO", "CURSO", "NOTA", "ESTADO", "SEMESTRE");
        System.out.println("-------------------------------------------------------------------");

        double totalSum = 0.0;
        int passedCount = 0;

        for (Grade grade : studentGrades) {
            Group group = grade.getGroup();
            Course course = group.getRepresents();
            double gradeValue = grade.getGrade();
            String status = gradeValue >= PASSING_GRADE ? "APROBADO" : "REPROBADO";
            String courseName = course != null ? course.getName() : "N/A";
            String semester = group.getSemester();

            // Acortar nombre del curso si es muy largo
            if (courseName.length() > 18) {
                courseName = courseName.substring(0, 15) + "...";
            }

            System.out.printf("%-10s %-20s %-10.2f %-10s %-15s%n",
                    group.getNumber(),
                    courseName,
                    gradeValue,
                    status,
                    semester);

            totalSum += gradeValue;
            if (status.equals("APROBADO")) {
                passedCount++;
            }
        }

        double average = totalSum / studentGrades.size();

        System.out.println("-------------------------------------------------------------------");
        System.out.printf("PROMEDIO GENERAL: %.2f%n", average);
        System.out.printf("CURSOS APROBADOS: %d/%d%n", passedCount, studentGrades.size());
        System.out.printf("PORCENTAJE DE APROBACIÓN: %.1f%%%n",
                (passedCount * 100.0) / studentGrades.size());

        // Mostrar créditos aprobados
        List<String> passedCourses = getPassedCourses(studentUser);
        if (!passedCourses.isEmpty()) {
            System.out.printf("CURSOS APROBADOS: %s%n", String.join(", ", passedCourses));
        }
    }
}
