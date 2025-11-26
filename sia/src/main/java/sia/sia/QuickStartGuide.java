/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia;

import java.util.Arrays;
import java.util.List;
import sia.sia.business.*;
import sia.sia.data.Course;

/**
 * Guia de inicio rapido del sistema SIA 
 * Crea datos de ejemplo para probar el sistema
 *
 * @author luzel
 */
public class QuickStartGuide {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    CONFIGURACION INICIAL DEL SISTEMA SIA");
        System.out.println("==================================================\n");

        setupOnly();
    }

    /**
     * Crea datos de ejemplo en el sistema
     */
    private static void createSampleData() {
        System.out.println(">>> Creando datos de ejemplo...\n");

        // Limpiar sistema primero
        clearSystem();

        // ==========================================
        // CREAR USUARIOS
        // ==========================================
        System.out.println("1. Creando usuarios...");

        // Administrador
        CSVManager.signUp("admin", "admin123", "admin");

        // Profesores
        CSVManager.signUp("prof_garcia", "prof123", "professor");
        CSVManager.signUp("prof_rodriguez", "prof123", "professor");

        // Estudiantes
        CSVManager.signUp("juan_perez", "student123", "student");
        CSVManager.signUp("maria_lopez", "student123", "student");
        CSVManager.signUp("carlos_gomez", "student123", "student");

        System.out.println("   ✓ Usuarios creados correctamente.\n");

        // ==========================================
        // CREAR DATOS DE ESTUDIANTES Y PROFESORES
        // ==========================================
        System.out.println("2. Creando datos de estudiantes y profesores...");

        // Estudiantes
        StudentManager.createStudent("juan_perez", "student123", "Juan", "Perez", "2000-05-15");
        StudentManager.createStudent("maria_lopez", "student123", "Maria", "Lopez", "1999-08-22");
        StudentManager.createStudent("carlos_gomez", "student123", "Carlos", "Gomez", "2001-03-10");

        // Profesores
        ProfessorManager.createProfessor("prof_garcia", "prof123", "Luis", "Garcia", "1975-06-15");
        ProfessorManager.createProfessor("prof_rodriguez", "prof123", "Ana", "Rodriguez", "1980-09-20");

        System.out.println("   ✓ Datos de estudiantes y profesores creados.\n");

        // ==========================================
        // CREAR CURSOS
        // ==========================================
        System.out.println("3. Creando cursos...");

        // Formato: {Fundamentación, Disciplinar, Libre elección, Nivelación}
        CourseManager.createCourse("Calculo Diferencial", new int[]{4, 0, 0, 0}, Arrays.asList());
        CourseManager.createCourse("Algebra Lineal", new int[]{4, 0, 0, 0}, Arrays.asList());
        CourseManager.createCourse("Fisica I", new int[]{2, 0, 0, 0}, Arrays.asList()); 
        CourseManager.createCourse("Programacion Basica", new int[]{0, 3, 0, 0}, Arrays.asList());
        
        // Obtener código de Calculo Diferencial para usarlo como prerrequisito
        Course calculoDif = CourseManager.findCourse("Calculo Diferencial");
        if (calculoDif != null) {
            CourseManager.createCourse("Calculo Integral", new int[]{4, 0, 0, 0}, 
                Arrays.asList(String.valueOf(calculoDif.getCode())));
        }

        System.out.println("   ✓ Cursos creados correctamente.\n");

        // ==========================================
        // CREAR GRUPOS
        // ==========================================
        System.out.println("4. Creando grupos...");

        List<String[]> courses = CourseManager.getCourses();
        System.out.println("   Cursos disponibles: " + courses.size());

        if (courses.size() >= 4) {
            // Grupo 1: Calculo Diferencial - Lunes, Miércoles, Viernes 7-9
            String[] days1 = {"L", "W", "V"};
            String[] times1 = {"7-9", "7-9", "7-9"};
            GroupManager.createGroup(days1, times1, "2025-1", courses.get(0)[0]);

            // Grupo 2: Algebra Lineal - Martes, Jueves 9-11
            String[] days2 = {"M", "J"};
            String[] times2 = {"9-11", "9-11"};
            GroupManager.createGroup(days2, times2, "2025-1", courses.get(1)[0]);

            // Grupo 3: Fisica I - Lunes, Miércoles, Viernes 14-16
            String[] days3 = {"L", "W", "V"};
            String[] times3 = {"14-16", "14-16", "14-16"};
            GroupManager.createGroup(days3, times3, "2025-1", courses.get(2)[0]);

            // Grupo 4: Programacion Basica - Martes, Jueves 14-16
            String[] days4 = {"M", "J"};
            String[] times4 = {"14-16", "14-16"};
            GroupManager.createGroup(days4, times4, "2025-1", courses.get(3)[0]);
        }

        System.out.println("   ✓ Grupos creados correctamente.\n");

        // ==========================================
        // ASIGNAR PROFESORES
        // ==========================================
        System.out.println("5. Asignando profesores a grupos...");

        List<String[]> groups = GroupManager.loadGroups();
        System.out.println("   Grupos disponibles: " + groups.size());

        if (groups.size() >= 4) {
            GroupManager.assignProfessor(groups.get(0)[0], "prof_garcia"); // Calculo Diferencial
            GroupManager.assignProfessor(groups.get(1)[0], "prof_garcia"); // Algebra Lineal
            GroupManager.assignProfessor(groups.get(2)[0], "prof_rodriguez"); // Fisica I
            GroupManager.assignProfessor(groups.get(3)[0], "prof_rodriguez"); // Programacion Basica
        }

        System.out.println("   ✓ Profesores asignados correctamente.\n");

        // ==========================================
        // INSCRIBIR ESTUDIANTES
        // ==========================================
        System.out.println("6. Inscribiendo estudiantes...");

        if (groups.size() >= 4) {
            String grupoCalculo = groups.get(0)[0];
            String grupoAlgebra = groups.get(1)[0];
            String grupoFisica = groups.get(2)[0];
            String grupoProgramacion = groups.get(3)[0];

            // Juan Perez - 2 materias (sin conflicto de horario)
            System.out.println("   Inscribiendo a Juan Perez...");
            EnrollmentManager.enrollStudent("juan_perez", grupoCalculo);
            EnrollmentManager.enrollStudent("juan_perez", grupoAlgebra);

            // Maria Lopez - 3 materias 
            System.out.println("   Inscribiendo a Maria Lopez...");
            EnrollmentManager.enrollStudent("maria_lopez", grupoCalculo);
            EnrollmentManager.enrollStudent("maria_lopez", grupoFisica);
            EnrollmentManager.enrollStudent("maria_lopez", grupoProgramacion);

            // Carlos Gomez - 1 materia
            System.out.println("   Inscribiendo a Carlos Gomez...");
            EnrollmentManager.enrollStudent("carlos_gomez", grupoAlgebra);
        }

        System.out.println("   ✓ Estudiantes inscritos correctamente.\n");

        // ==========================================
        // REGISTRAR CALIFICACIONES
        // ==========================================
        System.out.println("7. Registrando calificaciones de ejemplo...");

        if (groups.size() >= 4) {
            String grupoCalculo = groups.get(0)[0];
            String grupoAlgebra = groups.get(1)[0];
            String grupoFisica = groups.get(2)[0];

            // Calificaciones para Juan Perez
            GradeManager.createGrade("juan_perez", grupoCalculo, 4.2);
            GradeManager.createGrade("juan_perez", grupoAlgebra, 3.8);

            // Calificaciones para Maria Lopez
            GradeManager.createGrade("maria_lopez", grupoCalculo, 4.5);
            GradeManager.createGrade("maria_lopez", grupoFisica, 4.0);

            // Calificación para Carlos Gomez
            GradeManager.createGrade("carlos_gomez", grupoAlgebra, 3.5);
        }

        System.out.println("   ✓ Calificaciones registradas correctamente.\n");

        System.out.println("==================================================");
        System.out.println("    CONFIGURACION COMPLETADA EXITOSAMENTE");
        System.out.println("==================================================\n");
    }

    /**
     * Limpia el sistema antes de crear datos nuevos
     */
    private static void clearSystem() {
        System.out.println(">>> Limpiando sistema existente...");
        
        // Limpiar todos los managers
        StudentManager.reload();
        ProfessorManager.reload();
        CourseManager.reload();
        GroupManager.reload();
        EnrollmentManager.reload();
        GradeManager.reload();
        
        System.out.println("   ✓ Sistema limpiado.\n");
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
        System.out.println("  Contraseña: admin123");
        System.out.println("  Funciones: Gestión completa del sistema\n");

        System.out.println("PROFESORES:");
        System.out.println("  Usuario: prof_garcia");
        System.out.println("  Contraseña: prof123");
        System.out.println("  Asignado: Cálculo Diferencial y Álgebra Lineal");
        System.out.println();
        System.out.println("  Usuario: prof_rodriguez");
        System.out.println("  Contraseña: prof123");
        System.out.println("  Asignado: Física I y Programación Básica\n");

        System.out.println("ESTUDIANTES:");
        System.out.println("  Usuario: juan_perez");
        System.out.println("  Contraseña: student123");
        System.out.println("  Inscrito: Cálculo Diferencial, Álgebra Lineal");
        System.out.println();
        System.out.println("  Usuario: maria_lopez");
        System.out.println("  Contraseña: student123");
        System.out.println("  Inscrito: Cálculo Diferencial, Física I, Programación Básica");
        System.out.println();
        System.out.println("  Usuario: carlos_gomez");
        System.out.println("  Contraseña: student123");
        System.out.println("  Inscrito: Álgebra Lineal\n");

        System.out.println("HORARIOS CONFIGURADOS:");
        System.out.println("  • Lunes, Miércoles, Viernes 7-9: Cálculo Diferencial");
        System.out.println("  • Martes, Jueves 9-11: Álgebra Lineal");
        System.out.println("  • Lunes, Miércoles, Viernes 14-16: Física I");
        System.out.println("  • Martes, Jueves 14-16: Programación Básica\n");

        System.out.println("==================================================\n");
    }

    /**
     * Inicia el sistema principal
     */
    private static void startSystem() {
        System.out.println("Iniciando sistema en 3 segundos...\n");

        try {
            for (int i = 3; i > 0; i--) {
                System.out.println("Iniciando en " + i + "...");
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Iniciar el sistema principal
        Main.main(new String[]{});
    }

    /**
     * Metodo alternativo para solo crear datos sin iniciar el sistema
     */
    public static void setupOnly() {
        System.out.println(">>> CONFIGURANDO DATOS DE EJEMPLO <<<\n");
        createSampleData();
        printCredentials();
        
        System.out.println("¿Desea iniciar el sistema ahora? (s/n): ");
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            String respuesta = scanner.nextLine().trim().toLowerCase();
            
            if (respuesta.equals("s") || respuesta.equals("si")) {
                startSystem();
            } else {
                System.out.println("\n✅ Configuración completada.");
                System.out.println("Ejecute Main.java para iniciar el sistema cuando lo desee.");
            }
        } catch (Exception e) {
            System.out.println("\n✅ Configuración completada.");
            System.out.println("Ejecute Main.java para iniciar el sistema.");
        }
    }
}