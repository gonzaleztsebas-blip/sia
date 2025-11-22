/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.List;
import java.util.Scanner;
import sia.sia.business.*;
import sia.sia.data.Grade;
import sia.sia.data.Group;
import sia.sia.data.Student;

/**
 * Menu principal para profesores
 * Permite ver grupos asignados, registrar notas y gestionar estudiantes
 * @author luzel
 */
public class ProfessorMenu {

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
                        case 1 -> viewMyGroups();
                        case 2 -> viewGroupDetails(scan);
                        case 3 -> viewGroupStudents(scan);
                        case 4 -> registerGrade(scan);
                        case 5 -> updateGrade(scan);
                        case 6 -> viewGroupGrades(scan);
                        case 7 -> viewGroupAverage(scan);
                        case 8 -> viewGroupSchedule(scan);
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
        System.out.println("           MENU PROFESOR");
        System.out.println("==========================================");
        System.out.println("1. Ver Mis Grupos");
        System.out.println("2. Ver Detalles de Grupo");
        System.out.println("3. Ver Estudiantes de Grupo");
        System.out.println("4. Registrar Calificacion");
        System.out.println("5. Actualizar Calificacion");
        System.out.println("6. Ver Calificaciones de Grupo");
        System.out.println("7. Ver Promedio de Grupo");
        System.out.println("8. Ver Horario de Grupo");
        System.out.println("0. Salir");
        System.out.print("\nSeleccione una opcion: ");
    }

    private static void viewMyGroups() {
        System.out.println("\n--- MIS GRUPOS ASIGNADOS ---");

        List<String[]> allGroups = GroupManager.loadGroups();
        boolean hasGroups = false;

        System.out.println("\nGrupos donde soy profesor:");
        System.out.println("==========================================");

        for (String[] groupData : allGroups) {
            if (groupData.length >= 6 && groupData[5].equals(currentUser)) {
                hasGroups = true;
                Group group = GroupManager.findGroup(groupData[0]);
                if (group != null) {
                    int studentCount = group.getAttendedBy() != null ? group.getAttendedBy().size() : 0;
                    System.out.printf("Grupo %s | %s | Semestre: %s | Estudiantes: %d%n",
                            groupData[0],
                            group.getRepresents().getName(),
                            groupData[3],
                            studentCount);
                }
            }
        }

        if (!hasGroups) {
            System.out.println("No tiene grupos asignados actualmente.");
        }
        System.out.println("==========================================");
    }

    private static void viewGroupDetails(Scanner scan) {
        System.out.println("\n--- DETALLES DE GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para ver este grupo.");
            return;
        }

        System.out.println("\n==========================================");
        System.out.println("INFORMACION DEL GRUPO");
        System.out.println("==========================================");
        System.out.println("Numero: " + group.getNumber());
        System.out.println("Curso: " + group.getRepresents().getName());
        System.out.println("Codigo: " + group.getRepresents().getCode());
        System.out.println("Creditos: " + group.getRepresents().getCredits());
        System.out.println("Semestre: " + group.getSemester());

        String[] days = group.getDaysOfWeek();
        String[] times = group.getTimesOfDay();

        if (days != null && times != null) {
            System.out.println("\nHorario:");
            for (int i = 0; i < days.length && i < times.length; i++) {
                System.out.printf("  %s: %s%n", getDayName(days[i]), times[i]);
            }
        }

        int studentCount = group.getAttendedBy() != null ? group.getAttendedBy().size() : 0;
        System.out.println("\nEstudiantes inscritos: " + studentCount);
        System.out.println("==========================================");
    }

    private static void viewGroupStudents(Scanner scan) {
        System.out.println("\n--- ESTUDIANTES DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para ver este grupo.");
            return;
        }

        List<Student> students = group.getAttendedBy();

        if (students == null || students.isEmpty()) {
            System.out.println("No hay estudiantes inscritos en este grupo.");
            return;
        }

        System.out.println("\nEstudiantes inscritos:");
        System.out.println("==========================================");

        for (Student student : students) {
            System.out.printf("ID: %s | Usuario: %s | Nombre: %s %s%n",
                    student.getId(),
                    student.getUser(),
                    student.getFirstName(),
                    student.getLastName());

            // Mostrar calificacion si existe
            Grade grade = GradeManager.findGrade(student.getUser(), groupNumber);
            if (grade != null) {
                System.out.printf("  Calificacion: %.2f%n", grade.getGrade());
            } else {
                System.out.println("  Calificacion: Pendiente");
            }
        }
        System.out.println("==========================================");
    }

    private static void registerGrade(Scanner scan) {
        System.out.println("\n--- REGISTRAR CALIFICACION ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para modificar este grupo.");
            return;
        }

        System.out.print("Usuario del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("Usuario invalido.");
            return;
        }

        System.out.print("Calificacion (0.0 - 5.0): ");
        double grade;
        try {
            grade = Double.parseDouble(scan.nextLine().trim());
            if (grade < 0.0 || grade > 5.0) {
                System.out.println("La calificacion debe estar entre 0.0 y 5.0");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Calificacion invalida.");
            return;
        }

        GradeManager.createGrade(studentUser, groupNumber, grade);
    }

    private static void updateGrade(Scanner scan) {
        System.out.println("\n--- ACTUALIZAR CALIFICACION ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para modificar este grupo.");
            return;
        }

        System.out.print("Usuario del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("Usuario invalido.");
            return;
        }

        // Mostrar calificacion actual
        Grade currentGrade = GradeManager.findGrade(studentUser, groupNumber);
        if (currentGrade != null) {
            System.out.printf("Calificacion actual: %.2f%n", currentGrade.getGrade());
        }

        System.out.print("Nueva calificacion (0.0 - 5.0): ");
        double newGrade;
        try {
            newGrade = Double.parseDouble(scan.nextLine().trim());
            if (newGrade < 0.0 || newGrade > 5.0) {
                System.out.println("La calificacion debe estar entre 0.0 y 5.0");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Calificacion invalida.");
            return;
        }

        GradeManager.updateGrade(studentUser, groupNumber, newGrade);
    }

    private static void viewGroupGrades(Scanner scan) {
        System.out.println("\n--- CALIFICACIONES DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para ver este grupo.");
            return;
        }

        GradeManager.listGroupGrades(groupNumber);
    }

    private static void viewGroupAverage(Scanner scan) {
        System.out.println("\n--- PROMEDIO DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para ver este grupo.");
            return;
        }

        double average = GradeManager.calculateGroupAverage(groupNumber);
        System.out.printf("\nPromedio del grupo: %.2f%n", average);
    }

    private static void viewGroupSchedule(Scanner scan) {
        System.out.println("\n--- HORARIO DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("Numero de grupo invalido.");
            return;
        }

        Group group = GroupManager.findGroup(groupNumber);

        if (group == null) {
            System.out.println("Grupo no encontrado.");
            return;
        }

        if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
            System.out.println("No tiene permiso para ver este grupo.");
            return;
        }

        System.out.println("\n==========================================");
        System.out.println("Curso: " + group.getRepresents().getName());
        System.out.println("Grupo: " + group.getNumber());
        System.out.println("Semestre: " + group.getSemester());
        System.out.println("\nHorario:");

        String[] days = group.getDaysOfWeek();
        String[] times = group.getTimesOfDay();

        if (days != null && times != null) {
            for (int i = 0; i < days.length && i < times.length; i++) {
                System.out.printf("  %s: %s%n", getDayName(days[i]), times[i]);
            }
        } else {
            System.out.println("  Sin horario definido");
        }
        System.out.println("==========================================");
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