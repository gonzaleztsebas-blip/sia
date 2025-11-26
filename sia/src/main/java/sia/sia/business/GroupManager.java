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
import java.util.Collections;
import java.util.List;
import static sia.sia.business.ProfessorManager.findProfessor;
import static sia.sia.business.StudentManager.findStudent;
import sia.sia.data.Course;
import sia.sia.data.Group;
import sia.sia.data.Professor;
import sia.sia.data.Student;

public class GroupManager {

    private final static String GROUP_FILE_PATH = "src\\main\\resources\\dataBase\\groupCSV.csv";
    private final static int DEFAULT_MAX_CAPACITY = 40;

    private static List<String[]> groups = loadGroups();

    // ------------------- CARGA -------------------
    public static List<String[]> loadGroups() {
        try (CSVReader reader = new CSVReader(new FileReader(GROUP_FILE_PATH))) {
            List<String[]> rows = reader.readAll();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            // Archivo inexistente o vacío -> devolver lista vacía
            return new ArrayList<>();
        }
    }

    public static void reload() {
        groups = loadGroups();
    }

    public static void clearCache() {
        groups = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GROUP_FILE_PATH))) {
            writer.write("");
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de grupos: " + e.getMessage());
        }
    }

    // ------------------- CREATE -------------------
    public static void createGroup(String[] daysOfWeek, String[] timesOfDay,
            String semester, String courseId) {

        long courseCode;
        try {
            courseCode = Long.parseLong(courseId);
        } catch (NumberFormatException e) {
            System.out.println("El id del curso debe ser numérico.");
            return;
        }

        Course course = CourseManager.findCourse(courseCode);
        if (course == null) {
            System.out.println("El curso no existe.");
            return;
        }

        CodeNumbersManager codeManager = new CodeNumbersManager();
        long groupNumber = codeManager.createNewGroupNumber();

        Group newGroup = new Group(groupNumber, daysOfWeek, timesOfDay, semester, course);

        // construir fila CSV con formato consistente:
        // [number, days(;), times(;), semester, courseCode, professorUser|null, students(;)]
        String[] row = newGroup.toArray(); // asumo que toArray genera formato compatible
        // si toArray no incluye cols 5-6, aseguramos tamaño
        if (row.length < 7) {
            row = Arrays.copyOf(row, 7);
            if (row[5] == null) {
                row[5] = "null";
            }
            if (row[6] == null) {
                row[6] = "";
            }
        }

        groups.add(row);
        if (saveGroupsCSV()) {
            System.out.println("Grupo creado correctamente con numero: " + groupNumber);
        } else {
            System.out.println("Error al guardar el grupo en CSV.");
        }
    }

    public static void createGroup(String number, String[] daysOfWeek, String[] timesOfDay,
            String semester, String courseId, String professorUser) {

        if (findGroup(number) != null) {
            System.out.println("Error: el grupo ya existe.");
            return;
        }

        long courseCode;
        try {
            courseCode = Long.parseLong(courseId);
        } catch (NumberFormatException e) {
            System.out.println("El id del curso debe ser numérico.");
            return;
        }

        Course course = CourseManager.findCourse(courseCode);
        if (course == null) {
            System.out.println("El curso no existe.");
            return;
        }

        long num;
        try {
            num = Long.parseLong(number);
        } catch (NumberFormatException e) {
            System.out.println("Número de grupo inválido.");
            return;
        }

        Group newGroup = new Group(num, daysOfWeek, timesOfDay, semester, course, findProfessor(professorUser), new ArrayList<>(), new ArrayList<>());

        String[] row = newGroup.toArray();
        if (row.length < 7) {
            row = Arrays.copyOf(row, 7);
            if (row[5] == null) {
                row[5] = professorUser == null ? "null" : professorUser;
            }
            if (row[6] == null) {
                row[6] = "";
            }
        }

        groups.add(row);
        if (saveGroupsCSV()) {
            System.out.println("Grupo creado correctamente.");
        } else {
            System.out.println("Error al guardar el grupo en CSV.");
        }
    }

    // ------------------- DELETE -------------------
    public static void deleteGroup(String number) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).length > 0 && groups.get(i)[0].equals(number)) {
                groups.remove(i);
                if (saveGroupsCSV()) {
                    System.out.println("Grupo eliminado correctamente.");
                } else {
                    System.out.println("Error al actualizar CSV al eliminar grupo.");
                }
                return;
            }
        }
        System.out.println("No existe el grupo.");
    }

    // ------------------- FIND -------------------
    /**
     * Reconstruye completamente el objeto Group desde la fila CSV.
     */
    public static Group findGroup(String groupNumber) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(groupNumber)) {
                try {
                    long number = Long.parseLong(row[0]);

                    String[] days = row.length > 1 && row[1] != null && !row[1].isBlank()
                            ? row[1].split(";") : new String[0];

                    String[] times = row.length > 2 && row[2] != null && !row[2].isBlank()
                            ? row[2].split(";") : new String[0];

                    String semester = row.length > 3 ? row[3] : "";

                    long courseCode = row.length > 4 && row[4] != null && !row[4].isBlank()
                            ? Long.parseLong(row[4]) : -1L;

                    Course course = courseCode >= 0 ? CourseManager.findCourse(courseCode) : null;
                    if (course == null) {
                        // si no hay curso, no podemos construir grupo útilmente
                        return null;
                    }

                    // Profesor
                    String professorUser = (row.length > 5 && row[5] != null && !row[5].isBlank() && !"null".equals(row[5]))
                            ? row[5] : null;

                    // Estudiantes
                    List<Student> studentsList = new ArrayList<>();
                    if (row.length > 6 && row[6] != null && !row[6].isBlank()) {
                        String[] parts = row[6].split(";");
                        for (String s : parts) {
                            if (s != null && !s.isBlank()) {
                                studentsList.add(findStudent(s));
                            }
                        }
                    }

                    Group g = new Group(number, days, times, semester, course, findProfessor(professorUser), studentsList, new ArrayList<>());
                    return g;

                } catch (Exception e) {
                    // fila corrupta -> saltarla
                    return null;
                }
            }
        }
        return null;
    }

    // ------------------- UPDATES -------------------
    public static void updateSemester(String number, String newSemester) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(number)) {
                if (row.length < 4) {
                    row = Arrays.copyOf(row, 7);
                }
                row[3] = newSemester;
                saveGroupsCSV();
                System.out.println("Semestre actualizado.");
                return;
            }
        }
        System.out.println("Grupo no encontrado.");
    }

    public static void updateSchedule(String number, String[] newDays, String[] newHours) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(number)) {
                if (row.length < 7) {
                    row = Arrays.copyOf(row, 7);
                }
                row[1] = String.join(";", newDays);
                row[2] = String.join(";", newHours);
                saveGroupsCSV();
                System.out.println("Horario actualizado.");
                return;
            }
        }
        System.out.println("Grupo no encontrado.");
    }

    public static void updateCourseOfGroup(String number, String newCourseId) {
        long courseCode;
        try {
            courseCode = Long.parseLong(newCourseId);
        } catch (NumberFormatException e) {
            System.out.println("Id de curso inválido.");
            return;
        }
        Course c = CourseManager.findCourse(courseCode);
        if (c == null) {
            System.out.println("El curso no existe.");
            return;
        }

        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(number)) {
                if (row.length < 5) {
                    row = Arrays.copyOf(row, 7);
                }
                row[4] = String.valueOf(courseCode);
                saveGroupsCSV();
                System.out.println("Curso actualizado.");
                return;
            }
        }
        System.out.println("Grupo no encontrado.");
    }

    // ------------------- PROFESSORS -------------------
    public static void assignProfessor(String groupNumber, String professorUser) {
        for (int i = 0; i < groups.size(); i++) {
            String[] row = groups.get(i);
            if (row.length > 0 && row[0].equals(groupNumber)) {
                if (row.length < 6) {
                    row = Arrays.copyOf(row, 7);
                }
                row[5] = professorUser;
                groups.set(i, row);
                saveGroupsCSV();
                System.out.println("✓ Profesor asignado: " + professorUser + " -> Grupo " + groupNumber);
                return;
            }
        }
        System.out.println("ERROR: Grupo " + groupNumber + " no encontrado");
    }

    public static void removeProfessor(String number) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(number)) {
                if (row.length < 6) {
                    row = Arrays.copyOf(row, 7);
                }
                row[5] = "null";
                saveGroupsCSV();
                System.out.println("Profesor eliminado del grupo.");
                return;
            }
        }
        System.out.println("Grupo no encontrado.");
    }

    // ------------------- STUDENTS -------------------
    public static boolean addStudent(String number, String studentUser) {

        Student s = StudentManager.findStudent(studentUser);
        if (s == null) {
            System.out.println("Estudiante no existe.");
            return false;
        }

        // Usar findGroup reconstruido (NO recarga implícita)
        Group group = findGroup(number);
        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return false;
        }

        // validar cupo
        int current = group.getAttendedBy() != null ? group.getAttendedBy().size() : 0;
        if (current >= DEFAULT_MAX_CAPACITY) {
            System.out.println("El grupo está lleno (maximo " + DEFAULT_MAX_CAPACITY + " estudiantes).");
            return false;
        }

        // validar cruce horarios
        if (hasScheduleConflict(studentUser, group)) {
            System.out.println("El estudiante tiene cruce de horarios con otro grupo.");
            return false;
        }

        // Actualizar la fila en memoria
        for (int i = 0; i < groups.size(); i++) {
            String[] row = groups.get(i);
            if (row.length > 0 && row[0].equals(number)) {
                if (row.length < 7) {
                    row = Arrays.copyOf(row, 7);
                }
                String currentStr = row[6] == null ? "" : row[6];
                List<String> list = new ArrayList<>();
                if (!currentStr.isBlank()) {
                    list.addAll(Arrays.asList(currentStr.split(";")));
                }
                if (list.contains(studentUser)) {
                    System.out.println("El estudiante ya está inscrito en este grupo.");
                    return false;
                }
                list.add(studentUser);
                row[6] = String.join(";", list);
                groups.set(i, row);
                if (saveGroupsCSV()) {
                    System.out.println("Estudiante agregado.");
                    return true;
                } else {
                    System.out.println("Error al guardar cambios en CSV.");
                    return false;
                }
            }
        }

        System.out.println("❌ Grupo no encontrado.");
        return false;
    }

    public static void removeStudent(String number, String studentUser) {
        for (int i = 0; i < groups.size(); i++) {
            String[] row = groups.get(i);
            if (row.length > 0 && row[0].equals(number)) {
                String cur = row.length > 6 ? row[6] : "";
                if (cur == null || cur.isBlank()) {
                    System.out.println("El grupo no tiene estudiantes.");
                    return;
                }
                List<String> list = new ArrayList<>(Arrays.asList(cur.split(";")));
                if (!list.remove(studentUser)) {
                    System.out.println("El estudiante no esta en el grupo.");
                    return;
                }
                row[6] = list.isEmpty() ? "" : String.join(";", list);
                groups.set(i, row);
                saveGroupsCSV();
                System.out.println("Estudiante eliminado.");
                return;
            }
        }
        System.out.println("Grupo no encontrado.");
    }

    public static void listStudents(String number) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(number)) {
                String cur = row.length > 6 ? row[6] : "";
                if (cur == null || cur.isBlank()) {
                    System.out.println("(sin estudiantes)");
                } else {
                    System.out.println(cur);
                }
                return;
            }
        }
        System.out.println("Grupo no encontrado.");
    }

    public static List<String> getStudentsInGroup(String groupNumber) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(groupNumber)) {
                if (row.length > 6 && row[6] != null && !row[6].isBlank()) {
                    String[] students = row[6].split(";");
                    return new ArrayList<>(Arrays.asList(students));
                }
                return new ArrayList<>(); // Retornar lista vacía si no hay estudiantes
            }
        }
        return new ArrayList<>(); // Retornar lista vacía si el grupo no existe
    }

    // ------------------- VALIDACIONES -------------------
    private static boolean hasScheduleConflict(String studentUser, Group newGroup) {
        List<Group> studentGroups = getGroupsByStudent(studentUser);
        if (studentGroups.isEmpty()) {
            return false;
        }

        String[] newDays = newGroup.getDaysOfWeek();
        String[] newTimes = newGroup.getTimesOfDay();

        if (newDays == null || newTimes == null) {
            return false;
        }

        for (Group existingGroup : studentGroups) {
            String[] existingDays = existingGroup.getDaysOfWeek();
            String[] existingTimes = existingGroup.getTimesOfDay();
            if (existingDays == null || existingTimes == null) {
                continue;
            }

            for (String nd : newDays) {
                for (String ed : existingDays) {
                    if (nd.equalsIgnoreCase(ed)) {
                        for (String nt : newTimes) {
                            for (String et : existingTimes) {
                                if (nt.equals(et)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static List<Group> getGroupsByStudent(String studentUser) {
        List<Group> result = new ArrayList<>();
        for (String[] row : groups) {
            if (row.length >= 7 && row[6] != null && !row[6].isBlank()) {
                String[] studs = row[6].split(";");
                for (String s : studs) {
                    if (studentUser.equals(s)) {
                        Group g = findGroup(row[0]);
                        if (g != null) {
                            result.add(g);
                        }
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static int getAvailableSpots(String number) {
        Group g = findGroup(number);
        if (g == null) {
            return 0;
        }
        int current = g.getAttendedBy() != null ? g.getAttendedBy().size() : 0;
        return DEFAULT_MAX_CAPACITY - current;
    }

    public static String getProfessor(String groupNumber) {
        for (String[] row : groups) {
            if (row.length > 0 && row[0].equals(groupNumber)) {
                if (row.length > 5 && row[5] != null && !row[5].isBlank() && !"null".equals(row[5])) {
                    return row[5];
                }
                return null;
            }
        }
        return null;
    }

    private static String findProfessorUsernameById(String professorId) {
        List<String[]> professors = ProfessorManager.getProfessors();
        for (String[] professor : professors) {
            if (professor.length > 3 && professor[3].equals(professorId)) {
                return professor[0];
            }
        }
        return professorId;
    }

    // ------------------- UTIL -------------------
    public static void listGroups() {
        if (groups.isEmpty()) {
            System.out.println("No hay grupos registrados.");
            return;
        }
        System.out.println("\nLISTA DE GRUPOS:");
        for (String[] row : groups) {
            String num = row.length > 0 ? row[0] : "";
            String curso = row.length > 4 ? row[4] : "";
            String sem = row.length > 3 ? row[3] : "";
            String prof = row.length > 5 ? row[5] : "";
            String studs = row.length > 6 ? row[6] : "";
            System.out.println("- Grupo " + num + " | Curso: " + curso + " | Semestre: " + sem + " | Profesor: " + prof + " | Estudiantes: " + studs);
        }
    }

    // Guarda seguro usando CSVWriter + archivo temporal
    private static boolean saveGroupsCSV() {
        File original = new File(GROUP_FILE_PATH);
        File tmp = new File(GROUP_FILE_PATH + ".tmp");
        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] row : groups) {
                if (row == null) {
                    continue;
                }
                String[] safe = Arrays.copyOf(row, Math.max(row.length, 7));
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
}
