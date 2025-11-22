/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sia.sia.business.CourseManager;

/**
 * Menu para gestion de cursos
 * @author luzel
 */
public class CourseManagementMenu {

    public static void show(Scanner scan) {
        boolean running = true;

        while (running) {
            System.out.println("\n=====================================");
            System.out.println("|        GESTION DE CURSOS              |");
            System.out.println("=====================================");
            System.out.println("1. Crear Curso");
            System.out.println("2. Actualizar Curso");
            System.out.println("3. Buscar Curso");
            System.out.println("4. Listar Cursos");
            System.out.println("5. Eliminar Curso");
            System.out.println("6. Agregar Requisitos");
            System.out.println("7. Ver Requisitos");
            System.out.println("0. Volver");
            System.out.print("\n Seleccione una opcion: ");

            try {
                int option = Integer.parseInt(scan.nextLine().trim());

                switch (option) {
                    case 1 -> createCourse(scan);
                    case 2 -> updateCourse(scan);
                    case 3 -> findCourse(scan);
                    case 4 -> listCourses();
                    case 5 -> deleteCourse(scan);
                    case 6 -> addRequisites(scan);
                    case 7 -> viewRequisites(scan);
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

    private static void createCourse(Scanner scan) {
        System.out.println("\n--- CREAR CURSO ---");

        System.out.print("Nombre del curso: ");
        String name = scan.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("El nombre no puede estar vacio.");
            return;
        }

        System.out.print("Creditos: ");
        int credits;
        try {
            credits = Integer.parseInt(scan.nextLine().trim());
            if (credits <= 0) {
                System.out.println("Los creditos deben ser mayores a 0.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Creditos invalidos.");
            return;
        }

        List<String> requisites = collectRequisites(scan);

        CourseManager.createCourse(name, credits, requisites);
    }

    private static void updateCourse(Scanner scan) {
        System.out.println("\n--- ACTUALIZAR CURSO ---");

        System.out.print("Codigo del curso: ");
        long code;
        try {
            code = Long.parseLong(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
            return;
        }

        System.out.print("Nuevo nombre: ");
        String newName = scan.nextLine().trim();

        System.out.print("Nuevos creditos: ");
        int newCredits;
        try {
            newCredits = Integer.parseInt(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Creditos invalidos.");
            return;
        }

        List<String> newRequisites = collectRequisites(scan);

        CourseManager.updateCourse(code, newName, newCredits, newRequisites);
    }

    private static void findCourse(Scanner scan) {
        System.out.println("\n--- BUSCAR CURSO ---");
        System.out.println("1. Buscar por nombre");
        System.out.println("2. Buscar por codigo");
        System.out.print("Seleccione: ");

        try {
            int searchOption = Integer.parseInt(scan.nextLine().trim());

            switch (searchOption) {
                case 1 -> {
                    System.out.print("Nombre del curso: ");
                    String name = scan.nextLine().trim();
                    var course = CourseManager.findCourse(name);
                    System.out.println(course == null ? "Curso no encontrado." : course);
                }
                case 2 -> {
                    System.out.print("Codigo del curso: ");
                    long code = Long.parseLong(scan.nextLine().trim());
                    var course = CourseManager.findCourse(code);
                    System.out.println(course == null ? "Curso no encontrado." : course);
                }
                default -> System.out.println("Opcion invalida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.");
        }
    }

    private static void listCourses() {
        System.out.println("\n--- LISTA DE CURSOS ---");
        CourseManager.listCourses();
    }

    private static void deleteCourse(Scanner scan) {
        System.out.println("\n--- ELIMINAR CURSO ---");

        System.out.print("Codigo del curso a eliminar: ");
        long code;
        try {
            code = Long.parseLong(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            CourseManager.deleteCourse(code);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void addRequisites(Scanner scan) {
        System.out.println("\n--- AGREGAR REQUISITOS ---");

        System.out.print("Codigo del curso: ");
        long code;
        try {
            code = Long.parseLong(scan.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
            return;
        }

        List<String> newRequisites = collectRequisites(scan);

        if (!newRequisites.isEmpty()) {
            CourseManager.addRequisite(code, newRequisites);
        }
    }

    private static void viewRequisites(Scanner scan) {
        System.out.println("\n--- VER REQUISITOS ---");

        System.out.print("Codigo del curso: ");
        String code = scan.nextLine().trim();

        if (code.isEmpty()) {
            System.out.println("El codigo no puede estar vacio.");
            return;
        }

        CourseManager.listRequisites(code);
    }

    /**
     * Metodo auxiliar para recolectar requisitos
     */
    private static List<String> collectRequisites(Scanner scan) {
        List<String> requisites = new ArrayList<>();

        System.out.println("\nIngrese codigos de requisitos (separados por ; o 'fin' para terminar)");
        System.out.println("Ejemplo: 10001;10002 o escriba cada uno en lineas separadas");

        while (true) {
            System.out.print("Requisito: ");
            String input = scan.nextLine().trim();

            if (input.equalsIgnoreCase("fin") || input.isEmpty()) {
                break;
            }

            String[] parts = input.split(";");
            for (String part : parts) {
                String requisite = part.trim();
                if (!requisite.isEmpty()) {
                    if (requisites.contains(requisite)) {
                        System.out.println("El requisito '" + requisite + "' ya fue agregado.");
                    } else {
                        requisites.add(requisite);
                        System.out.println("Requisito agregado: " + requisite);
                    }
                }
            }

            System.out.println("Requisitos actuales: " + requisites);
            System.out.println("(Escriba 'fin' o deje vacio para terminar)");
        }

        return requisites;
    }
}
