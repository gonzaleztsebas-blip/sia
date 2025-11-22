package sia.sia.ui;

import java.util.Scanner;

/**
 * Menú principal del administrador
 * Delega funcionalidades a submenús especializados
 * @author luzel
 */
public class AdminMenu {

    public static boolean show() {
        Scanner scan = new Scanner(System.in);
            boolean running = true;

            System.out.println("\n==============================================");
            System.out.println("|                                               |");
            System.out.println("|      SISTEMA DE INFORMACION ACADEMICO       |");
            System.out.println("|           Universidad Nacional                |");
            System.out.println("|                                               |");
            System.out.println("==============================================");

            while (running) {
                printMainMenu();

                try {
                    int option = Integer.parseInt(scan.nextLine().trim());

                    switch (option) {
                        case 1 -> StudentManagementMenu.show(scan);
                        case 2 -> ProfessorManagementMenu.show(scan);
                        case 3 -> CourseManagementMenu.show(scan);
                        case 4 -> GroupManagementMenu.show(scan);
                        case 5 -> EnrollmentMenu.show(scan);
                        case 6 -> GradeMenu.show(scan);
                        case 0 -> {
                            System.out.println("\n==========================================");
                            System.out.println("|      Gracias por usar el sistema SIA          |");
                            System.out.println("|         ¡Hasta pronto!                        |");
                            System.out.println("==========================================\n");
                            running = false;
                            return false; // Cerrar sesión
                        }
                        default -> System.out.println("Opcion inválida. Intente nuevamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un numero valido.");
                }
            }
        
        return false; // Cerrar sesión cuando sale del bucle
    }

    private static void printMainMenu() {
        System.out.println("\n=====================================");
        System.out.println("|         MENÚ ADMINISTRADOR            |");
        System.out.println("=====================================");
        System.out.println("1. Gestionar Estudiantes");
        System.out.println("2. Gestionar Profesores");
        System.out.println("3. Gestionar Cursos");
        System.out.println("4. Gestionar Grupos");
        System.out.println("5. Gestionar Inscripciones");
        System.out.println("6. Gestionar Calificaciones");
        System.out.println("0. Salir");
        System.out.print("\n Seleccione una opcion: ");
    }
}