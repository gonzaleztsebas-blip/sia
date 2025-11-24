/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;


/**
 *
 * @author luzel
 */
public class Admin extends User {

    private boolean permission;

    public Admin(String user, String password) {
        super(user, password, "admin");
    }

    public Admin(String user, String password, long id, String firstName, String lastName, String birthDate, boolean permission) {
        super(user, password, "admin", id, firstName, lastName, birthDate);
        this.permission = permission;
    }

 
    public boolean getPermission() {
        return permission;
    }
    
    public String getPermissionString() {
        return "" + permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.permission ? 1 : 0);
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
        final Admin other = (Admin) obj;
        return this.permission == other.permission;
    }

    @Override
    public String toString() {
        return "Admin{" + "permission=" + permission + '}';
    }
    
    @Override
    public String[] toArray() {
        String[] array = {getUser(), getPassword(), getRole(), getIdString(), getFirstName(), getLastName(), getBirthDate(), getPermissionString()};
        return array;
    }
    
    @Override
    public void menu() {
        // Swing interface is now handled by SIAFrame
    }
    
    
}
