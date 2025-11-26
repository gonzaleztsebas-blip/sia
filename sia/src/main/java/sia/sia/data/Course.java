/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author luzel
 */
public class Course {

    public static final int FUNDAMENTACION = 0;
    public static final int DISCIPLINAR = 1;
    public static final int LIBRE_ELECCION = 2;
    public static final int NIVELACION = 3;

    private long code;
    private String name;
    private int[] credits = new int[4];
    //credits[Fundamentacion,disciplinar,libre eleccion,nivelacion]

    private List<String> requisites;
    private List<Group> offeredAs;

    // Agregar este constructor a la clase Course
    public Course(String[] data) {
        this.code = Long.parseLong(data[0]);
        this.name = data[1];

        // Parsear créditos desde string "4,0,0,0"
        String[] creditsParts = data[2].split(",");
        this.credits = new int[4];

        // Asegurar que siempre tengas 4 elementos
        for (int i = 0; i < 4 && i < creditsParts.length; i++) {
            this.credits[i] = Integer.parseInt(creditsParts[i].trim());
        }
        // Si creditsParts tiene menos de 4 elementos, los demás quedan en 0

        // Parsear requisitos
        String reqStr = data[3].replace("[", "").replace("]", "").trim();
        if (reqStr.isEmpty()) {
            this.requisites = new ArrayList<>();
        } else {
            this.requisites = new ArrayList<>(Arrays.asList(reqStr.split(";")));
        }

        this.offeredAs = new ArrayList<>(); // Inicializar vacío
    }

    public Course(String name, int[] credits) {
        if (credits.length != 4) {
            throw new IllegalArgumentException("Credits array must have exactly 4 elements");
        }
        this.name = name;
        this.credits = credits;
    }

    public Course(long code, String name, int[] credits, List<String> requisites) {
        if (credits.length != 4) {
            throw new IllegalArgumentException("Credits array must have exactly 4 elements");
        }
        this.name = name;
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
        if (offeredAs == null) {
            return "";
        }
        return offeredAs.stream()
                .map(g -> String.valueOf(g.getNumber()))
                .collect(Collectors.joining(";"));
    }

    public long getCode() {
        return code;
    }

    public String getCodeString() {
        return "" + code;
    }

    public int[] getCredits() {
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

    public void setCredits(int[] credits) {
        this.credits = credits;
    }

    public void setRequisites(List<String> requisites) {
        this.requisites = new ArrayList<>(requisites);
    }

    public void addRequisite(String newR) {
        this.requisites.add(newR);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (int) (this.code ^ (this.code >>> 32));
        hash = 13 * hash + Objects.hashCode(this.name);
        hash = 13 * hash + Arrays.hashCode(this.credits);
        hash = 13 * hash + Objects.hashCode(this.requisites);
        hash = 13 * hash + Objects.hashCode(this.offeredAs);
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
                + ", credits=" + Arrays.toString(credits)
                + ", requisites=" + requisites
                + '}';
    }

    public String[] toArray() {
        // Convertir array de créditos a formato string
        String creditsStr = Arrays.stream(credits)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        return new String[]{
            String.valueOf(code),
            name,
            creditsStr, // Ej: "4,0,0,0"
            "[" + String.join(";", requisites) + "]"
        };
    }
}
