package sia.sia.ui;

import java.util.List;
import java.util.Scanner;
import sia.sia.business.EnrollmentManager;
import sia.sia.business.GroupManager;
import sia.sia.data.Group;
import sia.sia.data.Course;

public class EnrollmentMenu {

    public static void show(Scanner scan) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- GESTION DE INSCRIPCIONES ---");
            System.out.println("1. Inscribir estudiante");
            System.out.println("2. Retirar estudiante");
            System.out.println("3. Listar inscripciones de estudiante");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            String opt = scan.nextLine().trim();
            switch (opt) {
                case "1" -> enrollStudent(scan);
                case "2" -> withdrawStudent(scan);
                case "3" -> listStudentEnrollments(scan);
                case "0" -> running = false;
                default -> System.out.println("Opcion invalida");
            }
        }
    }

    private static void enrollStudent(Scanner scan) {
        System.out.print("Usuario estudiante: ");
        String user = scan.nextLine().trim();
        System.out.print("Numero grupo: ");
        String group = scan.nextLine().trim();
        boolean ok = EnrollmentManager.enrollStudent(user, group);
        System.out.println(ok ? "Inscripcion OK" : "Inscripcion fallida");
    }

    private static void withdrawStudent(Scanner scan) {
        System.out.print("Usuario estudiante: ");
        String user = scan.nextLine().trim();
        System.out.print("Numero grupo a retirar: ");
        String group = scan.nextLine().trim();
        boolean ok = EnrollmentManager.unenrollStudent(user, group);
        System.out.println(ok ? "Retiro OK" : "Retiro fallido");
    }

    private static void listStudentEnrollments(Scanner scan) {
        System.out.print("Usuario estudiante: ");
        String user = scan.nextLine().trim();
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(user);
        if (enrollments.isEmpty()) {
            System.out.println("El estudiante no tiene inscripciones activas");
        } else {
            System.out.println("Inscripciones activas:");
            for (Group group : enrollments) {
                Course course = group.getRepresents();
                System.out.printf("Grupo: %s | Curso: %s - %s | Semestre: %s\n", 
                    group.getNumber(), 
                    course.getCode(), 
                    course.getName(), 
                    group.getSemester());
            }
        }
    }
}