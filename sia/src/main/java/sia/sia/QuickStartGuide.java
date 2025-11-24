/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia;

import java.util.Arrays;
import sia.sia.business.*;

/**
 * Guia de inicio rapido del sistema SIA
 * Crea datos de ejemplo para probar el sistema
 * @author luzel
 */
public class QuickStartGuide {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    CONFIGURACION INICIAL DEL SISTEMA SIA");
        System.out.println("==================================================\n");

        createSampleData();
        printCredentials();
        startSystem();
    }

    /**
     * Crea datos de ejemplo en el sistema
     */
    private static void createSampleData() {
        System.out.println(">>> Creando datos de ejemplo...\n");

        // ==========================================
        // CREAR USUARIOS
        // ==========================================
        System.out.println("1. Creando usuarios...");
        
        // Administrador
        CSVManager.signUp("admin", "admin123", "admin");
        
        // Profesores
        ProfessorManager.createProfessor("prof_garcia", "prof123", "Ana", "Garcia", "1980-04-15");
        ProfessorManager.createProfessor("prof_rodriguez", "prof123", "Carlos", "Rodriguez", "1975-11-22");
        
        // Estudiantes
        StudentManager.createStudent("juan_perez", "student123", "Juan", "Perez", "2000-05-15");
        StudentManager.createStudent("maria_lopez", "student123", "Maria", "Lopez", "2001-03-20");
        StudentManager.createStudent("carlos_gomez", "student123", "Carlos", "Gomez", "2002-08-10");
        
        System.out.println("   Usuarios creados correctamente.\n");

        // ==========================================
        // CREAR CURSOS
        // ==========================================
        System.out.println("2. Creando cursos...");
        
        CourseManager.createCourse("Calculo Diferencial", 4, Arrays.asList());
        CourseManager.createCourse("Algebra Lineal", 4, Arrays.asList());
        CourseManager.createCourse("Fisica I", 4, Arrays.asList());
        CourseManager.createCourse("Programacion Basica", 3, Arrays.asList());
        CourseManager.createCourse("Calculo Integral", 4, Arrays.asList("10001"));
        
        System.out.println("   Cursos creados correctamente.\n");

        // ==========================================
        // CREAR GRUPOS
        // ==========================================
        System.out.println("3. Creando grupos...");
        
        var courses = CourseManager.getCourses();
        
        if (courses.size() >= 4) {
            // Grupo 1: Calculo Diferencial
            String[] days1 = {"L", "W", "V"};
            String[] times1 = {"7-9", "7-9", "7-9"};
            GroupManager.createGroup(days1, times1, "2025-1", courses.get(0)[0]);
            
            // Grupo 2: Algebra Lineal
            String[] days2 = {"M", "J"};
            String[] times2 = {"9-11", "9-11"};
            GroupManager.createGroup(days2, times2, "2025-1", courses.get(1)[0]);
            
            // Grupo 3: Fisica I
            String[] days3 = {"L", "W", "V"};
            String[] times3 = {"14-16", "14-16", "14-16"};
            GroupManager.createGroup(days3, times3, "2025-1", courses.get(2)[0]);
            
            // Grupo 4: Programacion Basica
            String[] days4 = {"M", "J"};
            String[] times4 = {"14-16", "14-16"};
            GroupManager.createGroup(days4, times4, "2025-1", courses.get(3)[0]);
        }
        
        System.out.println("   Grupos creados correctamente.\n");

        // ==========================================
        // ASIGNAR PROFESORES
        // ==========================================
        System.out.println("4. Asignando profesores a grupos...");
        
        var groups = GroupManager.loadGroups();
        
        if (groups.size() >= 4) {
            GroupManager.assignProfessor(groups.get(0)[0], "prof_garcia");
            GroupManager.assignProfessor(groups.get(1)[0], "prof_garcia");
            GroupManager.assignProfessor(groups.get(2)[0], "prof_rodriguez");
            GroupManager.assignProfessor(groups.get(3)[0], "prof_rodriguez");
        }
        System.out.println("   Profesores asignados correctamente.\n");

        // ==========================================
        // INSCRIBIR ESTUDIANTES
        // ==========================================
        System.out.println("5. Inscribiendo estudiantes...");
        
        if (groups.size() >= 4) {
            // Juan Perez - 2 materias
            EnrollmentManager.enrollStudent("juan_perez", groups.get(0)[0]);
            EnrollmentManager.enrollStudent("juan_perez", groups.get(1)[0]);
            
            // Maria Lopez - 3 materias
            EnrollmentManager.enrollStudent("maria_lopez", groups.get(0)[0]);
            EnrollmentManager.enrollStudent("maria_lopez", groups.get(2)[0]);
            EnrollmentManager.enrollStudent("maria_lopez", groups.get(3)[0]);
            
            // Carlos Gomez - 1 materia
            EnrollmentManager.enrollStudent("carlos_gomez", groups.get(1)[0]);
        }
        
        System.out.println("   Estudiantes inscritos correctamente.\n");

        // ==========================================
        // REGISTRAR CALIFICACIONES
        // ==========================================
        System.out.println("6. Registrando calificaciones de ejemplo...");
        
        if (groups.size() >= 4) {
            GradeManager.createGrade("juan_perez", groups.get(0)[0], 4.2);
            GradeManager.createGrade("juan_perez", groups.get(1)[0], 3.8);
            
            GradeManager.createGrade("maria_lopez", groups.get(0)[0], 4.5);
            GradeManager.createGrade("maria_lopez", groups.get(2)[0], 4.0);
            
            GradeManager.createGrade("carlos_gomez", groups.get(1)[0], 3.5);
        }
        
        System.out.println("   Calificaciones registradas correctamente.\n");

        System.out.println("==================================================");
        System.out.println("    CONFIGURACION COMPLETADA EXITOSAMENTE");
        System.out.println("==================================================\n");
    }

    /**
     * Muestra las credenciales de acceso
     */
    private static void printCredentials() {
        System.out.println("\n==================================================");
        System.out.println("         CREDENCIALES DE ACCESO");
        System.out.println("==================================================\n");
        
        System.out.println("ADMINISTRADOR:");
        System.out.println("  Usuario: admin");
        System.out.println("  Contrasena: admin123\n");
        
        System.out.println("PROFESORES:");
        System.out.println("  Usuario: prof_garcia");
        System.out.println("  Contrasena: prof123");
        System.out.println();
        System.out.println("  Usuario: prof_rodriguez");
        System.out.println("  Contrasena: prof123\n");
        
        System.out.println("ESTUDIANTES:");
        System.out.println("  Usuario: juan_perez");
        System.out.println("  Contrasena: student123");
        System.out.println();
        System.out.println("  Usuario: maria_lopez");
        System.out.println("  Contrasena: student123");
        System.out.println();
        System.out.println("  Usuario: carlos_gomez");
        System.out.println("  Contrasena: student123\n");
        
        System.out.println("==================================================\n");
    }

    /**
     * Inicia el sistema principal
     */
    private static void startSystem() {
        System.out.println("Iniciando sistema en 3 segundos...\n");
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        Main.main(new String[]{});
    }

    /**
     * Metodo alternativo para solo crear datos sin iniciar el sistema
     */
    public static void setupOnly() {
        System.out.println("Configurando datos de ejemplo...\n");
        createSampleData();
        printCredentials();
        System.out.println("Configuracion completada. Ejecute Main.java para iniciar el sistema.");
    }
}