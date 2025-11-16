/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author luzel
 */
public class Course {
    
    private int number;
    private String name;
    private List<Group> offeredAs;
    
    public Course(int number, String name) {
        this.number = number;
        this.name = name;
        this.offeredAs = null;
    }
    public Course(int number, String name, List<Group> offeredAs) {
        this.number = number;
        this.name = name;
        this.offeredAs = offeredAs;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public List<Group> getOfferedAs() {
        return offeredAs;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOfferedAs(List<Group> offeredAs) {
        this.offeredAs = offeredAs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.number;
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.offeredAs);
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
        final Course other = (Course) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.offeredAs, other.offeredAs);
    }

    @Override
    public String toString() {
        return "Course{" + "number=" + number + 
                ", name=" + name + 
                ", offeredAs=" + offeredAs + 
                '}';
    }
    
    
}
