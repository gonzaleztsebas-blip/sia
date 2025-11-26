/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.business;

import sia.sia.data.Group;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilidad para validar cruces de horarios
 *
 * @author luzel
 */
public class ScheduleValidator {

    /**
     * OPCIÓN 1: Validación con bloques de tiempo predefinidos Formato:
     * daysOfWeek = ["L", "M", "W"] timesOfDay = ["7-9", "7-9", "7-9"]
     */
    public static boolean hasScheduleConflict(String studentUser, Group newGroup) {

        // Obtener todos los grupos actuales del estudiante
        List<Group> studentGroups = GroupManager.getGroupsByStudent(studentUser);

        if (studentGroups.isEmpty()) {
            return false; // No tiene grupos, no hay conflicto
        }

        String[] newDays = newGroup.getDaysOfWeek();
        String[] newTimes = newGroup.getTimesOfDay();

        // Validación de seguridad
        if (newDays == null || newTimes == null || newDays.length == 0 || newTimes.length == 0) {
            System.out.println("El grupo no tiene horario definido.");
            return false;
        }

        // Verificar contra cada grupo existente
        for (Group existingGroup : studentGroups) {
            String[] existingDays = existingGroup.getDaysOfWeek();
            String[] existingTimes = existingGroup.getTimesOfDay();

            // Validación de seguridad
            if (existingDays == null || existingTimes == null) {
                continue;
            }

            // Verificar si hay cruce
            if (hasConflictBetweenGroups(newDays, newTimes, existingDays, existingTimes)) {
                System.out.println("Cruce de horario detectado con el curso: "
                        + existingGroup.getRepresents().getName()
                        + " (Grupo " + existingGroup.getNumber() + ")");
                return true;
            }
        }

        return false; // No hay conflictos
    }

    /**
     * Compara dos horarios para detectar cruces
     */
    /**
     * Compara dos horarios para detectar cruces
     */
    public static boolean hasConflictBetweenGroups(String[] days1, String[] times1,
            String[] days2, String[] times2) {

        // Validar arrays null o vacíos
        if (days1 == null || times1 == null || days2 == null || times2 == null) {
            return false;
        }
        if (days1.length == 0 || times1.length == 0 || days2.length == 0 || times2.length == 0) {
            return false;
        }

        // Crear un mapa de día -> bloques de tiempo para el primer grupo
        for (int i = 0; i < days1.length && i < times1.length; i++) {
            String day1 = days1[i] != null ? days1[i].trim().toUpperCase() : "";
            String time1 = times1[i] != null ? times1[i].trim() : "";

            if (day1.isEmpty() || time1.isEmpty()) {
                continue;
            }

            // Comparar con el segundo grupo
            for (int j = 0; j < days2.length && j < times2.length; j++) {
                String day2 = days2[j] != null ? days2[j].trim().toUpperCase() : "";
                String time2 = times2[j] != null ? times2[j].trim() : "";

                if (day2.isEmpty() || time2.isEmpty()) {
                    continue;
                }

                // Si es el mismo día Y mismo bloque de tiempo → CONFLICTO
                if (day1.equals(day2) && time1.equals(time2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * OPCIÓN 2: Validación con horarios exactos (más compleja) Formato:
     * daysOfWeek = ["L", "M"] timesOfDay = ["07:00-09:00", "07:00-09:00"]
     */
    public static boolean hasScheduleConflictDetailed(String studentUser, Group newGroup) {

        List<Group> studentGroups = GroupManager.getGroupsByStudent(studentUser);

        if (studentGroups.isEmpty()) {
            return false;
        }

        String[] newDays = newGroup.getDaysOfWeek();
        String[] newTimes = newGroup.getTimesOfDay();

        if (newDays == null || newTimes == null) {
            return false;
        }

        for (Group existingGroup : studentGroups) {
            String[] existingDays = existingGroup.getDaysOfWeek();
            String[] existingTimes = existingGroup.getTimesOfDay();

            if (existingDays == null || existingTimes == null) {
                continue;
            }

            if (hasTimeRangeConflict(newDays, newTimes, existingDays, existingTimes)) {
                System.out.println("Cruce de horario con: "
                        + existingGroup.getRepresents().getName());
                return true;
            }
        }

        return false;
    }

    /**
     * Compara rangos de tiempo (más preciso) Formato: "07:00-09:00"
     */
    public static boolean hasTimeRangeConflict(String[] days1, String[] times1,
            String[] days2, String[] times2) {

        for (int i = 0; i < days1.length && i < times1.length; i++) {
            String day1 = days1[i].trim().toUpperCase();
            String timeRange1 = times1[i].trim();

            for (int j = 0; j < days2.length && j < times2.length; j++) {
                String day2 = days2[j].trim().toUpperCase();
                String timeRange2 = times2[j].trim();

                // Mismo día
                if (day1.equals(day2)) {
                    // Parsear rangos de tiempo
                    TimeRange range1 = parseTimeRange(timeRange1);
                    TimeRange range2 = parseTimeRange(timeRange2);

                    if (range1 != null && range2 != null && range1.overlaps(range2)) {
                        return true; // CONFLICTO
                    }
                }
            }
        }

        return false;
    }

    /**
     * Parsea un rango de tiempo "07:00-09:00" a objeto TimeRange
     */
    private static TimeRange parseTimeRange(String timeRange) {
        try {
            String[] parts = timeRange.split("-");
            if (parts.length != 2) {
                return null;
            }

            int start = parseTime(parts[0].trim());
            int end = parseTime(parts[1].trim());

            return new TimeRange(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convierte "07:00" a minutos desde medianoche (420)
     */
    private static int parseTime(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    /**
     * Clase interna para representar un rango de tiempo
     */
    private static class TimeRange {

        int start; // minutos desde medianoche
        int end;

        TimeRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean overlaps(TimeRange other) {
            // Dos rangos se superponen si:
            // - El inicio de uno está dentro del otro
            // - O el final de uno está dentro del otro
            return (this.start < other.end && this.end > other.start);
        }
    }

    /**
     * OPCIÓN 3: Validación simplificada por cadenas (menos precisa pero
     * funcional)
     */
    public static boolean hasScheduleConflictSimple(String studentUser, Group newGroup) {

        List<Group> studentGroups = GroupManager.getGroupsByStudent(studentUser);

        for (Group existing : studentGroups) {
            // Convertir a Sets para comparación rápida
            Set<String> newSchedule = createScheduleSet(
                    newGroup.getDaysOfWeek(),
                    newGroup.getTimesOfDay()
            );

            Set<String> existingSchedule = createScheduleSet(
                    existing.getDaysOfWeek(),
                    existing.getTimesOfDay()
            );

            // Buscar intersección
            newSchedule.retainAll(existingSchedule);

            if (!newSchedule.isEmpty()) {
                System.out.println("Cruce de horario con: "
                        + existing.getRepresents().getName());
                return true;
            }
        }

        return false;
    }

    /**
     * Crea un conjunto de "DIA-HORA" para comparación Ejemplo: {"L-7-9",
     * "M-7-9", "W-7-9"}
     */
    private static Set<String> createScheduleSet(String[] days, String[] times) {
        Set<String> schedule = new HashSet<>();

        if (days == null || times == null) {
            return schedule;
        }

        for (int i = 0; i < days.length && i < times.length; i++) {
            schedule.add(days[i].trim().toUpperCase() + "-" + times[i].trim());
        }

        return schedule;
    }

    /**
     * Utilidad: Muestra el horario de un grupo de forma legible
     */
    public static void printSchedule(Group group) {
        System.out.println("\nHorario del grupo " + group.getNumber() + ":");
        System.out.println("Curso: " + group.getRepresents().getName());

        String[] days = group.getDaysOfWeek();
        String[] times = group.getTimesOfDay();

        if (days != null && times != null) {
            for (int i = 0; i < days.length && i < times.length; i++) {
                System.out.println("  " + getDayName(days[i]) + ": " + times[i]);
            }
        } else {
            System.out.println("  (Sin horario definido)");
        }
    }

    /**
     * Convierte código de día a nombre completo
     */
    private static String getDayName(String code) {
        switch (code.trim().toUpperCase()) {
            case "L":
                return "Lunes";
            case "M":
                return "Martes";
            case "W":
                return "Miercoles";
            case "J":
                return "Jueves";
            case "V":
                return "Viernes";
            case "S":
                return "Sabado";
            case "D":
                return "Domingo";
            default:
                return code;
        }
    }

    /**
     * Método de prueba
     */
    public static void testScheduleValidator() {
        System.out.println("=== PRUEBA DE VALIDACION DE HORARIOS ===\n");

        // Crear grupos de prueba
        String[] days1 = {"L", "M", "W"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "W", "V"};
        String[] times2 = {"9-11", "9-11", "9-11"};

        String[] days3 = {"L", "M"};
        String[] times3 = {"7-9", "7-9"}; // CONFLICTO con grupo 1

        boolean conflict1 = hasConflictBetweenGroups(days1, times1, days2, times2);
        System.out.println("Grupo 1 vs Grupo 2: " + (conflict1 ? "CONFLICTO" : "OK"));

        boolean conflict2 = hasConflictBetweenGroups(days1, times1, days3, times3);
        System.out.println("Grupo 1 vs Grupo 3: " + (conflict2 ? "CONFLICTO" : "OK"));
    }
}
