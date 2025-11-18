/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package sia.sia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import sia.sia.business.CSVManager;
import static sia.sia.business.CSVManager.*;
import sia.sia.data.*;

/**
 *
 * @author luzel
 */
public class Sia {

    public static void main(String[] args) {

        Course course = new Course(
                "Programacion Orientada a Objetos",
                2
        );

        Admin admin = new Admin(
                "admin1",
                "clave0"
        );

        Student student = new Student(
                "estudiante1",
                "clave1",
                1L,
                "Miguel",
                "Sanchez",
                "29/05/2007" // Fecha actual, solo para prueba
        );

        Professor professor = new Professor(
                "profe1",
                "clave2",
                3L,
                "Carlos",
                "Perez",
                "29/05/2007" // Fecha actual, solo para prueba
        );

        List<Student> students = new ArrayList<>();
        students.add(student);

        List<Grade> grades = new ArrayList<>();

        Group group = new Group(
                10L,
                new String[]{"Lunes", "Miercoles"},
                new String[]{"08:00-10:00", "08:00-10:00"},
                "2025-1",
                course,
                professor,
                students,
                grades
        );

        Grade grade = new Grade(student, group, 4.5);
        grades.add(grade);


        System.out.println("----------------------");
        System.out.println(student.toString());
        System.out.println("----------------------");
        System.out.println(professor.toString());
        System.out.println("----------------------");
        System.out.println(group.toString());
        System.out.println("----------------------");
        System.out.println(grade.toString());
        System.out.println("----------------------");
        System.out.println(course.toString());
        System.out.println("----------------------");


        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Salir");
            System.out.print("Opcion: ");
            
            int option = scan.nextInt();
            scan.nextLine();

            if (option == 1) {
                System.out.print("Username: ");
                String username = scan.nextLine();

                System.out.print("Password: ");
                String password = scan.nextLine();

                System.out.print("Role: ");
                String role = scan.nextLine();

                boolean ok = CSVManager.signUp(username, password, role);
                if (ok) {
                    System.out.println("Usuario creado exitosamente!");
                } else {
                    System.out.println("El usuario ya existe.");
                }
            } else if (option == 2) {
                System.out.print("Username: ");
                String username = scan.nextLine();
                
                System.out.print("Password: ");
                String password = scan.nextLine();

                User user = login(username, password);
                if (user != null) {
                    System.out.println("Bienvenido " + user.getUser());
                    user.menu(); // ← cada clase hija tiene su propio menú
                } else {
                    System.out.println("Usuario o contraseña incorrectos");
                }

            } else {
                System.out.println("Saliendo...");
                break;
            }

        }
    }
}
