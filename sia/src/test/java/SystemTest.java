import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

import java.util.List;

import sia.sia.business.*;

import static org.junit.jupiter.api.Assertions.*;
import sia.sia.data.Course;
import sia.sia.data.Grade;
import sia.sia.data.Group;
import sia.sia.data.Student;

/**
 * Pruebas integrales del sistema SIA. Verifica estudiantes, profesores, cursos,
 * grupos, inscripciones y calificaciones.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SystemTest {

    private static final String[] files = {
        "src\\main\\resources\\dataBase\\usersCSV.csv",
        "src\\main\\resources\\dataBase\\studentCSV.csv",
        "src\\main\\resources\\dataBase\\professorCSV.csv",
        "src\\main\\resources\\dataBase\\courseCSV.csv",
        "src\\main\\resources\\dataBase\\groupCSV.csv",
        "src\\main\\resources\\dataBase\\enrollmentCSV.csv",
        "src\\main\\resources\\dataBase\\gradeCSV.csv"
    };

    @BeforeAll
    static void setup() {
        System.out.println("=== INICIANDO LIMPIEZA DEL SISTEMA ===");

        // Limpiar archivos CSV
        for (String file : files) {
            try (java.io.FileWriter fw = new java.io.FileWriter(file)) {
                fw.write(""); // Limpiar archivo
                System.out.println("✓ Archivo limpiado: " + file);
            } catch (Exception e) {
                System.out.println("✗ No se pudo limpiar: " + file + " -> " + e.getMessage());
            }
        }

        // Limpiar caches de todos los managers
        StudentManager.reload();
        ProfessorManager.reload();
        CourseManager.reload();
        GroupManager.reload();
        GradeManager.reload();
        EnrollmentManager.reload();

        System.out.println("=== SISTEMA LIMPIADO PARA TESTS ===\n");
    }

    // ============================================================
    //  TEST: ESTUDIANTES
    // ============================================================
    @Test
    @Order(1)
    void testStudents() {
        System.out.println("=== EJECUTANDO TEST: ESTUDIANTES ===");

        // Verificar que no existen previamente
        assertNull(StudentManager.findStudent("juan123"), "No debe existir juan123 al inicio");
        assertNull(StudentManager.findStudent("maria456"), "No debe existir maria456 al inicio");
        assertNull(StudentManager.findStudent("carlos789"), "No debe existir carlos789 al inicio");

        // Crear estudiantes
        System.out.println("Creando estudiantes...");
        StudentManager.createStudent("juan123", "pass123", "Juan", "Perez", "2000-01-15");
        StudentManager.createStudent("maria456", "pass456", "Maria", "Lopez", "1999-05-20");
        StudentManager.createStudent("carlos789", "pass789", "Carlos", "Gomez", "2001-03-10");

        // Forzar recarga y verificar que se crearon
        StudentManager.reload();

        var juan = StudentManager.findStudent("juan123");
        var maria = StudentManager.findStudent("maria456");
        var carlos = StudentManager.findStudent("carlos789");

        assertNotNull(juan, "Estudiante juan123 debe existir después de crearlo");
        assertNotNull(maria, "Estudiante maria456 debe existir después de crearlo");
        assertNotNull(carlos, "Estudiante carlos789 debe existir después de crearlo");

        // Actualizar estudiante
        System.out.println("Actualizando estudiante juan123...");
        StudentManager.updateStudent("juan123","pass321", "Juan Carlos", "Perez Gomez","2007-01-15");

        // Verificar actualización
        var updated = StudentManager.findStudent("juan123");
        assertNotNull(updated, "Estudiante actualizado debe existir");
        assertEquals("Juan Carlos", updated.getFirstName(), "Nombre debe estar actualizado");
        assertEquals("Perez Gomez", updated.getLastName(), "Apellido debe estar actualizado");

        System.out.println("✓ Test estudiantes completado exitosamente\n");
    }

    // ============================================================
    //  TEST: PROFESORES
    // ============================================================
    @Test
    @Order(2)
    void testProfessors() {
        System.out.println("=== EJECUTANDO TEST: PROFESORES ===");

        // Verificar que no existen previamente
        assertNull(ProfessorManager.findProfessor("prof_garcia"), "No debe existir prof_garcia al inicio");
        assertNull(ProfessorManager.findProfessor("prof_rodriguez"), "No debe existir prof_rodriguez al inicio");

        // Crear profesores
        System.out.println("Creando profesores...");
        ProfessorManager.createProfessor("prof_garcia", "profpass1", "Luis", "Garcia", "1975-06-15");
        ProfessorManager.createProfessor("prof_rodriguez", "profpass2", "Ana", "Rodriguez", "1980-09-20");

        // Forzar recarga
        ProfessorManager.reload();

        // Verificar que se crearon
        var profGarcia = ProfessorManager.findProfessor("prof_garcia");
        var profRodriguez = ProfessorManager.findProfessor("prof_rodriguez");

        assertNotNull(profGarcia, "Profesor prof_garcia debe existir");
        assertNotNull(profRodriguez, "Profesor prof_rodriguez debe existir");

        System.out.println("✓ Test profesores completado exitosamente\n");
    }

    // ============================================================
    //  TEST: CURSOS
    // ============================================================
    @Test
    @Order(3)
    void testCourses() {
        System.out.println("=== EJECUTANDO TEST: CURSOS ===");

        // Crear cursos con arrays de 4 elementos
        System.out.println("Creando cursos...");
        CourseManager.createCourse("Calculo Diferencial", new int[]{3, 0, 0, 0}, new ArrayList<>());
        CourseManager.createCourse("Algebra Lineal", new int[]{0, 2, 0, 0}, new ArrayList<>());

        // Para Calculo Integral con requisito, primero obtener el código del curso prerequisito
        Course calculoDif = CourseManager.findCourse("Calculo Diferencial");
        if (calculoDif != null) {
            CourseManager.createCourse("Calculo Integral", new int[]{0, 0, 4, 0},
                    List.of(String.valueOf(calculoDif.getCode())));
        }

        // Forzar recarga
        CourseManager.reload();

        // Verificar que se crearon
        var course = CourseManager.findCourse("Calculo Diferencial");
        assertNotNull(course, "Curso Calculo Diferencial debe existir");
        assertEquals("Calculo Diferencial", course.getName(), "Nombre del curso debe coincidir");

        // Verificar que hay cursos disponibles
        List<String[]> courses = CourseManager.getCourses();
        System.out.println("Cursos encontrados: " + courses.size());
        for (String[] c : courses) {
            System.out.println("  - " + Arrays.toString(c));
        }

        assertTrue(courses.size() >= 3, "Debe haber al menos 3 cursos creados, encontrados: " + courses.size());

        System.out.println("✓ Test cursos completado exitosamente\n");
    }

    // ============================================================
    //  TEST: GRUPOS (VERSIÓN DIAGNÓSTICO)
    // ============================================================
    @Test
    @Order(4)
    void testGroups() {
        System.out.println("=== EJECUTANDO TEST: GRUPOS ===");

        // Obtener cursos disponibles
        List<String[]> courses = CourseManager.getCourses();
        System.out.println("Cursos disponibles: " + courses.size());
        for (int i = 0; i < courses.size() && i < 3; i++) {
            System.out.println("  Curso " + i + ": " + Arrays.toString(courses.get(i)));
        }

        assertTrue(courses.size() >= 2,
                "Debe haber al menos 2 cursos para crear grupos. Encontrados: " + courses.size());

        // Crear grupos
        System.out.println("\nCreando grupos...");
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};
        String courseCode1 = courses.get(0)[0];
        System.out.println("Creando grupo 1 para curso: " + courseCode1);
        GroupManager.createGroup(days1, times1, "2025-1", courseCode1);

        String[] days2 = {"M", "J"};
        String[] times2 = {"9-11", "9-11"};
        String courseCode2 = courses.get(1)[0];
        System.out.println("Creando grupo 2 para curso: " + courseCode2);
        GroupManager.createGroup(days2, times2, "2025-1", courseCode2);

        // Verificar grupos creados
        System.out.println("\nVerificando grupos creados...");
        List<String[]> groups = GroupManager.loadGroups();
        System.out.println("Grupos encontrados: " + groups.size());
        for (String[] group : groups) {
            System.out.println("  Grupo: " + Arrays.toString(group));
        }

        assertTrue(groups.size() >= 2,
                "Debe haber al menos 2 grupos creados. Encontrados: " + groups.size());

        // Obtener número del primer grupo
        String groupNumber = groups.get(0)[0];
        System.out.println("\n=== ASIGNACIÓN DE PROFESOR ===");
        System.out.println("Grupo antes de asignar profesor: " + Arrays.toString(groups.get(0)));

        // Asignar profesor
        System.out.println("Asignando profesor 'prof_garcia' al grupo " + groupNumber);
        GroupManager.assignProfessor(groupNumber, "prof_garcia");

        // Recargar grupos después de la asignación
        groups = GroupManager.loadGroups();
        System.out.println("Grupo después de asignar profesor: " + Arrays.toString(groups.get(0)));

        // Verificar asignación
        String professor = GroupManager.getProfessor(groupNumber);
        System.out.println("Profesor retornado por getProfessor(): " + professor);

        // Verificación estricta
        assertNotNull(professor, "El profesor asignado no debe ser null");
        assertEquals("prof_garcia", professor,
                "El profesor asignado debe ser 'prof_garcia', pero se obtuvo: " + professor);

        System.out.println("✓ Test grupos completado exitosamente\n");
    }

    // ============================================================
    //  TEST: INSCRIPCIONES
    // ============================================================
    @Test
    @Order(5)
    void testEnrollment() {
        System.out.println("=== EJECUTANDO TEST: INSCRIPCIONES ===");

        // Obtener grupos disponibles
        List<String[]> groups = GroupManager.loadGroups();
        assertTrue(groups.size() >= 1, "Debe existir al menos un grupo. Encontrados: " + groups.size());

        String groupNumber = groups.get(0)[0];
        System.out.println("Inscribiendo estudiantes en grupo: " + groupNumber);

        // Inscribir estudiantes
        boolean juanEnrolled = EnrollmentManager.enrollStudent("juan123", groupNumber);
        boolean mariaEnrolled = EnrollmentManager.enrollStudent("maria456", groupNumber);

        System.out.println("Juan inscrito: " + juanEnrolled);
        System.out.println("Maria inscrita: " + mariaEnrolled);

        // Verificar inscripciones
        List<Group> studentEnrollments = EnrollmentManager.getCurrentEnrollments("juan123");
        System.out.println("Inscripciones de Juan: " + studentEnrollments.size());

        // Verificar en CSV directamente
        boolean foundInCSV = checkEnrollmentInCSV("juan123", groupNumber);
        System.out.println("Juan encontrado en CSV: " + foundInCSV);

        assertTrue(juanEnrolled || !studentEnrollments.isEmpty() || foundInCSV, 
            "Juan debe estar inscrito exitosamente");

        System.out.println("✓ Test inscripciones completado exitosamente\n");
    }

    // ============================================================
    //  TEST: CALIFICACIONES
    // ============================================================
    @Test
    @Order(6)
    void testGrades() {
        System.out.println("=== EJECUTANDO TEST: CALIFICACIONES ===");

        // Obtener grupos disponibles
        List<String[]> groups = GroupManager.loadGroups();
        assertFalse(groups.isEmpty(), "Debe existir al menos un grupo creado");

        String groupNumber = groups.get(0)[0];
        System.out.println("Grupo: " + groupNumber);

        // DIAGNÓSTICO DETALLADO
        System.out.println("=== DIAGNÓSTICO GRADE MANAGER ===");

        // 1. Verificar que los estudiantes existen
        Student juan = StudentManager.findStudent("juan123");
        Student maria = StudentManager.findStudent("maria456");
        System.out.println("Juan encontrado: " + (juan != null));
        System.out.println("Maria encontrada: " + (maria != null));

        // 2. Verificar inscripciones
        List<Group> enrollmentsJuan = EnrollmentManager.getCurrentEnrollments("juan123");
        List<Group> enrollmentsMaria = EnrollmentManager.getCurrentEnrollments("maria456");
        System.out.println("Inscripciones Juan: " + enrollmentsJuan.size());
        System.out.println("Inscripciones Maria: " + enrollmentsMaria.size());

        // 3. Ver calificaciones ANTES de crear
        List<Grade> gradesBefore = GradeManager.getGradesByStudent("juan123");
        System.out.println("Calificaciones de Juan ANTES: " + gradesBefore.size());

        // 4. Crear calificaciones
        System.out.println("Creando calificación para Juan...");
        boolean gradeCreated = GradeManager.createGrade("juan123", groupNumber, 4.5);
        System.out.println("Calificación creada exitosamente: " + gradeCreated);

        // 5. Ver calificaciones DESPUÉS de crear
        List<Grade> gradesAfter = GradeManager.getGradesByStudent("juan123");
        System.out.println("Calificaciones de Juan DESPUÉS: " + gradesAfter.size());

        if (!gradesAfter.isEmpty()) {
            Grade grade = gradesAfter.get(0);
            System.out.println("Calificación guardada: " + grade.getGrade());
            System.out.println("Estudiante: " + grade.getStudent().getUser());
            System.out.println("Grupo: " + grade.getGroup().getNumber());
            
            // Assert principal
            assertEquals(4.5, grade.getGrade(), 0.01, "La calificación debe ser 4.5");
        } else {
            // Verificar en CSV directamente como fallback
            System.out.println("=== VERIFICANDO ARCHIVO CSV ===");
            try {
                List<String[]> gradeRows = GradeManager.loadGrades();
                System.out.println("Filas en grades.csv: " + gradeRows.size());
                for (String[] row : gradeRows) {
                    System.out.println("Fila: " + Arrays.toString(row));
                    if (row.length >= 3 && row[0].equals("juan123") && row[1].equals(groupNumber)) {
                        double gradeValue = Double.parseDouble(row[2]);
                        assertEquals(4.5, gradeValue, 0.01, "Calificación en CSV debe ser 4.5");
                        return; // Test pasa
                    }
                }
            } catch (Exception e) {
                System.out.println("Error leyendo archivo: " + e.getMessage());
            }
            
            // Si llegamos aquí, fallar el test
            fail("La calificación no se guardó correctamente - no se encontró en memoria ni en CSV");
        }

        System.out.println("✓ Test calificaciones completado exitosamente\n");
    }

    // ============================================================
    //  MÉTODOS AUXILIARES
    // ============================================================
    
    private boolean checkEnrollmentInCSV(String studentUsername, String groupNumber) {
        try {
            List<String[]> enrollments = EnrollmentManager.loadEnrollments();
            for (String[] row : enrollments) {
                if (row.length >= 5 && 
                    row[0].equals(studentUsername) && 
                    row[1].equals(groupNumber) &&
                    "ACTIVE".equals(row[4])) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error leyendo CSV de inscripciones: " + e.getMessage());
        }
        return false;
    }
}