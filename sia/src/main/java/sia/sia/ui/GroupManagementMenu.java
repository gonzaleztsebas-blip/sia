package sia.sia.ui;

import java.util.*;
import sia.sia.business.GroupManager;

public class GroupManagementMenu {

    public static void show(Scanner scan) {
        boolean run = true;
        while (run) {
            System.out.println("\n--- GESTION DE GRUPOS ---");
            System.out.println("1. Crear Grupo");
            System.out.println("2. Actualizar Horario");
            System.out.println("3. Asignar Profesor");
            System.out.println("4. Agregar Estudiante");
            System.out.println("5. Listar Grupos");
            System.out.println("6. Ver Estudiantes de Grupo");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" ->
                    createGroup(scan);
                case "2" ->
                    updateSchedule(scan);
                case "3" ->
                    assignProfessor(scan);
                case "4" ->
                    addStudent(scan);
                case "5" ->
                    GroupManager.listGroups();
                case "6" -> {
                    System.out.print("Numero grupo: ");
                    GroupManager.listStudents(scan.nextLine().trim());
                }
                case "0" ->
                    run = false;
                default ->
                    System.out.println("Opcion invalida");
            }
        }
    }

    private static void createGroup(Scanner scan) {
        System.out.print("Dias (ej: L;M): ");
        String days = scan.nextLine().trim();
        System.out.print("Horas (ej: 08:00;10:00): ");
        String times = scan.nextLine().trim();
        System.out.print("Semestre: ");
        String sem = scan.nextLine().trim();
        System.out.print("Codigo curso: ");
        String course = scan.nextLine().trim();
        GroupManager.createGroup(days.isEmpty() ? new String[0] : days.split(";"), times.isEmpty() ? new String[0] : times.split(";"), sem, course);
    }

    private static void updateSchedule(Scanner scan) {
        System.out.print("Numero grupo: ");
        String number = scan.nextLine().trim();
        System.out.print("Nuevos dias (L;M): ");
        String d = scan.nextLine().trim();
        System.out.print("Nuevas horas (08:00;10:00): ");
        String t = scan.nextLine().trim();
        GroupManager.updateSchedule(number, d.isEmpty() ? new String[0] : d.split(";"), t.isEmpty() ? new String[0] : t.split(";"));
    }

    private static void assignProfessor(Scanner scan) {
        System.out.print("Grupo: ");
        String g = scan.nextLine().trim();
        System.out.print("Profesor username: ");
        String p = scan.nextLine().trim();
        GroupManager.assignProfessor(g, p);
    }

    private static void addStudent(Scanner scan) {
        System.out.print("Grupo: ");
        String g = scan.nextLine().trim();
        System.out.print("Estudiante username: ");
        String s = scan.nextLine().trim();
        GroupManager.addStudent(g, s);
    }
}
