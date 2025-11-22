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
import java.util.List;
import sia.sia.data.Grade;
import sia.sia.data.Student;
import sia.sia.data.Group;

/**
 * Manager para gestionar las calificaciones del sistema Permite crear,
 * actualizar, eliminar y consultar notas
 *
 * @author luzel
 */
public class GradeManager {

    private final static String GRADE_FILE_PATH = "src\\main\\resources\\dataBase\\gradeCSV.csv";

    // Cargar una sola vez
    private static List<String[]> grades = loadGrades();

    // ============================================================
    // CARGA INICIAL
    // ============================================================
    public static List<String[]> loadGrades() {
        try {
            CSVReader reader = new CSVReader(new FileReader(GRADE_FILE_PATH));
            List<String[]> rows = reader.readAll();
            reader.close();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("No se pudieron cargar calificaciones: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void clearCache() {
        grades = new ArrayList<>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GRADE_FILE_PATH))) {
            writer.write("");
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de calificaciones: " + e.getMessage());
        }
    }

    public static List<String[]> getGrades() {
        return grades;
    }

    // ============================================================
    // METODOS PRINCIPALES
    // ============================================================
    /**
     * Registra una nueva calificación
     *
     * @param studentUser Username del estudiante
     * @param groupNumber Numero del grupo
     * @param gradeValue Calificación (0.0 - 5.0)
     */
    public static void createGrade(String studentUser, String groupNumber, double gradeValue) {
        System.out.println("DEBUG: Creando calificación - " + studentUser + ", " + groupNumber + ", " + gradeValue);
        
        // PRIMERO recargar las calificaciones actuales
        grades = loadGrades();
        
        // Validar que el estudiante existe
        Student student = StudentManager.findStudent(studentUser);
        if (student == null) {
            System.out.println("El estudiante no existe.");
            return;
        }

        // Validar que el grupo existe
        Group group = GroupManager.findGroup(groupNumber);
        if (group == null) {
            System.out.println("El grupo no existe.");
            return;
        }

        // Validar que la nota está en el rango correcto
        if (gradeValue < 0.0 || gradeValue > 5.0) {
            System.out.println("La calificación debe estar entre 0.0 y 5.0");
            return;
        }

        // Verificar que el estudiante está inscrito en el grupo
        if (!EnrollmentManager.isStudentEnrolled(studentUser, groupNumber)) {
            System.out.println("El estudiante no está inscrito en este grupo.");
            return;
        }

        // Buscar y actualizar calificación existente
        boolean found = false;
        for (int i = 0; i < grades.size(); i++) {
            String[] row = grades.get(i);
            if (row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                // Actualizar existente
                row[2] = String.valueOf(gradeValue);
                found = true;
                System.out.println("DEBUG: Calificación actualizada");
                break;
            }
        }
        
        // Si no existe, crear nueva
        if (!found) {
            String[] gradeRow = {studentUser, groupNumber, String.valueOf(gradeValue)};
            grades.add(gradeRow);
            System.out.println("DEBUG: Calificación creada nueva");
        }

        updateGradeCSV();
        
        // VERIFICACIÓN INMEDIATA
        System.out.println("DEBUG: Verificando guardado...");
        grades = loadGrades();
        for (String[] row : grades) {
            if (row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                System.out.println("DEBUG: Calificación guardada en CSV: " + row[2]);
            }
        }
        
        System.out.println("Calificacion registrada correctamente.");
    }

    /**
     * Actualiza una calificación existente
     */
    public static void updateGrade(String studentUser, String groupNumber, double newGrade) {
        // PRIMERO recargar
        grades = loadGrades();
        
        // Validar rango
        if (newGrade < 0.0 || newGrade > 5.0) {
            System.out.println("La calificacion debe estar entre 0.0 y 5.0");
            return;
        }

        for (String[] row : grades) {
            if (row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                row[2] = String.valueOf(newGrade);
                updateGradeCSV();
                System.out.println("Calificacion actualizada correctamente.");
                return;
            }
        }

        System.out.println("No existe una calificacion para este estudiante en este grupo.");
    }

    /**
     * Elimina una calificación
     */
    public static void deleteGrade(String studentUser, String groupNumber) {
        // PRIMERO recargar
        grades = loadGrades();

        for (int i = 0; i < grades.size(); i++) {
            String[] row = grades.get(i);
            if (row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                grades.remove(i);
                updateGradeCSV();
                System.out.println("Calificacion eliminada correctamente.");
                return;
            }
        }

        System.out.println("No existe una calificacion para eliminar.");
    }

    /**
     * Busca una calificación específica
     */
    public static Grade findGrade(String studentUser, String groupNumber) {
        // PRIMERO recargar
        List<String[]> currentGrades = loadGrades();

        for (String[] row : currentGrades) {
            if (row.length >= 3 && row[0].equals(studentUser) && row[1].equals(groupNumber)) {
                try {
                    Student student = StudentManager.findStudent(row[0]);
                    Group group = GroupManager.findGroup(row[1]);
                    double gradeValue = Double.parseDouble(row[2]);

                    if (student != null && group != null) {
                        return new Grade(student, group, gradeValue);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: No se pudo convertir la calificación: " + row[2]);
                }
            }
        }

        return null;
    }

    // ============================================================
    // CONSULTAS Y REPORTES
    // ============================================================
    /**
     * Obtiene todas las calificaciones de un estudiante
     */
    public static List<Grade> getGradesByStudent(String studentUsername) {
        List<Grade> result = new ArrayList<>();
        
        // SIEMPRE recargar datos frescos del CSV
        List<String[]> currentGrades = loadGrades();

        for (String[] row : currentGrades) {
            if (row[0].equals(studentUsername)) {
                try {
                    double gradeValue = Double.parseDouble(row[2]);
                    System.out.println("DEBUG: Parseando calificación - " + row[2] + " -> " + gradeValue);
                    
                    Student student = StudentManager.findStudent(row[0]);
                    Group group = GroupManager.findGroup(row[1]);
                    
                    if (student != null && group != null) {
                        Grade grade = new Grade(student, group, gradeValue);
                        result.add(grade);
                    } else {
                        System.out.println("ERROR: No se pudo encontrar estudiante o grupo para la calificación");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: No se pudo convertir '" + row[2] + "' a double");
                }
            }
        }

        System.out.println("DEBUG: " + result.size() + " calificaciones para " + studentUsername);
        return result;
    }

    /**
     * Obtiene todas las calificaciones de un grupo
     */
    public static List<Grade> getGradesByGroup(String groupNumber) {
        List<Grade> result = new ArrayList<>();
        
        // SIEMPRE recargar datos frescos del CSV
        List<String[]> currentGrades = loadGrades();

        for (String[] row : currentGrades) {
            if (row[1].equals(groupNumber)) {
                try {
                    Student student = StudentManager.findStudent(row[0]);
                    Group group = GroupManager.findGroup(row[1]);
                    double gradeValue = Double.parseDouble(row[2]);

                    if (student != null && group != null) {
                        result.add(new Grade(student, group, gradeValue));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERROR: No se pudo convertir la calificación: " + row[2]);
                }
            }
        }

        return result;
    }

    /**
     * Calcula el promedio de un estudiante
     */
    public static double calculateStudentAverage(String studentUsername) {
        List<Grade> studentGrades = getGradesByStudent(studentUsername);
        if (studentGrades.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Grade grade : studentGrades) {
            sum += grade.getGrade();
        }

        double average = sum / studentGrades.size();
        System.out.println("DEBUG: Promedio de " + studentUsername + " = " + average);
        return average;
    }

    /**
     * Calcula el promedio de un grupo
     */
    public static double calculateGroupAverage(String groupNumber) {
        List<Grade> groupGrades = getGradesByGroup(groupNumber);

        if (groupGrades.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Grade g : groupGrades) {
            sum += g.getGrade();
        }

        return sum / groupGrades.size();
    }

    /**
     * Verifica si un estudiante aprobó un curso (nota >= 3.0)
     */
    public static boolean hasPassed(String studentUser, String groupNumber) {
        Grade grade = findGrade(studentUser, groupNumber);

        if (grade == null) {
            return false;
        }

        return grade.getGrade() >= 3.0;
    }

    /**
     * Obtiene todas las materias aprobadas por un estudiante
     */
    public static List<String> getPassedCourses(String studentUser) {
        List<String> passedCourses = new ArrayList<>();
        List<Grade> studentGrades = getGradesByStudent(studentUser);

        for (Grade g : studentGrades) {
            if (g.getGrade() >= 3.0) {
                String courseCode = String.valueOf(g.getGroup().getRepresents().getCode());
                if (!passedCourses.contains(courseCode)) {
                    passedCourses.add(courseCode);
                }
            }
        }

        return passedCourses;
    }

    /**
     * Lista todas las calificaciones de un estudiante
     */
    public static void listStudentGrades(String studentUser) {
        List<Grade> studentGrades = getGradesByStudent(studentUser);

        if (studentGrades.isEmpty()) {
            System.out.println("El estudiante no tiene calificaciones registradas.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("CALIFICACIONES DE: " + studentUser);
        System.out.println("========================================");

        for (Grade g : studentGrades) {
            Group group = g.getGroup();
            String courseName = group.getRepresents().getName();
            String courseCode = String.valueOf(group.getRepresents().getCode());
            String status = g.getGrade() >= 3.0 ? "APROBADO" : "REPROBADO";

            System.out.printf("Curso: %s (%s) | Grupo: %d | Nota: %.2f | %s%n",
                    courseName, courseCode, group.getNumber(), g.getGrade(), status);
        }

        System.out.printf("\nPROMEDIO GENERAL: %.2f%n", calculateStudentAverage(studentUser));
        System.out.println("========================================\n");
    }

    /**
     * Lista todas las calificaciones de un grupo
     */
    public static void listGroupGrades(String groupNumber) {
        List<Grade> groupGrades = getGradesByGroup(groupNumber);

        if (groupGrades.isEmpty()) {
            System.out.println("❌ El grupo no tiene calificaciones registradas.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);
        System.out.println("\n========================================");
        System.out.println("CALIFICACIONES DEL GRUPO: " + groupNumber);
        System.out.println("Curso: " + group.getRepresents().getName());
        System.out.println("========================================");

        for (Grade g : groupGrades) {
            String status = g.getGrade() >= 3.0 ? "APROBADO" : "REPROBADO";
            System.out.printf("Estudiante: %s | Nota: %.2f | %s%n",
                    g.getStudent().getUser(), g.getGrade(), status);
        }

        System.out.printf("\nPROMEDIO DEL GRUPO: %.2f%n", calculateGroupAverage(groupNumber));
        System.out.println("========================================\n");
    }

    /**
     * Lista todas las calificaciones del sistema
     */
    public static void listAllGrades() {
        // PRIMERO recargar
        List<String[]> currentGrades = loadGrades();

        if (currentGrades.isEmpty()) {
            System.out.println("❌ No hay calificaciones registradas.");
            return;
        }

        System.out.println("\nLISTA DE TODAS LAS CALIFICACIONES:");
        System.out.println("========================================");

        for (String[] row : currentGrades) {
            System.out.printf("Estudiante: %s | Grupo: %s | Nota: %s%n",
                    row[0], row[1], row[2]);
        }

        System.out.println("========================================\n");
    }

    // ============================================================
    // VALIDACIONES Y UTILIDADES
    // ============================================================
    /**
     * Verifica si un estudiante cumple con los requisitos de un curso (útil
     * para la lógica de inscripción)
     */
    public static boolean meetsPrerequisites(String studentUser, String courseCode) {
        // Obtener el curso
        Long code = Long.parseLong(courseCode);
        var course = CourseManager.findCourse(code);

        if (course == null) {
            System.out.println("Curso no encontrado.");
            return false;
        }

        // Si no tiene requisitos, puede inscribirse
        List<String> requisites = course.getRequisites();
        if (requisites == null || requisites.isEmpty()) {
            return true;
        }

        // Obtener cursos aprobados por el estudiante
        List<String> passedCourses = getPassedCourses(studentUser);

        // Verificar que haya aprobado todos los requisitos
        for (String req : requisites) {
            if (!passedCourses.contains(req)) {
                System.out.println("No cumple con el requisito: " + req);
                return false;
            }
        }

        return true;
    }

    /**
     * Calcula los créditos aprobados por un estudiante
     */
    public static int calculateApprovedCredits(String studentUser) {
        List<String> passedCourses = getPassedCourses(studentUser);
        int totalCredits = 0;

        for (String courseCode : passedCourses) {
            var course = CourseManager.findCourse(Long.parseLong(courseCode));
            if (course != null) {
                totalCredits += course.getCredits();
            }
        }

        return totalCredits;
    }

    /**
     * Actualiza el archivo CSV
     */
    private static void updateGradeCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GRADE_FILE_PATH))) {
            for (String[] row : grades) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
            System.out.println("DEBUG: Archivo de calificaciones actualizado");
        } catch (Exception e) {
            System.out.println("Error actualizando calificaciones CSV: " + e.getMessage());
        }
    }

    public static void reload() {
        grades = loadGrades();
    }

    // Método para diagnóstico
    public static void printAllGrades() {
        System.out.println("=== TODAS LAS CALIFICACIONES ===");
        List<String[]> currentGrades = loadGrades();
        for (String[] row : currentGrades) {
            System.out.println("Estudiante: " + row[0] + ", Grupo: " + row[1] + ", Nota: " + row[2]);
        }
    }
    
    // Método temporal para ver el archivo REAL
    public static void debugFileContent() {
        System.out.println("=== CONTENIDO REAL DEL ARCHIVO ===");
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(GRADE_FILE_PATH));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("LINEA: " + line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error leyendo archivo: " + e.getMessage());
        }
    }
}