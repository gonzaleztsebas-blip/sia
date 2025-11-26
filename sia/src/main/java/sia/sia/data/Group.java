/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author luzel
 */
public class Group {

    private long number;
    private String[] daysOfWeek;
    private String[] timesOfDay;
    private String semester;
    private Course represents;
    private Professor taughtBy;
    private List<Student> attendedBy;
    private List<Grade> issues;

    public Group(long number, String[] daysOfWeek, String[] timesOfDay, String semester, Course represents) {
        this.number = number;
        this.daysOfWeek = daysOfWeek;
        this.timesOfDay = timesOfDay;
        this.semester = semester;
        this.represents = represents;
        this.taughtBy = null;
        this.attendedBy = null;
        this.issues = null;
    }

    public Group(long number, String[] daysOfWeek, String[] timesOfDay, String semester, Course represents, Professor taughtBy, List<Student> attendedBy, List<Grade> issues) {
        this.number = number;
        this.daysOfWeek = daysOfWeek;
        this.timesOfDay = timesOfDay;
        this.semester = semester;
        this.represents = represents;
        this.taughtBy = taughtBy;
        this.attendedBy = attendedBy;
        this.issues = issues;
    }

    public long getNumber() {
        return number;
    }

    public String[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public String[] getTimesOfDay() {
        return timesOfDay;
    }

    public String getSemester() {
        return semester;
    }

    public Course getRepresents() {
        return represents;
    }

    public Professor getTaughtBy() {
        return taughtBy;
    }

    public List<Student> getAttendedBy() {
        return attendedBy;
    }

    public List<Grade> getIssues() {
        return issues;
    }

    public String getScheduleFormatted() {
        if (daysOfWeek == null || timesOfDay == null) {
            return "Sin horario";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < daysOfWeek.length; i++) {
            String day = daysOfWeek[i];
            String time = (i < timesOfDay.length) ? timesOfDay[i] : "";

            sb.append(getDayName(day)).append(": ").append(time);

            if (i < daysOfWeek.length - 1) {
                sb.append(" | ");
            }
        }

        return sb.toString();
    }

    private String getDayName(String code) {
        switch (code.trim().toUpperCase()) {
            case "L":
                return "Lunes";
            case "M":
                return "Martes";
            case "W":
                return "Miércoles";
            case "J":
                return "Jueves";
            case "V":
                return "Viernes";
            case "S":
                return "Sábado";
            case "D":
                return "Domingo";
            default:
                return code;
        }
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setDaysOfWeek(String[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public void setTimesOfDay(String[] timesOfDay) {
        this.timesOfDay = timesOfDay;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setRepresents(Course represents) {
        this.represents = represents;
    }

    public void setTaughtBy(Professor taughtBy) {
        this.taughtBy = taughtBy;
    }

    public void setAttendedBy(List<Student> attendedBy) {
        this.attendedBy = attendedBy;
    }

    public void setIssues(List<Grade> issues) {
        this.issues = issues;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (int) (this.number ^ (this.number >>> 32));
        hash = 19 * hash + Arrays.deepHashCode(this.daysOfWeek);
        hash = 19 * hash + Arrays.deepHashCode(this.timesOfDay);
        hash = 19 * hash + Objects.hashCode(this.semester);
        hash = 19 * hash + Objects.hashCode(this.represents);
        hash = 19 * hash + Objects.hashCode(this.taughtBy);
        hash = 19 * hash + Objects.hashCode(this.attendedBy);
        hash = 19 * hash + Objects.hashCode(this.issues);
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
        final Group other = (Group) obj;
        if (this.number != other.number) {
            return false;
        }
        if (!Objects.equals(this.semester, other.semester)) {
            return false;
        }
        if (!Arrays.deepEquals(this.daysOfWeek, other.daysOfWeek)) {
            return false;
        }
        if (!Arrays.deepEquals(this.timesOfDay, other.timesOfDay)) {
            return false;
        }
        if (!Objects.equals(this.represents, other.represents)) {
            return false;
        }
        if (!Objects.equals(this.taughtBy, other.taughtBy)) {
            return false;
        }
        if (!Objects.equals(this.attendedBy, other.attendedBy)) {
            return false;
        }
        return Objects.equals(this.issues, other.issues);
    }

    @Override
    public String toString() {
        return "Group{"
                + "number=" + number
                + ", daysOfWeek=" + Arrays.toString(daysOfWeek)
                + ", timesOfDay=" + Arrays.toString(timesOfDay)
                + ", semester=" + semester
                + ", represents=" + (represents == null ? null : represents.getCode())
                + ", taughtBy=" + (taughtBy == null ? null : taughtBy.getId())
                + ", attendedBy=" + (attendedBy == null ? null : attendedBy.stream().map(Student::getId).toList())
                + ", issues=" + (issues == null ? null : issues.stream().map(grade -> grade.getGrade()).toList())
                + '}';
    }

    public String[] toArray() {
        return new String[]{
            String.valueOf(number),
            daysOfWeek == null ? "" : String.join(";", daysOfWeek),
            timesOfDay == null ? "" : String.join(";", timesOfDay),
            semester == null ? "" : semester,
            represents == null ? "" : String.valueOf(represents.getCode()),
            taughtBy == null ? "" : String.valueOf(taughtBy.getId()),
            attendedBy == null ? ""
            : attendedBy.stream()
            .map(student -> String.valueOf(student.getId()))
            .reduce((a, b) -> a + ";" + b)
            .orElse(""),
            issues == null ? ""
            : issues.stream()
            .map(grade -> String.valueOf(grade.getGrade()))
            .reduce((a, b) -> a + ";" + b)
            .orElse("")
        };
    }

}
