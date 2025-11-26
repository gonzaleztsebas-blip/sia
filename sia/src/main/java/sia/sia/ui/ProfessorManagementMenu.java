package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.ProfessorManager;

public class ProfessorManagementMenu {

    public static void show(Scanner scan) {
        boolean run = true;
        while (run) {
            System.out.println("\n--- GESTION DE PROFESORES ---");
            System.out.println("1. Crear Profesor");
            System.out.println("2. Actualizar Profesor");
            System.out.println("3. Eliminar Profesor");
            System.out.println("4. Listar Profesores");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    createProfessor(scan);
                case "2" ->
                    updateProfessor(scan);
                case "3" ->
                    deleteProfessor(scan);
                case "4" ->
                    ProfessorManager.listProfessors();
                case "0" ->
                    run = false;
                default ->
                    System.out.println("Opcion invalida");
            }
        }
    }

    private static void createProfessor(Scanner scan) {
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
        ProfessorManager.createProfessor(u, p, fn, ln, b);
    }

    private static void updateProfessor(Scanner scan) {
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
        ProfessorManager.updateProfessor(u, p, fn, ln, b);
    }

    private static void deleteProfessor(Scanner scan) {
        System.out.print("Usuario a eliminar: ");
        String u = scan.nextLine().trim();
        System.out.print("Confirma (S/N): ");
        if (scan.nextLine().trim().equalsIgnoreCase("S")) {
            ProfessorManager.deleteProfessor(u);
        }
    }
}
