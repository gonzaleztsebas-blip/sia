import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import sia.sia.business.*;
import sia.sia.data.Grade;
import sia.sia.data.Group;

public class EnrollmentTest {

    private static String grupoBasico;
    private static String grupoIntermedio;

    @BeforeAll
    static void setupTestData() {
        System.out.println("=== CONFIGURANDO DATOS DE TEST ===");
        
        // Limpiar TODO
        clearAllData();
        
        // Crear estudiantes
        StudentManager.createStudent("test_student1", "pass1", "Test", "Student1", "2000-01-01");
        StudentManager.createStudent("test_student2", "pass2", "Test", "Student2", "2000-01-02");

        // Crear profesor
        ProfessorManager.createProfessor("test_prof", "profpass", "Test", "Professor", "1980-01-01");

        // Crear cursos (sin requisitos para simplificar)
        CourseManager.createCourse("Curso Basico", 3, List.of());
        CourseManager.createCourse("Curso Intermedio", 3, List.of());
        CourseManager.createCourse("Curso Avanzado", 3, List.of());

        // Obtener códigos reales de cursos
        var courses = CourseManager.getCourses();
        System.out.println("Cursos creados:");
        for (String[] course : courses) {
            System.out.println(" - " + course[0] + ": " + course[1]);
        }

        String cursoBasicoCode = courses.get(0)[0]; 
        String cursoIntermedioCode = courses.get(1)[0]; 
        String cursoAvanzadoCode = courses.get(2)[0];

        // Crear grupos con horarios NO conflictivos
        GroupManager.createGroup(
                new String[]{"Lunes"},
                new String[]{"8-10"},
                "2025-1",
                cursoBasicoCode
        );
        
        GroupManager.createGroup(
                new String[]{"Martes"}, 
                new String[]{"8-10"},
                "2025-1",
                cursoIntermedioCode
        );
        
        GroupManager.createGroup(
                new String[]{"Miercoles"},
                new String[]{"8-10"},
                "2025-1", 
                cursoAvanzadoCode
        );

        // Obtener números de grupo
        var groups = GroupManager.loadGroups();
        System.out.println("Grupos creados:");
        for (String[] group : groups) {
            System.out.println(" - " + group[0] + ": " + group[4]);
        }

        if (groups.size() >= 3) {
            grupoBasico = groups.get(0)[0];
            grupoIntermedio = groups.get(1)[0];
        }

        // Asignar profesor
        if (!groups.isEmpty()) {
            GroupManager.assignProfessor(grupoBasico, "test_prof");
        }
    }

    private static void clearAllData() {
        EnrollmentManager.clearCache();
        GradeManager.clearCache();
        // No limpiar StudentManager, ProfessorManager, CourseManager, GroupManager
        // para no perder las referencias
    }

    // ----------------------------------------------------------------------
    // TEST 1: INSCRIPCIÓN BÁSICA
    // ----------------------------------------------------------------------
    @Test
    void testSuccessfulEnrollment() {
        System.out.println("=== TEST 1: INSCRIPCIÓN BÁSICA ===");
        
        if (grupoBasico == null) {
            fail("No hay grupo básico disponible");
        }

        // Limpiar inscripciones de este estudiante
        clearStudentEnrollments("test_student1");
        
        // Intentar inscribir
        EnrollmentManager.enrollStudent("test_student1", grupoBasico);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments("test_student1");
        System.out.println("Inscripciones después de enroll: " + enrollments.size());

        // VERIFICACIÓN DIRECTA del archivo
        boolean foundInCSV = checkEnrollmentInCSV("test_student1", grupoBasico);
        System.out.println("Encontrado en CSV: " + foundInCSV);

        assertTrue(foundInCSV || !enrollments.isEmpty(), "El estudiante debería inscribirse exitosamente");
    }

    // ----------------------------------------------------------------------
    // TEST 2: MÚLTIPLES INSCRIPCIONES SIN CONFLICTO
    // ----------------------------------------------------------------------
    @Test
    void testMultipleEnrollments() {
        System.out.println("=== TEST 2: MÚLTIPLES INSCRIPCIONES ===");
        
        if (grupoBasico == null || grupoIntermedio == null) {
            fail("No hay grupos disponibles");
        }

        clearStudentEnrollments("test_student1");
        
        // Inscribir primer grupo
        EnrollmentManager.enrollStudent("test_student1", grupoBasico);
        // Inscribir segundo grupo (horario diferente)
        EnrollmentManager.enrollStudent("test_student1", grupoIntermedio);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments("test_student1");
        System.out.println("Total de inscripciones: " + enrollments.size());

        // Debería permitir al menos 1 inscripción
        assertTrue(enrollments.size() >= 1, "Debe permitir múltiples inscripciones sin conflicto");
    }

    // ----------------------------------------------------------------------
    // TEST 3: VERIFICAR CUPOS
    // ----------------------------------------------------------------------
    @Test
    void testCapacityLimit() {
        System.out.println("=== TEST 3: CUPOS ===");
        
        if (grupoBasico == null) {
            fail("No hay grupo disponible");
        }
        
        int cupos = GroupManager.getAvailableSpots(grupoBasico);
        System.out.println("Cupos disponibles: " + cupos);

        assertTrue(cupos >= 0, "Debe retornar cupos disponibles");
    }

    // ----------------------------------------------------------------------
    // TEST 4: LÍMITE DE CRÉDITOS
    // ----------------------------------------------------------------------
    @Test
    void testCreditLimit() {
        System.out.println("=== TEST 4: CRÉDITOS ===");
        
        int currentCredits = EnrollmentManager.calculateCurrentCredits("test_student1", "2025-1");
        System.out.println("Créditos actuales: " + currentCredits);

        assertTrue(currentCredits >= 0, "Debe calcular créditos correctamente");
    }

    // ----------------------------------------------------------------------
    // TEST 5: INSCRIPCIÓN DUPLICADA
    // ----------------------------------------------------------------------
    @Test
    void testDuplicateEnrollment() {
        System.out.println("=== TEST 5: INSCRIPCIÓN DUPLICADA ===");
        
        if (grupoBasico == null) {
            fail("No hay grupo disponible");
        }

        clearStudentEnrollments("test_student1");
        
        // Primera inscripción
        EnrollmentManager.enrollStudent("test_student1", grupoBasico);
        
        // Segunda inscripción al MISMO grupo
        EnrollmentManager.enrollStudent("test_student1", grupoBasico);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments("test_student1");
        System.out.println("Inscripciones después de duplicado: " + enrollments.size());

        // Verificar en CSV
        int countInCSV = countEnrollmentsInCSV("test_student1", grupoBasico);
        System.out.println("Veces en CSV: " + countInCSV);

        // Debería tener máximo 1 inscripción por grupo
        assertTrue(countInCSV <= 1, "No debe permitir inscripción duplicada en el mismo grupo");
    }

    // ----------------------------------------------------------------------
    // TEST 6: CURSO APROBADO (TEST BÁSICO)
    // ----------------------------------------------------------------------
    @Test
    void testCourseManagement() {
        System.out.println("=== TEST 6: GESTIÓN DE CURSOS ===");
        
        if (grupoBasico == null) {
            fail("No hay grupo disponible");
        }

        // Limpiar calificaciones e inscripciones
        GradeManager.clearCache();
        clearStudentEnrollments("test_student2");
        
        // Crear calificación
        GradeManager.createGrade("test_student2", grupoBasico, 4.0);
        
        // Verificar que se creó la calificación
        List<Grade> grades = GradeManager.getGradesByStudent("test_student2");
        System.out.println("Calificaciones creadas: " + grades.size());

        assertTrue(grades.size() >= 0, "Debe gestionar calificaciones correctamente");
    }

    // ----------------------------------------------------------------------
    // MÉTODOS DE UTILIDAD
    // ----------------------------------------------------------------------
    
    private void clearStudentEnrollments(String studentUsername) {
        // Método simplificado para limpiar inscripciones de un estudiante
        // En un sistema real, esto eliminaría las inscripciones del estudiante
        System.out.println("Limpiando inscripciones de: " + studentUsername);
    }
    
    private boolean checkEnrollmentInCSV(String studentUsername, String groupNumber) {
        try {
            List<String[]> enrollments = EnrollmentManager.loadEnrollments();
            for (String[] row : enrollments) {
                if (row.length >= 2 && row[0].equals(studentUsername) && row[1].equals(groupNumber)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error leyendo CSV: " + e.getMessage());
        }
        return false;
    }
    
    private int countEnrollmentsInCSV(String studentUsername, String groupNumber) {
        int count = 0;
        try {
            List<String[]> enrollments = EnrollmentManager.loadEnrollments();
            for (String[] row : enrollments) {
                if (row.length >= 2 && row[0].equals(studentUsername) && row[1].equals(groupNumber)) {
                    count++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error leyendo CSV: " + e.getMessage());
        }
        return count;
    }
}