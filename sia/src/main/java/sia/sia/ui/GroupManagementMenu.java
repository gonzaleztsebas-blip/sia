/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;

import java.util.Scanner;
import sia.sia.business.GroupManager;

/**
 * Menú para gestión de grupos
 * @author luzel
 */
public class GroupManagementMenu {

    public static void show(Scanner scan) {
        boolean running = true;

        while (running) {
            System.out.println("\n----------------------------------------");
            System.out.println("           GESTION DE GRUPOS            ");
            System.out.println("----------------------------------------");
            System.out.println("1. Crear Grupo");
            System.out.println("2. Actualizar Horario");
            System.out.println("3. Buscar Grupo");
            System.out.println("4. Listar Grupos");
            System.out.println("5. Eliminar Grupo");
            System.out.println("6. Asignar Profesor");
            System.out.println("7. Remover Profesor");
            System.out.println("8. Agregar Estudiante");
            System.out.println("9. Remover Estudiante");
            System.out.println("10. Ver Estudiantes del Grupo");
            System.out.println("0. Volver");
            System.out.print("\nSeleccione una opcion: ");

            try {
                int option = Integer.parseInt(scan.nextLine().trim());

                switch (option) {
                    case 1 -> createGroup(scan);
                    case 2 -> updateSchedule(scan);
                    case 3 -> findGroup(scan);
                    case 4 -> listGroups();
                    case 5 -> deleteGroup(scan);
                    case 6 -> assignProfessor(scan);
                    case 7 -> removeProfessor(scan);
                    case 8 -> addStudent(scan);
                    case 9 -> removeStudent(scan);
                    case 10 -> listStudents(scan);
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

    private static void createGroup(Scanner scan) {
        System.out.println("\n--- CREAR GRUPO ---");

        System.out.print("Codigo del curso: ");
        String courseCode = scan.nextLine().trim();

        if (courseCode.isEmpty()) {
            System.out.println("El codigo del curso no puede estar vacio.");
            return;
        }

        System.out.println("\nDias de clase (separados por ;)");
        System.out.println("Codigos: L=Lunes, M=Martes, W=Miercoles, J=Jueves, V=Viernes, S=Sabado");
        System.out.print("Ejemplo (L;W;V): ");
        String daysInput = scan.nextLine().trim();

        if (daysInput.isEmpty()) {
            System.out.println("Debe especificar al menos un dia.");
            return;
        }

        String[] days = daysInput.split(";");

        System.out.println("\nHorarios (separados por ;, uno por cada dia)");
        System.out.println("Formato: 7-9, 9-11, 11-13, 14-16, 16-18");
        System.out.print("Ejemplo (7-9;7-9;7-9): ");
        String hoursInput = scan.nextLine().trim();

        if (hoursInput.isEmpty()) {
            System.out.println("Debe especificar los horarios.");
            return;
        }

        String[] hours = hoursInput.split(";");

        if (days.length != hours.length) {
            System.out.println("El numero de dias y horarios debe coincidir.");
            return;
        }

        System.out.print("Semestre (Ejemplo: 2025-1): ");
        String semester = scan.nextLine().trim();

        if (semester.isEmpty()) {
            System.out.println("El semestre no puede estar vacio.");
            return;
        }

        GroupManager.createGroup(days, hours, semester, courseCode);
    }

    private static void updateSchedule(Scanner scan) {
        System.out.println("\n--- ACTUALIZAR HORARIO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Nuevos dias (L;W;V): ");
        String daysInput = scan.nextLine().trim();
        String[] newDays = daysInput.split(";");

        System.out.print("Nuevos horarios (7-9;7-9;7-9): ");
        String hoursInput = scan.nextLine().trim();
        String[] newHours = hoursInput.split(";");

        if (newDays.length != newHours.length) {
            System.out.println("El numero de dias y horarios debe coincidir.");
            return;
        }

        GroupManager.updateSchedule(groupNumber, newDays, newHours);
    }

    private static void findGroup(Scanner scan) {
        System.out.println("\n--- BUSCAR GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        var group = GroupManager.findGroup(groupNumber);
        System.out.println(group == null ? "Grupo no encontrado." : group);
    }

    private static void listGroups() {
        System.out.println("\n--- LISTA DE GRUPOS ---");
        GroupManager.listGroups();
    }

    private static void deleteGroup(Scanner scan) {
        System.out.println("\n--- ELIMINAR GRUPO ---");

        System.out.print("Numero del grupo a eliminar: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            GroupManager.deleteGroup(groupNumber);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void assignProfessor(Scanner scan) {
        System.out.println("\n--- ASIGNAR PROFESOR ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Username del profesor: ");
        String professorUsername = scan.nextLine().trim();

        if (professorUsername.isEmpty()) {
            System.out.println("El username del profesor no puede estar vacio.");
            return;
        }

        GroupManager.assignProfessor(groupNumber, professorUsername);
    }

    private static void removeProfessor(Scanner scan) {
        System.out.println("\n--- REMOVER PROFESOR ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            GroupManager.removeProfessor(groupNumber);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void addStudent(Scanner scan) {
        System.out.println("\n--- AGREGAR ESTUDIANTE ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Username del estudiante: ");
        String studentUsername = scan.nextLine().trim();

        if (studentUsername.isEmpty()) {
            System.out.println("El username del estudiante no puede estar vacio.");
            return;
        }

        GroupManager.addStudent(groupNumber, studentUsername);
    }

    private static void removeStudent(Scanner scan) {
        System.out.println("\n--- REMOVER ESTUDIANTE ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        System.out.print("Username del estudiante: ");
        String studentUsername = scan.nextLine().trim();

        if (studentUsername.isEmpty()) {
            System.out.println("El username del estudiante no puede estar vacio.");
            return;
        }

        System.out.print("Esta seguro? (S/N): ");
        String confirm = scan.nextLine().trim().toUpperCase();

        if (confirm.equals("S")) {
            GroupManager.removeStudent(groupNumber, studentUsername);
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    private static void listStudents(Scanner scan) {
        System.out.println("\n--- ESTUDIANTES DEL GRUPO ---");

        System.out.print("Numero del grupo: ");
        String groupNumber = scan.nextLine().trim();

        if (groupNumber.isEmpty()) {
            System.out.println("El numero del grupo no puede estar vacio.");
            return;
        }

        GroupManager.listStudents(groupNumber);
    }
}
