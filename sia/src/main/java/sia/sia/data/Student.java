package sia.sia.data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import sia.sia.business.GradeManager;

public class Student extends User {

    private List<Group> attends;
    private int[] approvedCredits = new int[4]; // [Fundamentación, Disciplinar, Libre elección, Nivelación]
    private double papa; // Promedio Acumulado Ponderado Acumulado (todas las materias)
    private double pa;   // Promedio Acumulado (solo materias aprobadas)

    public Student(String user, String password) {
        super(user, password, "student");
        this.approvedCredits = new int[4];
        this.papa = 0.0;
        this.pa = 0.0;
    }

    public Student(String user, String password, long id, String firstName, String lastName,
            String birthDate, int[] approvedCredits) {
        super(user, password, "student", id, firstName, lastName, birthDate);
        this.attends = null;
        this.approvedCredits = approvedCredits != null ? approvedCredits : new int[4];
        this.papa = 0.0;
        this.pa = 0.0;
    }

    public Student(String user, String password, long id, String firstName, String lastName,
            String birthDate, List<Group> attends, int[] approvedCredits,
            double papa, double pa) {
        super(user, password, "student", id, firstName, lastName, birthDate);
        this.attends = attends;
        this.approvedCredits = approvedCredits != null ? approvedCredits : new int[4];
        this.papa = papa;
        this.pa = pa;
    }

    // Constructor desde String[] para cargar desde CSV
    public Student(String[] data) {
        super(data[0], data[1], data[2], Long.parseLong(data[3]), data[4], data[5], data[6]);

        // Parsear attends (índice 7)
        if (data.length > 7 && !data[7].isEmpty() && !data[7].equals("null")) {
            this.attends = null; // Por ahora null
        } else {
            this.attends = null;
        }

        // Parsear approvedCredits (índice 8)
        if (data.length > 8 && !data[8].isEmpty()) {
            String creditsStr = data[8].replace("[", "").replace("]", "").trim();
            if (!creditsStr.isEmpty()) {
                String[] parts = creditsStr.split(",");
                this.approvedCredits = new int[4];
                for (int i = 0; i < 4 && i < parts.length; i++) {
                    this.approvedCredits[i] = Integer.parseInt(parts[i].trim());
                }
            } else {
                this.approvedCredits = new int[4];
            }
        } else {
            this.approvedCredits = new int[4];
        }

        // Parsear PAPA (índice 9)
        if (data.length > 9 && !data[9].isEmpty()) {
            this.papa = Double.parseDouble(data[9]);
        } else {
            this.papa = 0.0;
        }

        // Parsear PA (índice 10)
        if (data.length > 10 && !data[10].isEmpty()) {
            this.pa = Double.parseDouble(data[10]);
        } else {
            this.pa = 0.0;
        }
    }

    // Getters y setters
    public double getPapa() {
        return papa;
    }

    public void setPapa(double papa) {
        this.papa = papa;
    }

    public double getPa() {
        return pa;
    }

    public void setPa(double pa) {
        this.pa = pa;
    }

    public void setApprovedCredits(int[] approvedCredits) {
        if (approvedCredits != null && approvedCredits.length == 4) {
            this.approvedCredits = approvedCredits;
        } else {
            throw new IllegalArgumentException("Approved credits must have exactly 4 elements");
        }
    }

    public int[] getApprovedCredits() {
        return approvedCredits;
    }

    public String getApprovedCreditsString() {
        return Arrays.toString(approvedCredits);
    }

    public void addApprovedCredits(int[] courseCredits) {
        if (courseCredits != null && courseCredits.length == 4) {
            for (int i = 0; i < 4; i++) {
                this.approvedCredits[i] += courseCredits[i];
            }
        }
    }

    public int getTotalApprovedCredits() {
        return Arrays.stream(approvedCredits).sum();
    }

    public List<Group> getAttends() {
        return attends;
    }

    public String getAttendsString() {
        return "" + (attends == null ? "null" : attends.stream().map(Group::getNumber).toList());
    }

    public void setAttends(List<Group> attends) {
        this.attends = attends;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.attends);
        hash = 97 * hash + Arrays.hashCode(this.approvedCredits);
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
        return Objects.equals(this.attends, other.attends)
                && Arrays.equals(this.approvedCredits, other.approvedCredits);
    }

    @Override
    public String toString() {
        return "Student{" + "user=" + getUser()
                + ", role=" + getRole()
                + ", id=" + getId()
                + ", firstName=" + getFirstName()
                + ", lastName=" + getLastName()
                + ", birthDate=" + getBirthDate()
                + ", attends=" + (attends == null ? "null" : attends.stream().map(Group::getNumber).toList())
                + ", approvedCredits=" + Arrays.toString(approvedCredits)
                + ", totalApproved=" + getTotalApprovedCredits()
                + ", PAPA=" + String.format("%.2f", papa)
                + ", PA=" + String.format("%.2f", pa)
                + '}';
    }

    @Override
    public String[] toArray() {
        String[] array = {
            getUser(),
            getPassword(),
            getRole(),
            getIdString(),
            getFirstName(),
            getLastName(),
            getBirthDate(),
            getAttendsString(),
            getApprovedCreditsString(),
            String.valueOf(papa),
            String.valueOf(pa)
        };
        return array;
    }

    public boolean meetsPrerequisites(Course course) {
        if (course.getRequisites() == null || course.getRequisites().isEmpty()) {
            return true; // no hay requisitos
        }

        // cursos que el estudiante ya aprobó
        List<String> passed = GradeManager.getPassedCourses(this.getUser());

        if (passed == null) {
            return false;
        }

        // cada requisito debe estar en passed
        for (String req : course.getRequisites()) {
            if (!passed.contains(req)) {
                return false;
            }
        }

        return true;
    }
}
