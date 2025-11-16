/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.Date;

/**
 *
 * @author luzel
 */
public class Admin extends Person {
    
    public Admin(long id, String user, String password) {
        super(id, user, password);
    }
       
    public Admin(long id, String user, String password, String firstName, String lastName, Date birthDate) {
        super(id, user, password, firstName, lastName, birthDate);
    }

    private void setPersonUser(Person person, String newUser){
        person.setUser(newUser);
    }
    
    
}
