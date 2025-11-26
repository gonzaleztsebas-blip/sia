package sia.sia.ui;

import java.util.*;
import sia.sia.business.CourseManager;
import sia.sia.data.Course;

public class CourseManagementMenu {

    public static void show(Scanner scan) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- GESTION DE CURSOS ---");
            System.out.println("1. Crear Curso");
            System.out.println("2. Actualizar Curso");
            System.out.println("3. Buscar Curso");
            System.out.println("4. Listar Cursos");
            System.out.println("5. Eliminar Curso");
            System.out.println("6. Agregar Requisitos");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    createCourse(scan);
                case "2" ->
                    updateCourse(scan);
                case "3" ->
                    findCourse(scan);
                case "4" ->
                    CourseManager.listCourses();
                case "5" ->
                    deleteCourse(scan);
                case "6" ->
                    addRequisites(scan);
                case "0" ->
                    running = false;
                default ->
                    System.out.println("Opcion invalida.");
            }
        }
    }

    private static void createCourse(Scanner scan) {
        System.out.print("Nombre: ");
        String name = scan.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Nombre vacio");
            return;
        }
        int[] credits = collectCredits(scan);
        if (credits == null) {
            return;
        }
        List<String> reqs = collectRequisites(scan);
        CourseManager.createCourse(name, credits, reqs);
    }

    private static void updateCourse(Scanner scan) {
        System.out.print("Codigo: ");
        try {
            long code = Long.parseLong(scan.nextLine().trim());
            System.out.print("Nuevo nombre: ");
            String name = scan.nextLine().trim();
            int[] credits = collectCredits(scan);
            if (credits == null) {
                return;
            }
            List<String> reqs = collectRequisites(scan);
            CourseManager.updateCourse(code, name, credits, reqs);
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
        }
    }

    private static int[] collectCredits(Scanner scan) {
        int[] credits = new int[4];
        String[] labels = {"Fundamentacion", "Disciplinar", "Libre eleccion", "Nivelacion"};
        for (int i = 0; i < 4; i++) {
            System.out.print(labels[i] + ": ");
            try {
                credits[i] = Integer.parseInt(scan.nextLine().trim());
                if (credits[i] < 0) {
                    System.out.println("No puede ser negativo");
                    return null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Numero invalido");
                return null;
            }
        }
        return credits;
    }

    private static List<String> collectRequisites(Scanner scan) {
        List<String> requisites = new ArrayList<>();
        System.out.println("Agregar requisitos (dejar vacio para terminar):");
        while (true) {
            System.out.print("Codigo curso requisito: ");
            String req = scan.nextLine().trim();
            if (req.isEmpty()) {
                break;
            }
            requisites.add(req);
        }
        return requisites;
    }

    private static void findCourse(Scanner scan) {
        System.out.print("Codigo del curso: ");
        try {
            long code = Long.parseLong(scan.nextLine().trim());
            Course course = CourseManager.findCourse(code);
            if (course != null) {
                System.out.println("\n=== INFORMACION DEL CURSO ===");
                System.out.println("Codigo: " + course.getCode());
                System.out.println("Nombre: " + course.getName());
                int[] credits = course.getCredits();
                System.out.println("Creditos - Fundamentacion: " + credits[0]);
                System.out.println("Creditos - Disciplinar: " + credits[1]);
                System.out.println("Creditos - Libre eleccion: " + credits[2]);
                System.out.println("Creditos - Nivelacion: " + credits[3]);
                System.out.println("Requisitos: " + (course.getRequisites().isEmpty()
                        ? "Ninguno" : String.join(", ", course.getRequisites())));
            } else {
                System.out.println("Curso no encontrado.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
        }
    }

    private static void deleteCourse(Scanner scan) {
        System.out.print("Codigo del curso a eliminar: ");
        try {
            long code = Long.parseLong(scan.nextLine().trim());
            System.out.print("¿Está seguro de eliminar este curso? (s/n): ");
            String confirmation = scan.nextLine().trim().toLowerCase();
            if (confirmation.equals("s") || confirmation.equals("si")) {
                // Solo llamar al método void, no asignar a variable boolean
                CourseManager.deleteCourse(code);
                System.out.println("Operación de eliminación completada.");
            } else {
                System.out.println("Eliminación cancelada.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
        }
    }

    private static void addRequisites(Scanner scan) {
        System.out.print("Codigo del curso al que agregar requisitos: ");
        try {
            long courseCode = Long.parseLong(scan.nextLine().trim());

            // Verificar que el curso existe
            Course course = CourseManager.findCourse(courseCode);
            if (course == null) {
                System.out.println("Curso no encontrado.");
                return;
            }

            System.out.println("Curso actual: " + course.getName());
            System.out.println("Requisitos actuales: "
                    + (course.getRequisites().isEmpty() ? "Ninguno" : String.join(", ", course.getRequisites())));

            List<String> newRequisites = collectRequisites(scan);
            if (!newRequisites.isEmpty()) {
                // Combinar requisitos existentes con los nuevos
                List<String> allRequisites = new ArrayList<>(course.getRequisites());
                for (String req : newRequisites) {
                    if (!allRequisites.contains(req)) {
                        allRequisites.add(req);
                    }
                }

                // Actualizar el curso - sin asignar a boolean
                CourseManager.updateCourse(
                        courseCode,
                        course.getName(),
                        course.getCredits(),
                        allRequisites
                );

                System.out.println("Requisitos agregados exitosamente.");

            } else {
                System.out.println("No se agregaron nuevos requisitos.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
        }
    }
}
