/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author luzel
 */
public class Student extends User {

    private List<Group> attends;

    public Student(String user, String password) {
        super(user, password, "student");
    }
    
    public Student(String user, String password, long id, String firstName, String lastName, String birthDate) {
        super(user, password, "student", id, firstName, lastName, birthDate);
        this.attends = null;
    }

    public Student(String user, String password, long id, String firstName, String lastName, String birthDate, List<Group> attends) {
        super(user, password, "student", id, firstName, lastName, birthDate);
        this.attends = attends;
    }

    public List<Group> getAttends() {
        return attends;
    }

    public String getAttendsString() {
        return "" + (attends == null ? null : attends.stream().map(Group::getNumber).toList());
    }

    public void setAttends(List<Group> attends) {
        this.attends = attends;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.attends);
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
        final Student other = (Student) obj;
        return Objects.equals(this.attends, other.attends);
    }

    @Override
    public String toString() {
        return "Student{" + "user=" + getUser()
                + ", role=" + getRole()
                + ", id=" + getId()
                + ", firstName=" + getFirstName()
                + ", lastName=" + getLastName()
                + ", birthDate=" + getBirthDate()
                + ", attends=" + (attends == null ? null : attends.stream().map(Group::getNumber).toList())
                + '}';
    }

    @Override
    public String[] toArray() {
        String[] array = {getUser(), getPassword(), getRole(), getIdString(), getFirstName(), getLastName(), getBirthDate(), getAttendsString()};
        return array;
    }

    
    @Override
    public void menu() {
        System.out.println("Menú Admin: gestionar usuarios, reportes, etc.");
    }
}
