package sia.sia.ui;

import java.util.Scanner;

public class AdminMenu {

    private static String currentUser;

    public static boolean show(String currentUser) {
        Scanner scan = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU ADMINISTRADOR =====");
            System.out.println("1. Gestionar Cursos");
            System.out.println("2. Gestionar Grupos");
            System.out.println("3. Gestionar Estudiantes");
            System.out.println("4. Gestionar Profesores");
            System.out.println("5. Gestionar Inscripciones");
            System.out.println("6. Gestionar Calificaciones");
            System.out.println("0. Salir");
            System.out.print("Seleccione: ");

            String line = scan.nextLine().trim();
            switch (line) {
                case "1" ->
                    CourseManagementMenu.show(scan);
                case "2" ->
                    GroupManagementMenu.show(scan);
                case "3" ->
                    StudentManagementMenu.show(scan);
                case "4" ->
                    ProfessorManagementMenu.show(scan);
                case "5" ->
                    EnrollmentMenu.show(scan);
                case "6" ->
                    GradeMenu.show(scan);
                case "0" -> {
                    System.out.println("Saliendo de admin...");
                    running = false;
                }
                default ->
                    System.out.println("Opcion invalida.");
            }
        }

        return false;

    }
}
