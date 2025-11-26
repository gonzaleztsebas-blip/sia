package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.StudentManager;

public class StudentManagementMenu {

    public static void show(Scanner scan) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- GESTION DE ESTUDIANTES ---");
            System.out.println("1. Crear Estudiante");
            System.out.println("2. Actualizar Estudiante");
            System.out.println("3. Buscar Estudiante");
            System.out.println("4. Listar Estudiantes");
            System.out.println("5. Eliminar Estudiante");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    createStudent(scan);
                case "2" ->
                    updateStudent(scan);
                case "3" ->
                    findStudent(scan);
                case "4" ->
                    StudentManager.listStudents();
                case "5" ->
                    deleteStudent(scan);
                case "0" ->
                    running = false;
                default ->
                    System.out.println("Opcion invalida");
            }
        }
    }

    private static void createStudent(Scanner scan) {
        System.out.print("Usuario: ");
        String u = scan.nextLine().trim();
        System.out.print("Contrasena: ");
        String p = scan.nextLine().trim();
        System.out.print("Nombre: ");
        String fn = scan.nextLine().trim();
        System.out.print("Apellido: ");
        String ln = scan.nextLine().trim();
        System.out.print("Fecha Nac (YYYY-MM-DD): ");
        String b = scan.nextLine().trim();
        StudentManager.createStudent(u, p, fn, ln, b);
    }

    private static void updateStudent(Scanner scan) {
        System.out.print("Nuevo Usuario: ");
        String u = scan.nextLine().trim();
        System.out.print("Nueva Contrasena: ");
        String p = scan.nextLine().trim();
        System.out.print("Nuevo nombre: ");
        String fn = scan.nextLine().trim();
        System.out.print("Nuevo apellido: ");
        String ln = scan.nextLine().trim();
        System.out.print("Nueva Fecha Nac (YYYY-MM-DD): ");
        String b = scan.nextLine().trim();
        StudentManager.updateStudent(u, p, fn, ln, b);
    }

    private static void findStudent(Scanner scan) {
        System.out.print("Usuario: ");
        String u = scan.nextLine().trim();
        StudentManager.printFindStudent(u);
    }

    private static void deleteStudent(Scanner scan) {
        System.out.print("Usuario a eliminar: ");
        String u = scan.nextLine().trim();
        System.out.print("Confirma (S/N): ");
        if (scan.nextLine().trim().equalsIgnoreCase("S")) {
            StudentManager.deleteStudent(u);
        }
    }
}
