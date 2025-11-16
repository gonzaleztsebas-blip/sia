/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.Objects;

/**
 *
 * @author luzel
 */
public class Grade {
    
    private Student student;
    private Group group;
    private double grade;

    public Grade(Student student, Group group, double garde) {
        this.student = student;
        this.group = group;
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public Group getGroup() {
        return group;
    }

    public double getGrade() {
        return grade;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.student);
        hash = 89 * hash + Objects.hashCode(this.group);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.grade) ^ (Double.doubleToLongBits(this.grade) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Grade other = (Grade) obj;
        if (Double.doubleToLongBits(this.grade) != Double.doubleToLongBits(other.grade)) {
            return false;
        }
        if (!Objects.equals(this.student, other.student)) {
            return false;
        }
        return Objects.equals(this.group, other.group);
    }

    @Override
    public String toString() {
        return "Grade{" + ", student=" + (student == null ? null : student.getId()) + 
                ", group=" + group + 
                ", group=" + (group == null ? null : group.getNumber()) + 
                '}';
    }
    
    
}
