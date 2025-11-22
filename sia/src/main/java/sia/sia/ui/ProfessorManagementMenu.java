/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.ProfessorManager;

/**
 * Menu para gestion de profesores
 * author luzel
 */
public class ProfessorManagementMenu {

    public static void show(Scanner scan) {
        boolean running = true;

        while (running) {
            System.out.println("\n----------------------------------------");
            System.out.println("        GESTION DE PROFESORES          ");
            System.out.println("----------------------------------------");
            System.out.println("1. Crear Profesor");
            System.out.println("2. Actualizar Profesor");
            System.out.println("3. Buscar Profesor");
            System.out.println("4. Listar Profesores");
            System.out.println("5. Eliminar Profesor");
            System.out.println("0. Volver");
            System.out.print("\nSeleccione una opcion: ");

            try {
                int option = Integer.parseInt(scan.nextLine().trim());

                switch (option) {
                    case 1 -> createProfessor(scan);
                    case 2 -> updateProfessor(scan);
                    case 3 -> findProfessor(scan);
                    case 4 -> listProfessors();
                    case 5 -> deleteProfessor(scan);
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

    private static void createProfessor(Scanner scan) {
        System.out.println("\n--- CREAR PROFESOR ---");

        System.out.print("Usuario: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        System.out.print("Contrasena: ");
        String password = scan.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("La contrasena no puede estar vacia.");
            return;
        }

        System.out.print("Nombre: ");
        String firstName = scan.nextLine().trim();

        System.out.print("Apellido: ");
        String lastName = scan.nextLine().trim();

        System.out.print("Fecha de nacimiento (YYYY-MM-DD): ");
        String birthDate = scan.nextLine().trim();

        ProfessorManager.createProfessor(username, password, firstName, lastName, birthDate);
    }

    private static void updateProfessor(Scanner scan) {
        System.out.println("\n--- ACTUALIZAR PROFESOR ---");

        System.out.print("Usuario del profesor: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        System.out.print("Nuevo nombre: ");
        String newFirstName = scan.nextLine().trim();

        System.out.print("Nuevo apellido: ");
        String newLastName = scan.nextLine().trim();

        ProfessorManager.updateProfessor(username, newFirstName, newLastName);
    }

    private static void findProfessor(Scanner scan) {
        System.out.println("\n--- BUSCAR PROFESOR ---");

        System.out.print("Usuario: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        ProfessorManager.printFindProfessor(username);
    }

    private static void listProfessors() {
        System.out.println("\n--- LISTA DE PROFESORES ---");
        ProfessorManager.listProfessors();
    }

    private static void deleteProfessor(Scanner scan) {
        System.out.println("\n--- ELIMINAR PROFESOR ---");

        System.out.print("Usuario del profesor a eliminar: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            ProfessorManager.deleteProfessor(username);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }
}
