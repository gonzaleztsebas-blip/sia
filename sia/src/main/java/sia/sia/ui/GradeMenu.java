package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.GradeManager;

public class GradeMenu {

    public static void show(Scanner scan) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- GESTION DE CALIFICACIONES ---");
            System.out.println("1. Crear calificacion");
            System.out.println("2. Actualizar calificacion");
            System.out.println("3. Eliminar calificacion");
            System.out.println("4. Listar calificaciones de estudiante");
            System.out.println("5. Listar calificaciones de grupo");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    createGrade(scan);
                case "2" ->
                    updateGrade(scan);
                case "3" ->
                    deleteGrade(scan);
                case "4" -> {
                    System.out.print("Usuario: ");
                    GradeManager.listStudentGrades(scan.nextLine().trim());
                }
                case "5" -> {
                    System.out.print("Grupo: ");
                    GradeManager.listGroupGrades(scan.nextLine().trim());
                }
                case "0" ->
                    running = false;
                default ->
                    System.out.println("Opcion invalida");
            }
        }
    }

    private static void createGrade(Scanner scan) {
        System.out.print("Usuario estudiante: ");
        String user = scan.nextLine().trim();
        System.out.print("Numero grupo: ");
        String group = scan.nextLine().trim();
        System.out.print("Nota (0.0-5.0): ");
        try {
            double g = Double.parseDouble(scan.nextLine().trim());
            GradeManager.createGrade(user, group, g);
        } catch (Exception e) {
            System.out.println("Nota invalida");
        }
    }

    private static void updateGrade(Scanner scan) {
        System.out.print("Usuario: ");
        String u = scan.nextLine().trim();
        System.out.print("Grupo: ");
        String gr = scan.nextLine().trim();
        System.out.print("Nueva nota: ");
        try {
            double n = Double.parseDouble(scan.nextLine().trim());
            GradeManager.updateGrade(u, gr, n);
        } catch (Exception e) {
            System.out.println("Nota invalida");
        }
    }

    private static void deleteGrade(Scanner scan) {
        System.out.print("Usuario: ");
        String u = scan.nextLine().trim();
        System.out.print("Grupo: ");
        String gr = scan.nextLine().trim();
        GradeManager.deleteGrade(u, gr);
    }
}
