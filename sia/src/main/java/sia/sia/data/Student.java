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
public class Student extends Person{
    
    private List<Group> attends;
    
    public Student(long id, String user, String password, String firstName, String lastName, Date birthDate) {
        super(id, user, password, firstName, lastName, birthDate);
        this.attends = null;
    }
    public Student(List<Group> attends, long id, String user, String password, String firstName, String lastName, Date birthDate) {
        super(id, user, password, firstName, lastName, birthDate);
        this.attends = attends;
    }

    public List<Group> getAttends() {
        return attends;
    }

    public String getAttendsString() {
        return ""+(attends == null ? null : attends.stream().map(Group::getNumber).toList());
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
        return "Student{" + "id=" + getId() + 
                ", user=" + getUser() + 
                ", firstName=" + getFirstName() + 
                ", lastName=" + getLastName() + 
                ", birthDate=" + getBirthDate() + 
                ", attends=" + (attends == null ? null : attends.stream().map(Group::getNumber).toList()) +
                '}';
    }
    
    @Override
    public String[] toArray() {
        String[] array = {getIdString(),getUser(),getPassword(),getFirstName(),getLastName(),getBirthDateString(),getAttendsString()};
        return array;
    }
    
}
