/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sia.sia;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import sia.sia.data.*;
/**
 *
 * @author luzel
 */
public class Sia {
    
    public static void main(String[] args) {
        
        List<Group> groups = new ArrayList<>(); // vacío por ahora

        Course course = new Course(
                1001,
                "Programacion Orientada a Objetos",
                groups
            );

        Student student = new Student(
                2L,
                "estudiante1",
                "clave1",
                "Miguel",
                "Sanchez",
                new Date()   // Fecha actual, solo para prueba
            );

        Professor professor = new Professor(
                1L,
                "profe1",
                "clave2",
                "Carlos",
                "Perez",
                new Date()   // Fecha actual, solo para prueba
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
        
        groups.add(group);
        
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
    }
}
