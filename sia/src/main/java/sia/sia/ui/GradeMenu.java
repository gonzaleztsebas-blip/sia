/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.GradeManager;

/**
 * Menu para gestion de calificaciones
 * @author luzel
 */
public class GradeMenu {

    public static void show(Scanner scan) {
        boolean running = true;

        while (running) {
            System.out.println("\n=====================================");
            System.out.println("|      GESTION DE CALIFICACIONES    |");
            System.out.println("=====================================");
            System.out.println("1. Registrar Calificacion");
            System.out.println("2. Actualizar Calificacion");
            System.out.println("3. Eliminar Calificacion");
            System.out.println("4. Ver Historial de Estudiante");
            System.out.println("5. Ver Calificaciones de Grupo");
            System.out.println("6. Ver Promedio de Estudiante");
            System.out.println("7. Ver Promedio de Grupo");
            System.out.println("8. Ver Cursos Aprobados");
            System.out.println("9. Listar Todas las Calificaciones");
            System.out.println("0. Volver");
            System.out.print("\nSeleccione una opcion: ");

            try {
                int option = Integer.parseInt(scan.nextLine().trim());

                switch (option) {
                    case 1 -> createGrade(scan);
                    case 2 -> updateGrade(scan);
                    case 3 -> deleteGrade(scan);
                    case 4 -> viewStudentGrades(scan);
                    case 5 -> viewGroupGrades(scan);
                    case 6 -> viewStudentAverage(scan);
                    case 7 -> viewGroupAverage(scan);
                    case 8 -> viewPassedCourses(scan);
                    case 9 -> listAllGrades();
                    case 0 -> {
                        System.out.println("Volviendo al menu principal...");
                        running = false;
                    }
                    default -> System.out.println("Opcion invalida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un numero valido.");
            }
        }
    }

    private static void createGrade(Scanner scan) {
        System.out.println("\n--- REGISTRAR CALIFICACION ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
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

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
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

    private static void deleteGrade(Scanner scan) {
        System.out.println("\n--- ELIMINAR CALIFICACION ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            GradeManager.deleteGrade(studentUser, groupNumber);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void viewStudentGrades(Scanner scan) {
        System.out.println("\n--- HISTORIAL ACADEMICO ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        GradeManager.listStudentGrades(studentUser);
    }

    private static void viewGroupGrades(Scanner scan) {
        System.out.println("\n--- CALIFICACIONES DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        GradeManager.listGroupGrades(groupNumber);
    }

    private static void viewStudentAverage(Scanner scan) {
        System.out.println("\n--- PROMEDIO DEL ESTUDIANTE ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        double average = GradeManager.calculateStudentAverage(studentUser);
        System.out.printf("\nPromedio general: %.2f%n", average);
    }

    private static void viewGroupAverage(Scanner scan) {
        System.out.println("\n--- PROMEDIO DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        double average = GradeManager.calculateGroupAverage(groupNumber);
        System.out.printf("\nPromedio del grupo: %.2f%n", average);
    }

    private static void viewPassedCourses(Scanner scan) {
        System.out.println("\n--- CURSOS APROBADOS ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        var passedCourses = GradeManager.getPassedCourses(studentUser);

        if (passedCourses.isEmpty()) {
            System.out.println("El estudiante no ha aprobado ningun curso.");
            return;
        }

        System.out.println("\nCursos aprobados:");
        for (String courseCode : passedCourses) {
            System.out.println("- Codigo: " + courseCode);
        }

        int totalCredits = GradeManager.calculateApprovedCredits(studentUser);
        System.out.printf("\nTotal de creditos aprobados: %d%n", totalCredits);
    }

    private static void listAllGrades() {
        System.out.println("\n--- TODAS LAS CALIFICACIONES ---");
        GradeManager.listAllGrades();
    }
}
