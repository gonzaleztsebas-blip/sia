package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.StudentManager;

/**
 * Menu para gestion de estudiantes
 * @author luzel
 */
public class StudentManagementMenu {

    public static void show(Scanner scan) {
        boolean running = true;

        while (running) {
            System.out.println("\n----------------------------------------");
            System.out.println("        GESTION DE ESTUDIANTES          ");
            System.out.println("----------------------------------------");
            System.out.println("1. Crear Estudiante");
            System.out.println("2. Actualizar Estudiante");
            System.out.println("3. Buscar Estudiante");
            System.out.println("4. Listar Estudiantes");
            System.out.println("5. Eliminar Estudiante");
            System.out.println("0. Volver");
            System.out.print("\nSeleccione una opcion: ");

            try {
                int option = Integer.parseInt(scan.nextLine().trim());

                switch (option) {
                    case 1 -> createStudent(scan);
                    case 2 -> updateStudent(scan);
                    case 3 -> findStudent(scan);
                    case 4 -> listStudents();
                    case 5 -> deleteStudent(scan);
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

    private static void createStudent(Scanner scan) {
        System.out.println("\n--- CREAR ESTUDIANTE ---");

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

        StudentManager.createStudent(username, password, firstName, lastName, birthDate);
    }

    private static void updateStudent(Scanner scan) {
        System.out.println("\n--- ACTUALIZAR ESTUDIANTE ---");

        System.out.print("Usuario del estudiante: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        System.out.print("Nuevo nombre: ");
        String newFirstName = scan.nextLine().trim();

        System.out.print("Nuevo apellido: ");
        String newLastName = scan.nextLine().trim();

        StudentManager.updateStudent(username, newFirstName, newLastName, "");
    }

    private static void findStudent(Scanner scan) {
        System.out.println("\n--- BUSCAR ESTUDIANTE ---");

        System.out.print("Usuario: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        StudentManager.printFindStudent(username);
    }

    private static void listStudents() {
        System.out.println("\n--- LISTA DE ESTUDIANTES ---");
        StudentManager.listStudents();
    }

    private static void deleteStudent(Scanner scan) {
        System.out.println("\n--- ELIMINAR ESTUDIANTE ---");

        System.out.print("Usuario del estudiante a eliminar: ");
        String username = scan.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("El usuario no puede estar vacio.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            StudentManager.deleteStudent(username);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }
}
