/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.EnrollmentManager;

/**
 * Menu para gestion de inscripciones
 * @author luzel
 */
public class EnrollmentMenu {

    public static void show(Scanner scan) {
        boolean running = true;

        while (running) {
            System.out.println("\n=====================================");
            System.out.println("|      GESTION DE INSCRIPCIONES     |");
            System.out.println("=====================================");
            System.out.println("1. Inscribir Estudiante");
            System.out.println("2. Retirar Estudiante");
            System.out.println("3. Ver Inscripciones Actuales");
            System.out.println("4. Ver Inscripciones por Semestre");
            System.out.println("5. Ver Cursos Disponibles");
            System.out.println("6. Listar Todas las Inscripciones");
            System.out.println("0. Volver");
            System.out.print("\nSeleccione una opcion: ");

            try {
                int option = Integer.parseInt(scan.nextLine().trim());

                switch (option) {
                    case 1 -> enrollStudent(scan);
                    case 2 -> unenrollStudent(scan);
                    case 3 -> viewCurrentEnrollments(scan);
                    case 4 -> viewEnrollmentsBySemester(scan);
                    case 5 -> viewAvailableCourses(scan);
                    case 6 -> listAllEnrollments();
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

    private static void enrollStudent(Scanner scan) {
        System.out.println("\n--- INSCRIBIR ESTUDIANTE ---");

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

        EnrollmentManager.enrollStudent(studentUser, groupNumber);
    }

    private static void unenrollStudent(Scanner scan) {
        System.out.println("\n--- RETIRAR ESTUDIANTE ---");

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

        System.out.print("Esta seguro de retirar al estudiante? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            EnrollmentManager.unenrollStudent(studentUser, groupNumber);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void viewCurrentEnrollments(Scanner scan) {
        System.out.println("\n--- INSCRIPCIONES ACTUALES ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        EnrollmentManager.listCurrentEnrollments(studentUser);
    }

    private static void viewEnrollmentsBySemester(Scanner scan) {
        System.out.println("\n--- INSCRIPCIONES POR SEMESTRE ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        System.out.print("Semestre (Ejemplo: 2025-1): ");
        String semester = scan.nextLine().trim();

        if (semester.isEmpty()) {
            System.out.println("El semestre no puede estar vacio.");
            return;
        }

        var enrollments = EnrollmentManager.getEnrollmentsBySemester(studentUser, semester);

        if (enrollments.isEmpty()) {
            System.out.println("No hay inscripciones para ese semestre.");
            return;
        }

        System.out.println("\nInscripciones en " + semester + ":");
        for (var group : enrollments) {
            System.out.printf("- Grupo %d: %s (%d creditos)%n",
                    group.getNumber(),
                    group.getRepresents().getName(),
                    group.getRepresents().getCredits());
        }
    }

    private static void viewAvailableCourses(Scanner scan) {
        System.out.println("\n--- CURSOS DISPONIBLES ---");

        System.out.print("Username del estudiante: ");
        String studentUser = scan.nextLine().trim();

        if (studentUser.isEmpty()) {
            System.out.println("El username no puede estar vacio.");
            return;
        }

        EnrollmentManager.listAvailableCourses(studentUser);
    }

    private static void listAllEnrollments() {
        System.out.println("\n--- TODAS LAS INSCRIPCIONES ---");
        EnrollmentManager.listAllEnrollments();
    }
}
