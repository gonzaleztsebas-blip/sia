package sia.sia.business;

import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sia.sia.data.CodeNumbersManager;
import sia.sia.data.Group;
import sia.sia.data.Course;
import sia.sia.data.Professor;
import sia.sia.data.Student;
import sia.sia.data.Grade;

public class GroupManager {

    private final static String GROUP_FILE_PATH = "src\\main\\resources\\dataBase\\groupCSV.csv";
    private final static int DEFAULT_MAX_CAPACITY = 40;

    private static List<String[]> groups = loadGroups();

    // ============================================================
    // CARGA INICIAL
    // ============================================================
    public static List<String[]> loadGroups() {
        try {
            CSVReader reader = new CSVReader(new FileReader(GROUP_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void clearCache() {
        groups = new ArrayList<>(); // Limpiar la lista en memoria
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GROUP_FILE_PATH))) {
            writer.write(""); // Archivo vacío
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de grupos: " + e.getMessage());
        }
    }

    // ============================================================
    // MÉTODOS PRINCIPALES
    // ============================================================
    public static void createGroup(String[] daysOfWeek, String[] timesOfDay,
            String semester, String courseId) {

        Course course = CourseManager.findCourse(Long.parseLong(courseId));
        if (course == null) {
            System.out.println("El curso no existe.");
            return;
        }

        // ✅ Generar número automáticamente
        CodeNumbersManager codeManager = new CodeNumbersManager();
        long groupNumber = codeManager.createNewGroupNumber();

        Group newGroup = new Group(
                groupNumber,
                daysOfWeek,
                timesOfDay,
                semester,
                course,
                null, // profesor
                new ArrayList<>(), // estudiantes
                new ArrayList<>() // notas
        );

        groups.add(newGroup.toArray());
        updateGroupCSV();
        System.out.println("Grupo creado correctamente con numero: " + groupNumber);
    }

    public static void createGroup(String number, String[] daysOfWeek, String[] timesOfDay,
            String semester, String courseId) {

        if (findGroup(number) != null) {
            System.out.println("Error: el grupo ya existe.");
            return;
        }

        Course course = CourseManager.findCourse(courseId);
        if (course == null) {
            System.out.println("El curso no existe.");
            return;
        }

        Group newGroup = new Group(
                Long.parseLong(number),
                daysOfWeek,
                timesOfDay,
                semester,
                course,
                null, // profesor
                new ArrayList<>(), // estudiantes
                new ArrayList<>() // notas
        );

        groups.add(newGroup.toArray());
        updateGroupCSV();
        System.out.println("Grupo creado correctamente.");
    }

    public static void deleteGroup(String number) {

        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i)[0].equals(number)) {
                groups.remove(i);
                updateGroupCSV();
                System.out.println("Grupo eliminado correctamente.");
                return;
            }
        }

        System.out.println("No existe el grupo.");
    }

    public static Group findGroup(String number) {

        for (String[] row : groups) {
            if (row.length < 8) {
                continue; // seguridad: necesitamos al menos 8 campos
            }
            if (row[0].equals(number)) {

                Course course = CourseManager.findCourse(Long.parseLong(row[4]));
                if (course == null) {
                    System.out.println("Advertencia: Curso no encontrado para grupo " + number);
                }

                // Profesor
                Professor professor = null;
                if (row[5] != null && !row[5].equals("null") && !row[5].isBlank()) {
                    professor = ProfessorManager.findProfessor(row[5]);
                }

                // ✅ Días de semana (manejo seguro)
                String[] daysOfWeek = new String[0];
                if (row[1] != null && !row[1].equals("null") && !row[1].isBlank()) {
                    daysOfWeek = row[1].split(";");
                }

                // ✅ Horarios (manejo seguro)
                String[] timesOfDay = new String[0];
                if (row[2] != null && !row[2].equals("null") && !row[2].isBlank()) {
                    timesOfDay = row[2].split(";");
                }

                // Estudiantes
                List<Student> students = new ArrayList<>();
                if (row[6] != null && !row[6].equals("null") && !row[6].isBlank()) {
                    for (String u : row[6].split(";")) {
                        Student s = StudentManager.findStudent(u.trim());
                        if (s != null) {
                            students.add(s);
                        }
                    }
                }

                // Notas (por implementar)
                List<Grade> grades = new ArrayList<>();

                return new Group(
                        Long.parseLong(row[0]),
                        daysOfWeek,
                        timesOfDay,
                        row[3],
                        course,
                        professor,
                        students,
                        grades
                );
            }
        }

        return null;
    }

    // ============================================================
    // ACTUALIZACIONES
    // ============================================================
    public static void updateSemester(String number, String newSemester) {

        for (String[] row : groups) {
            if (row[0].equals(number)) {
                row[3] = newSemester;
                updateGroupCSV();
                System.out.println("Semestre actualizado.");
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    public static void updateSchedule(String number, String[] newDays, String[] newHours) {

        for (String[] row : groups) {
            if (row[0].equals(number)) {

                row[1] = String.join(";", newDays);
                row[2] = String.join(";", newHours);

                updateGroupCSV();
                System.out.println("Horario actualizado.");
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    public static void updateCourseOfGroup(String number, String newCourseId) {

        Course c = CourseManager.findCourse(newCourseId);
        if (c == null) {
            System.out.println("El curso no existe.");
            return;
        }

        for (String[] row : groups) {
            if (row[0].equals(number)) {
                row[4] = newCourseId;
                updateGroupCSV();
                System.out.println("Curso actualizado.");
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    // ============================================================
    // PROFESORES
    // ============================================================
    public static void assignProfessor(String number, String professorUsername) {

        Professor p = ProfessorManager.findProfessor(professorUsername);
        if (p == null) {
            System.out.println("Profesor no existe.");
            return;
        }

        for (String[] row : groups) {
            if (row[0].equals(number)) {

                row[5] = professorUsername;
                updateGroupCSV();
                System.out.println("Profesor asignado correctamente.");
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    public static void removeProfessor(String number) {

        for (String[] row : groups) {
            if (row[0].equals(number)) {

                row[5] = "null";
                updateGroupCSV();
                System.out.println("Profesor eliminado del grupo.");
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    // ============================================================
    // ESTUDIANTES
    // ============================================================
    public static boolean addStudent(String number, String studentUser) {

        Student s = StudentManager.findStudent(studentUser);
        if (s == null) {
            System.out.println("Estudiante no existe.");
            return false;
        }

        Group group = findGroup(number);
        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return false;
        }

        // ✅ Validar cupo
        if (group.getAttendedBy() != null && group.getAttendedBy().size() >= DEFAULT_MAX_CAPACITY) {
            System.out.println("El grupo está lleno (maximo " + DEFAULT_MAX_CAPACITY + " estudiantes).");
            return false;
        }

        // ✅ Validar cruce de horarios
        if (hasScheduleConflict(studentUser, group)) {
            System.out.println("El estudiante tiene cruce de horarios con otro grupo.");
            return false;
        }

        for (String[] row : groups) {
            if (row[0].equals(number)) {

                String current = row[6];

                if (current == null || current.equals("null") || current.isBlank()) {
                    row[6] = studentUser;
                } else {
                    List<String> list = new ArrayList<>(Arrays.asList(current.split(";")));
                    if (list.contains(studentUser)) {
                        System.out.println("El estudiante ya está inscrito en este grupo.");
                        return false;
                    }
                    list.add(studentUser);
                    row[6] = String.join(";", list);
                }

                updateGroupCSV();
                System.out.println("Estudiante agregado.");
                return true;
            }
        }

        System.out.println("❌ Grupo no encontrado.");
        return false;
    }

    public static void removeStudent(String number, String studentUser) {

        for (String[] row : groups) {
            if (row[0].equals(number)) {

                if (row[6] == null || row[6].equals("null") || row[6].isBlank()) {
                    System.out.println("El grupo no tiene estudiantes.");
                    return;
                }

                List<String> list = new ArrayList<>(Arrays.asList(row[6].split(";")));
                if (!list.remove(studentUser)) {
                    System.out.println("El estudiante no esta en el grupo.");
                    return;
                }

                row[6] = list.isEmpty() ? "" : String.join(";", list);
                updateGroupCSV();
                System.out.println("Estudiante eliminado.");
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    public static void listStudents(String number) {

        for (String[] row : groups) {
            if (row[0].equals(number)) {

                if (row[6] == null || row[6].equals("null") || row[6].isBlank()) {
                    System.out.println("(sin estudiantes)");
                } else {
                    System.out.println(row[6]);
                }
                return;
            }
        }

        System.out.println("Grupo no encontrado.");
    }

    // ============================================================
    // VALIDACIONES
    // ============================================================
    /**
     * Verifica si hay cruce de horarios
     */
    private static boolean hasScheduleConflict(String studentUser, Group newGroup) {

        // Obtener todos los grupos del estudiante
        List<Group> studentGroups = getGroupsByStudent(studentUser);

        if (studentGroups.isEmpty()) {
            return false; // No tiene grupos, no hay conflicto
        }

        String[] newDays = newGroup.getDaysOfWeek();
        String[] newTimes = newGroup.getTimesOfDay();

        for (Group existingGroup : studentGroups) {
            String[] existingDays = existingGroup.getDaysOfWeek();
            String[] existingTimes = existingGroup.getTimesOfDay();

            // Verificar si hay días en común
            for (String newDay : newDays) {
                for (String existingDay : existingDays) {
                    if (newDay.equalsIgnoreCase(existingDay)) {
                        // Hay mismo dia, verificar horarios
                        for (String newTime : newTimes) {
                            for (String existingTime : existingTimes) {
                                if (newTime.equals(existingTime)) {
                                    return true; // Cruce detectado
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * ✅ NUEVO: Obtiene todos los grupos de un estudiante
     */
    public static List<Group> getGroupsByStudent(String studentUser) {
        List<Group> result = new ArrayList<>();

        for (String[] row : groups) {
            if (row.length >= 7 && row[6] != null && !row[6].isBlank()) {
                List<String> students = Arrays.asList(row[6].split(";"));
                if (students.contains(studentUser)) {
                    Group g = findGroup(row[0]);
                    if (g != null) {
                        result.add(g);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Verifica cuántos cupos quedan
     */
    public static int getAvailableSpots(String number) {
        Group g = findGroup(number);
        if (g == null) {
            return 0;
        }

        int current = g.getAttendedBy() != null ? g.getAttendedBy().size() : 0;
        return DEFAULT_MAX_CAPACITY - current;
    }

    public static String getProfessor(String groupNumber) {
        for (String[] group : groups) {
            if (group[0].equals(groupNumber)) {
                String professorId = group[4]; // ID del profesor
                // Buscar el username del profesor por su ID
                return findProfessorUsernameById(professorId);
            }
        }
        return null;
    }

    private static String findProfessorUsernameById(String professorId) {
        List<String[]> professors = ProfessorManager.getProfessors();
        for (String[] professor : professors) {
            if (professor[3].equals(professorId)) { // ID está en posición 3
                return professor[0]; // username está en posición 0
            }
        }
        return professorId; // fallback: retorna el ID si no encuentra
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    public static void listGroups() {

        if (groups.isEmpty()) {
            System.out.println("No hay grupos registrados.");
            return;
        }

        System.out.println("\nLISTA DE GRUPOS:");
        for (String[] row : groups) {
            System.out.println("- Grupo " + row[0]
                    + " | Curso: " + row[4]
                    + " | Semestre: " + row[3]
                    + " | Profesor: " + row[5]
                    + " | Estudiantes: " + row[6]);
        }
    }

    private static void updateGroupCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GROUP_FILE_PATH))) {
            for (String[] row : groups) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Para cada manager, agrega este método:

    public static void reload() {    // Para CourseManager
        groups = loadGroups();
    }
}
