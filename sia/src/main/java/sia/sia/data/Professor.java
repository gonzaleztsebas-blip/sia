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
public class Professor extends User {

    private List<Group> groupsTaught;

    public Professor(String user, String password) {
        super(user, password, "professor");
    }
    

    public Professor(String user, String password, long id, String firstName, String lastName, String birthDate) {
        super(user, password, "professor", id, firstName, lastName, birthDate);
        this.groupsTaught = null;
    }

    public Professor(String user, String password, long id, String firstName, String lastName, String birthDate, List<Group> groupsTaught) {
        super(user, password, "professor", id, firstName, lastName, birthDate);
        this.groupsTaught = groupsTaught;
    }

    public List<Group> getGroupsTaught() {
        return groupsTaught;
    }

    public String getGroupsTaughtString() {
        return "" + groupsTaught;
    }

    public void setGroupsTaught(List<Group> groupsTaught) {
        this.groupsTaught = groupsTaught;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.groupsTaught);
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
        final Professor other = (Professor) obj;
        return Objects.equals(this.groupsTaught, other.groupsTaught);
    }

    @Override
    public String toString() {
        return "Student{" + "user=" + getUser()
                + ", role=" + getRole()
                + "id=" + getId()
                + ", firstName=" + getFirstName()
                + ", lastName=" + getLastName()
                + ", birthDate=" + getBirthDate()
                + ", groupsTaught=" + groupsTaught
                + '}';
    }

    @Override
    public String[] toArray() {
        String[] array = {getUser(), getPassword(), getRole(), getIdString(), getFirstName(), getLastName(), getBirthDate(), getGroupsTaughtString()};
        return array;
    }
    
}
