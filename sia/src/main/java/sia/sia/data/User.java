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
public abstract class User {
    
    protected String user;
    protected String password;
    protected String role;
    protected long id;
    protected String firstName;
    protected String lastName;
    protected String birthDate;
    
    public User(String user, String password, String role) {
        this.user = user;
        this.password = password;
        this.role = role;
    }
    
    public User(String user, String password,String role,long id, String firstName, String lastName, String birthDate) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }
    
    public long getId() {
        return id;
    }
    
    public String getIdString(){
        return String.valueOf(id);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 89 * hash + Objects.hashCode(this.user);
        hash = 89 * hash + Objects.hashCode(this.password);
        hash = 89 * hash + Objects.hashCode(this.role);
        hash = 89 * hash + Objects.hashCode(this.firstName);
        hash = 89 * hash + Objects.hashCode(this.lastName);
        hash = 89 * hash + Objects.hashCode(this.birthDate);
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
        final User other = (User) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        return Objects.equals(this.birthDate, other.birthDate);
    }



    @Override
    public String toString() {
        return "Person{" + "user=" + user + 
                ", role=" + role +
                ", id=" + id +               
                ", firstName=" + firstName + 
                ", lastName=" + lastName + 
                ", birthDate=" + birthDate + 
                '}';
    }
    
    public String[] toArray() {
        String[] array = {getUser(),getPassword(),getRole(),getIdString(),getFirstName(),getLastName(),getBirthDate()};
        return array;
    }
    
}
