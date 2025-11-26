package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.GradeManager;
import sia.sia.business.GroupManager;

public class ProfessorMenu {

    private static String currentUser;

    public static boolean show(String username) {
        currentUser = username;
        Scanner scan = new Scanner(System.in);
        boolean run = true;
        while (run) {
            System.out.println("\n--- MENU PROFESOR ---");
            System.out.println("1. Ver Grupos a cargo");
            System.out.println("2. Ingresar calificacion");
            System.out.println("3. Listar calificaciones de un grupo");
            System.out.println("0. Cerrar sesion");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    GroupManager.listGroups();
                case "2" -> {
                    System.out.print("Estudiante: ");
                    String u = scan.nextLine().trim();
                    System.out.print("Grupo: ");
                    String g = scan.nextLine().trim();
                    System.out.print("Nota: ");
                    try {
                        double n = Double.parseDouble(scan.nextLine().trim());
                        GradeManager.createGrade(u, g, n);
                    } catch (Exception e) {
                        System.out.println("Nota invalida");
                    }
                }
                case "3" -> {
                    System.out.print("Grupo: ");
                    GradeManager.listGroupGrades(scan.nextLine().trim());
                }
                case "0" -> {
                    run = false;
                    return false;
                }
                default ->
                    System.out.println("Opcion invalida");
            }
        }
        return false;
    }
}
