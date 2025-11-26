package sia.sia.ui;

import java.util.List;
import java.util.Scanner;
import sia.sia.business.*;
import sia.sia.data.Group;
import sia.sia.data.Course;

public class StudentMenu {

    private static final int REQUIRED_FUNDAMENTAL_CREDITS = 17;
    private static final int REQUIRED_DISCIPLINARY_CREDITS = 118;
    private static final int REQUIRED_FREE_ELECTIVE_CREDITS = 34;
    private static final int REQUIRED_TOTAL_CREDITS = 169;
    private static final int REQUIRED_LEVEL_CREDITS = 12;
    private static final int REQUIRED_STUDENT_TOTAL_CREDITS = 181;

    private static String currentUser;

    public static boolean show(String username) {
        currentUser = username;
        Scanner scan = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMainMenu();
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    GradeManager.listStudentGrades(currentUser);
                case "2" ->
                    listCurrentEnrollments();
                case "3" ->
                    enrollInCourse(scan);
                case "4" ->
                    withdrawFromCourse(scan);
                case "5" ->
                    listAvailableCourses();
                case "6" ->
                    viewSchedule();
                case "7" -> {
                    double avg = GradeManager.calculateStudentAverage(currentUser);
                    System.out.printf("Promedio: %.2f\n", avg);
                }
                case "8" -> {
                    viewApprovedCredits();
                }
                case "0" -> {
                    System.out.println("Cerrando sesion...");
                    return false;
                }
                default ->
                    System.out.println("Opcion invalida");
            }
        }
        return false;
    }

    private static void printMainMenu() {
        System.out.println("\n--- MENU ESTUDIANTE ---");
        System.out.println("1. Ver Historial de Calificaciones");
        System.out.println("2. Ver Inscripciones Actuales");
        System.out.println("3. Inscribir Materia");
        System.out.println("4. Retirar Materia");
        System.out.println("5. Ver Cursos Disponibles");
        System.out.println("6. Ver Horario");
        System.out.println("7. Ver Promedio");
        System.out.println("8. Ver Creditos Aprobados");
        System.out.println("0. Salir");
        System.out.print("Seleccione: ");
    }

    private static void listCurrentEnrollments() {
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        if (enrollments.isEmpty()) {
            System.out.println("No tienes inscripciones activas.");
            return;
        }

        System.out.println("\n=== INSCRIPCIONES ACTUALES ===");
        System.out.printf("%-10s %-15s %-30s %-10s%n",
                "GRUPO", "CURSO", "NOMBRE", "SEMESTRE");
        System.out.println("----------------------------------------------------------------");

        for (Group group : enrollments) {
            Course course = group.getRepresents();
            if (course != null) {
                System.out.printf("%-10s %-15s %-30s %-10s%n",
                        group.getNumber(),
                        course.getCode(),
                        course.getName().length() > 28 ? course.getName().substring(0, 25) + "..." : course.getName(),
                        group.getSemester());
            }
        }
    }

    private static void listAvailableCourses() {
        // Si no existe este método en EnrollmentManager, usar alternativa
        System.out.println("\n=== CURSOS DISPONIBLES ===");

        // Método alternativo si getAvailableCoursesForStudent no existe
        List<Group> allGroups = GroupManager.getGroupsByStudent(currentUser);
        List<Group> currentEnrollments = EnrollmentManager.getCurrentEnrollments(currentUser);

        System.out.printf("%-10s %-15s %-30s %-10s%n",
                "GRUPO", "CURSO", "NOMBRE", "SEMESTRE");
        System.out.println("----------------------------------------------------------------");

        int availableCount = 0;
        for (Group group : allGroups) {
            Course course = group.getRepresents();
            if (course != null) {
                // Verificar si ya está inscrito en este curso
                boolean alreadyEnrolled = false;
                for (Group enrolled : currentEnrollments) {
                    if (enrolled.getRepresents() != null
                            && enrolled.getRepresents().getCode() == course.getCode()) {
                        alreadyEnrolled = true;
                        break;
                    }
                }

                if (!alreadyEnrolled && GroupManager.getAvailableSpots("" + group.getNumber()) > 0) {
                    System.out.printf("%-10s %-15s %-30s %-10s%n",
                            group.getNumber(),
                            course.getCode(),
                            course.getName().length() > 28 ? course.getName().substring(0, 25) + "..." : course.getName(),
                            group.getSemester());
                    availableCount++;
                }
            }
        }

        if (availableCount == 0) {
            System.out.println("No hay cursos disponibles para inscripción.");
        } else {
            System.out.println("Total de cursos disponibles: " + availableCount);
        }
    }

    private static void enrollInCourse(Scanner scan) {
        System.out.print("Numero de grupo a inscribir: ");
        String groupNumber = scan.nextLine().trim();

        boolean success = EnrollmentManager.enrollStudent(currentUser, groupNumber);
        if (success) {
            System.out.println("Inscripción exitosa en el grupo: " + groupNumber);
        } else {
            System.out.println("No se pudo realizar la inscripción en el grupo: " + groupNumber);
        }
    }

    private static void withdrawFromCourse(Scanner scan) {
        // Mostrar inscripciones actuales primero
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        if (enrollments.isEmpty()) {
            System.out.println("No tienes materias inscritas para retirar.");
            return;
        }

        System.out.println("\nTus inscripciones actuales:");
        for (Group group : enrollments) {
            Course course = group.getRepresents();
            if (course != null) {
                System.out.printf("Grupo: %s | Curso: %s - %s%n",
                        group.getNumber(), course.getCode(), course.getName());
            }
        }

        System.out.print("Numero de grupo a retirar: ");
        String groupNumber = scan.nextLine().trim();

        boolean success = EnrollmentManager.unenrollStudent(currentUser, groupNumber);
        if (success) {
            System.out.println("Retiro exitoso del grupo: " + groupNumber);
        } else {
            System.out.println("No se pudo realizar el retiro del grupo: " + groupNumber);
        }
    }

    private static void viewSchedule() {
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        if (enrollments.isEmpty()) {
            System.out.println("No tienes materias inscritas");
            return;
        }

        System.out.println("\n=== HORARIO ACTUAL ===");
        for (Group group : enrollments) {
            Course course = group.getRepresents();
            if (course != null) {
                // Asumiendo que Group tiene métodos para obtener horario
                String scheduleInfo = "Horario no disponible";
                // Si existe getScheduleFormatted(), usarlo, sino mostrar información básica
                try {
                    scheduleInfo = group.getScheduleFormatted();
                } catch (Exception e) {
                    scheduleInfo = "Consulta horario con administrador";
                }

                System.out.printf("%s (%s - Grupo %s) -> %s%n",
                        course.getName(),
                        course.getCode(),
                        group.getNumber(),
                        scheduleInfo);
            }
        }
    }

    private static void viewApprovedCredits() {


        List<String> passedCourses = GradeManager.getPassedCourses(currentUser);
        if (passedCourses.isEmpty()) {
            System.out.println("No tienes cursos aprobados.");
            return;
        }

        // Inicializar contadores de créditos por categoría
        int fundamentalCredits = 0;
        int disciplinaryCredits = 0;
        int freeElectiveCredits = 0;
        int levelCredits = 0;

        System.out.println("\n=== CRÉDITOS APROBADOS ===");
        System.out.println("Cursos aprobados: " + String.join(", ", passedCourses));
        System.out.println("Total de cursos aprobados: " + passedCourses.size());

        // Calcular créditos por categoría basado en el tipo de créditos de cada materia
        for (String courseCode : passedCourses) {
            try {
                long code = Long.parseLong(courseCode);
                Course course = CourseManager.findCourse(code);
                if (course != null) {
                    // Asumiendo que getCredits() retorna un array donde:
                    // créditos[0] = fundamentales, créditos[1] = disciplinarios, 
                    // créditos[2] = libre elección, créditos[3] = nivel
                    int[] credits = course.getCredits();

                    if (credits.length >= 4) {
                        fundamentalCredits += credits[0];
                        disciplinaryCredits += credits[1];
                        freeElectiveCredits += credits[2];
                        levelCredits += credits[3];
                    } else if (credits.length >= 3) {
                        fundamentalCredits += credits[0];
                        disciplinaryCredits += credits[1];
                        freeElectiveCredits += credits[2];
                    } else if (credits.length >= 2) {
                        fundamentalCredits += credits[0];
                        disciplinaryCredits += credits[1];
                    } else if (credits.length >= 1) {
                        disciplinaryCredits += credits[0]; // Por defecto a disciplinarios
                    }
                }
            } catch (NumberFormatException e) {
                // Ignorar códigos inválidos
            }
        }

        int totalApprovedCredits = fundamentalCredits + disciplinaryCredits + freeElectiveCredits;
        int totalStudentCredits = totalApprovedCredits + levelCredits;

        // Mostrar tabla similar a la imagen
        System.out.println("\n" + "=".repeat(90));
        System.out.printf("%-15s | %-10s | %-12s | %-12s | %-8s | %-7s | %-15s%n",
                "TIPO", "FUNDAM.", "DISCIPL.", "LIBRE E.", "TOTAL", "NIVEL", "TOTAL EST.");
        System.out.println("-".repeat(90));

        // Línea de créditos exigidos
        System.out.printf("%-15s | %-10d | %-12d | %-12d | %-8d | %-7d | %-15d%n",
                "EXIGIDOS",
                REQUIRED_FUNDAMENTAL_CREDITS,
                REQUIRED_DISCIPLINARY_CREDITS,
                REQUIRED_FREE_ELECTIVE_CREDITS,
                REQUIRED_TOTAL_CREDITS,
                REQUIRED_LEVEL_CREDITS,
                REQUIRED_STUDENT_TOTAL_CREDITS);

        // Línea de créditos aprobados
        System.out.printf("%-15s | %-10d | %-12d | %-12d | %-8d | %-7d | %-15d%n",
                "APROBADOS",
                fundamentalCredits,
                disciplinaryCredits,
                freeElectiveCredits,
                totalApprovedCredits,
                levelCredits,
                totalStudentCredits);

        // Línea de créditos aprobados según plan (sin nivel)
        System.out.printf("%-15s | %-10d | %-12d | %-12d | %-8d | %-7s | %-15d%n",
                "APROBADOS PLAN",
                fundamentalCredits,
                disciplinaryCredits,
                freeElectiveCredits,
                totalApprovedCredits,
                "--",
                totalApprovedCredits);

        // Línea de créditos pendientes
        int pendingFundamental = Math.max(0, REQUIRED_FUNDAMENTAL_CREDITS - fundamentalCredits);
        int pendingDisciplinary = Math.max(0, REQUIRED_DISCIPLINARY_CREDITS - disciplinaryCredits);
        int pendingFreeElective = Math.max(0, REQUIRED_FREE_ELECTIVE_CREDITS - freeElectiveCredits);
        int pendingTotal = Math.max(0, REQUIRED_TOTAL_CREDITS - totalApprovedCredits);
        int pendingLevel = Math.max(0, REQUIRED_LEVEL_CREDITS - levelCredits);
        int pendingStudentTotal = Math.max(0, REQUIRED_STUDENT_TOTAL_CREDITS - totalStudentCredits);

        System.out.printf("%-15s | %-10d | %-12d | %-12d | %-8d | %-7d | %-15d%n",
                "PENDIENTES",
                pendingFundamental,
                pendingDisciplinary,
                pendingFreeElective,
                pendingTotal,
                pendingLevel,
                pendingStudentTotal);

        System.out.println("=".repeat(90));

        // Mostrar porcentajes de avance
        System.out.println("\n--- PORCENTAJE DE AVANCE ---");
        System.out.printf("Fundamentales: %d/%d (%.1f%%)%n",
                fundamentalCredits, REQUIRED_FUNDAMENTAL_CREDITS,
                (REQUIRED_FUNDAMENTAL_CREDITS > 0
                        ? (fundamentalCredits * 100.0 / REQUIRED_FUNDAMENTAL_CREDITS) : 0));

        System.out.printf("Disciplinarios: %d/%d (%.1f%%)%n",
                disciplinaryCredits, REQUIRED_DISCIPLINARY_CREDITS,
                (REQUIRED_DISCIPLINARY_CREDITS > 0
                        ? (disciplinaryCredits * 100.0 / REQUIRED_DISCIPLINARY_CREDITS) : 0));

        System.out.printf("Libre elección: %d/%d (%.1f%%)%n",
                freeElectiveCredits, REQUIRED_FREE_ELECTIVE_CREDITS,
                (REQUIRED_FREE_ELECTIVE_CREDITS > 0
                        ? (freeElectiveCredits * 100.0 / REQUIRED_FREE_ELECTIVE_CREDITS) : 0));

        System.out.printf("Nivel: %d/%d (%.1f%%)%n",
                levelCredits, REQUIRED_LEVEL_CREDITS,
                (REQUIRED_LEVEL_CREDITS > 0
                        ? (levelCredits * 100.0 / REQUIRED_LEVEL_CREDITS) : 0));

        System.out.printf("Total plan: %d/%d (%.1f%%)%n",
                totalApprovedCredits, REQUIRED_TOTAL_CREDITS,
                (REQUIRED_TOTAL_CREDITS > 0
                        ? (totalApprovedCredits * 100.0 / REQUIRED_TOTAL_CREDITS) : 0));

        System.out.printf("Total general: %d/%d (%.1f%%)%n",
                totalStudentCredits, REQUIRED_STUDENT_TOTAL_CREDITS,
                (REQUIRED_STUDENT_TOTAL_CREDITS > 0
                        ? (totalStudentCredits * 100.0 / REQUIRED_STUDENT_TOTAL_CREDITS) : 0));

        // Mostrar resumen de estado
        System.out.println("\n--- ESTADO ACTUAL ---");
        if (totalStudentCredits >= REQUIRED_STUDENT_TOTAL_CREDITS) {
            System.out.println("¡FELICIDADES! Has completado todos los créditos requeridos.");
        } else {
            int remaining = REQUIRED_STUDENT_TOTAL_CREDITS - totalStudentCredits;
            System.out.printf("Te faltan %d créditos para completar tu plan de estudios.%n", remaining);
        }
    }
}
