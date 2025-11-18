/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author luzel
 */
public class Course {

    private long code;
    private String name;
    private int credits;
    private List<String> requisites;
    private List<Group> offeredAs;

    public Course(String name, int credits) {
        this.name = name;
        this.credits = credits;
    }
    
    
    public Course(long code, String name, int credits, List<String> requisites) {
        this.name = name;
        this.code = code;
        this.credits = credits;
        this.requisites = new ArrayList<>(requisites);
    }

    public Course(long code, String name, int credits, List<String> requisites, List<Group> offeredAs) {
        this.name = name;
        this.offeredAs = offeredAs;
        this.code = code;
        this.credits = credits;
        this.requisites = new ArrayList<>(requisites);
    }

    public String getName() {
        return name;
    }

    public List<Group> getOfferedAs() {
        return offeredAs;
    }

    public String getOfferedAsString() {
        return offeredAs == null ? "[]" : offeredAs.stream().map(g -> String.valueOf(g.getNumber())).toList().toString();
    }

    public long getCode() {
        return code;
    }

    public String getCodeString() {
        return "" + code;
    }

    public int getCredits() {
        return credits;
    }

    public List<String> getRequisites() {
        return requisites;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOfferedAs(List<Group> offeredAs) {
        this.offeredAs = offeredAs;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setRequisites(List<String> requisites) {
        this.requisites = new ArrayList<>(requisites);
    }
    
    public void addRequisite(String newR){
        this.requisites.add(newR);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (int) (59 * hash + this.code);
        hash = 59 * hash + Objects.hashCode(this.name);
        hash = 59 * hash + Objects.hashCode(this.offeredAs);
        hash = 59 * hash + this.credits;
        hash = 59 * hash + Objects.hashCode(this.requisites);
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
        if (this.code != other.code) {
            return false;
        }
        if (this.credits != other.credits) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.offeredAs, other.offeredAs)) {
            return false;
        }
        return Objects.equals(this.requisites, other.requisites);
    }

    @Override
    public String toString() {
        return "Course{" + "code=" + code
                + ", name=" + name
                + ", offeredAs=" + offeredAs
                + ", credits=" + credits
                + ", requisites=" + requisites
                + '}';
    }

    public String[] toArray() {
        return new String[]{
            String.valueOf(code),
            name,
            String.valueOf(credits),
            String.join(",", requisites)
        };
    }
}