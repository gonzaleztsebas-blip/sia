/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import sia.sia.business.GroupManager;
import sia.sia.business.StudentManager;

public class Grade {
    private String studentUsername;
    private String groupNumber;
    private double grade;
    
    // CONSTRUCTOR 1: Para usar con Student y Group objects
    public Grade(Student student, Group group, double grade) {
        this.studentUsername = student.getUser();
        this.groupNumber = String.valueOf(group.getNumber());
        this.grade = grade; // ¡IMPORTANTE: asignar el parámetro!
        System.out.println("DEBUG Grade Constructor: " + student.getUser() + ", " + group.getNumber() + ", " + grade);
    }
    
    // CONSTRUCTOR 2: Para usar directamente con strings (alternativo)
    public Grade(String studentUsername, String groupNumber, double grade) {
        this.studentUsername = studentUsername;
        this.groupNumber = groupNumber;
        this.grade = grade;
        System.out.println("DEBUG Grade Constructor: " + studentUsername + ", " + groupNumber + ", " + grade);
    }
    
    // Getters
    public String getStudentUsername() {
        return studentUsername;
    }
    
    public String getGroupNumber() {
        return groupNumber;
    }
    
    public double getGrade() {
        System.out.println("DEBUG getGrade(): retornando " + this.grade);
        return this.grade; // ¡NO return 0.0!
    }
    
    public Student getStudent() {
        return StudentManager.findStudent(studentUsername);
    }
    
    public Group getGroup() {
        return GroupManager.findGroup(groupNumber);
    }
    
    @Override
    public String toString() {
        return String.format("Grade{student=%s, group=%s, grade=%.2f}", 
                           studentUsername, groupNumber, grade);
    }
}