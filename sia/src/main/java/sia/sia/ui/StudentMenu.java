/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.List;
import java.util.Scanner;
import sia.sia.business.*;
import sia.sia.data.Course;
import sia.sia.data.Group;

/**
 * Menu principal para estudiantes
 * Permite ver historial, inscribir materias y gestionar su informacion
 * @author luzel
 */
public class StudentMenu {

    private static String currentUser;

    public static boolean show(String username) {
        currentUser = username;
        
        Scanner scan = new Scanner(System.in);
            boolean running = true;

            System.out.println("\n==============================================");
            System.out.println("    SISTEMA DE INFORMACION ACADEMICO");
            System.out.println("    Universidad Nacional de Colombia");
            System.out.println("==============================================");
            System.out.println("Bienvenido: " + username);

            while (running) {
                printMainMenu();

                try {
                    int option = Integer.parseInt(scan.nextLine().trim());

                    switch (option) {
                        case 1 -> viewAcademicHistory();
                        case 2 -> viewCurrentEnrollments();
                        case 3 -> enrollInCourse(scan);
                        case 4 -> withdrawFromCourse(scan);
                        case 5 -> viewAvailableCourses();
                        case 6 -> viewSchedule();
                        case 7 -> viewGradeAverage();
                        case 8 -> viewApprovedCredits();
                        case 0 -> {
                            System.out.println("\nCerrando sesion...");
                            System.out.println("Hasta pronto!\n");
                            running = false;
                            return false;
                        }
                        default -> System.out.println("Opcion invalida. Intente nuevamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un numero valido.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            return false;
        
    }

    private static void printMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("           MENU ESTUDIANTE");
        System.out.println("==========================================");
        System.out.println("1. Ver Historial Academico");
        System.out.println("2. Ver Inscripciones Actuales");
        System.out.println("3. Inscribir Materia");
        System.out.println("4. Retirar Materia");
        System.out.println("5. Ver Cursos Disponibles");
        System.out.println("6. Ver Horario");
        System.out.println("7. Ver Promedio General");
        System.out.println("8. Ver Creditos Aprobados");
        System.out.println("0. Salir");
        System.out.print("\nSeleccione una opcion: ");
    }

    private static void viewAcademicHistory() {
        System.out.println("\n--- HISTORIAL ACADEMICO ---");
        GradeManager.listStudentGrades(currentUser);
    }

    private static void viewCurrentEnrollments() {
        System.out.println("\n--- INSCRIPCIONES ACTUALES ---");
        EnrollmentManager.listCurrentEnrollments(currentUser);
    }

    private static void enrollInCourse(Scanner scan) {
        System.out.println("\n--- INSCRIBIR MATERIA ---");

        // Mostrar cursos disponibles
        List<Course> availableCourses = EnrollmentManager.getAvailableCoursesForStudent(currentUser);

        if (availableCourses.isEmpty()) {
            System.out.println("No hay cursos disponibles para inscribir.");
            return;
        }

        System.out.println("\nCursos disponibles:");
        for (Course course : availableCourses) {
            System.out.printf("Codigo: %s | %s | %d creditos%n",
                    course.getCode(), course.getName(), course.getCredits());
        }

        System.out.print("\nIngrese el codigo del curso: ");
        String courseCode = scan.nextLine().trim();

        if (courseCode.isEmpty()) {
            System.out.println("Codigo invalido.");
            return;
        }

        // Buscar grupos disponibles para ese curso
        List<String[]> allGroups = GroupManager.loadGroups();
        boolean foundGroups = false;

        System.out.println("\nGrupos disponibles:");
        for (String[] groupData : allGroups) {
            if (groupData[4].equals(courseCode)) {
                foundGroups = true;
                Group group = GroupManager.findGroup(groupData[0]);
                if (group != null) {
                    int spots = GroupManager.getAvailableSpots(groupData[0]);
                    System.out.printf("Grupo %s | Semestre: %s | Cupos: %d | Dias: %s | Horas: %s%n",
                            groupData[0], groupData[3], spots, groupData[1], groupData[2]);
                }
            }
        }

        if (!foundGroups) {
            System.out.println("No hay grupos disponibles para este curso.");
            return;
        }

        System.out.print("\nIngrese el numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        // Intentar inscribir
        EnrollmentManager.enrollStudent(currentUser, groupNumber);
    }

    private static void withdrawFromCourse(Scanner scan) {
        System.out.println("\n--- RETIRAR MATERIA ---");

        List<Group> currentEnrollments = EnrollmentManager.getCurrentEnrollments(currentUser);

        if (currentEnrollments.isEmpty()) {
            System.out.println("No tiene materias inscritas actualmente.");
            return;
        }

        System.out.println("\nMaterias inscritas:");
        for (Group group : currentEnrollments) {
            System.out.printf("Grupo %d | %s | %d creditos%n",
                    group.getNumber(),
                    group.getRepresents().getName(),
                    group.getRepresents().getCredits());
        }

        System.out.print("\nIngrese el numero del grupo a retirar: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            EnrollmentManager.unenrollStudent(currentUser, groupNumber);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void viewAvailableCourses() {
        System.out.println("\n--- CURSOS DISPONIBLES ---");
        EnrollmentManager.listAvailableCourses(currentUser);
    }

    private static void viewSchedule() {
        System.out.println("\n--- MI HORARIO ---");

        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);

        if (enrollments.isEmpty()) {
            System.out.println("No tiene materias inscritas.");
            return;
        }

        System.out.println("\nHorario actual:");
        System.out.println("==========================================");

        for (Group group : enrollments) {
            System.out.printf("\n%s (Grupo %d)%n",
                    group.getRepresents().getName(),
                    group.getNumber());

            String[] days = group.getDaysOfWeek();
            String[] times = group.getTimesOfDay();

            if (days != null && times != null) {
                for (int i = 0; i < days.length && i < times.length; i++) {
                    System.out.printf("  %s: %s%n",
                            getDayName(days[i]),
                            times[i]);
                }
            }
        }
        System.out.println("==========================================");
    }

    private static void viewGradeAverage() {
        System.out.println("\n--- PROMEDIO GENERAL ---");

        double average = GradeManager.calculateStudentAverage(currentUser);

        if (average == 0.0) {
            System.out.println("Aun no tiene calificaciones registradas.");
        } else {
            System.out.printf("Promedio general: %.2f%n", average);

            if (average >= 4.0) {
                System.out.println("Estado: Excelente");
            } else if (average >= 3.5) {
                System.out.println("Estado: Bueno");
            } else if (average >= 3.0) {
                System.out.println("Estado: Aceptable");
            } else {
                System.out.println("Estado: Bajo");
            }
        }
    }

    private static void viewApprovedCredits() {
        System.out.println("\n--- CREDITOS APROBADOS ---");

        int approvedCredits = GradeManager.calculateApprovedCredits(currentUser);
        List<String> passedCourses = GradeManager.getPassedCourses(currentUser);

        System.out.println("Total de creditos aprobados: " + approvedCredits);
        System.out.println("Total de cursos aprobados: " + passedCourses.size());

        if (!passedCourses.isEmpty()) {
            System.out.println("\nCursos aprobados:");
            for (String courseCode : passedCourses) {
                var course = CourseManager.findCourse(Long.parseLong(courseCode));
                if (course != null) {
                    System.out.printf("- %s (%s) - %d creditos%n",
                            course.getName(),
                            course.getCode(),
                            course.getCredits());
                }
            }
        }
    }

    private static String getDayName(String code) {
        switch (code.trim().toUpperCase()) {
            case "L": return "Lunes";
            case "M": return "Martes";
            case "W": return "Miercoles";
            case "J": return "Jueves";
            case "V": return "Viernes";
            case "S": return "Sabado";
            case "D": return "Domingo";
            default: return code;
        }
    }
}