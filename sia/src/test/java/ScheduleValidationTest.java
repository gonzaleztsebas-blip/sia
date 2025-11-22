import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleValidationTest {

    @Test
    void testNoConflict() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"M", "J"};
        String[] times2 = {"9-11", "9-11"};

        boolean hasConflict = testScheduleConflict(days1, times1, days2, times2);

        assertFalse(hasConflict, "No debería detectarse conflicto");
    }

    @Test
    void testSameDaySameTime() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "M"};
        String[] times2 = {"7-9", "9-11"};

        boolean hasConflict = testScheduleConflict(days1, times1, days2, times2);

        assertTrue(hasConflict, "Debe detectarse conflicto en L 7-9");
    }

    @Test
    void testSameDayDifferentTime() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "M"};
        String[] times2 = {"9-11", "9-11"};

        boolean hasConflict = testScheduleConflict(days1, times1, days2, times2);

        assertFalse(hasConflict, "No debe detectarse conflicto porque cambia la hora");
    }

    @Test
    void testDifferentDaySameTime() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"M", "J"};
        String[] times2 = {"7-9", "7-9"};

        boolean hasConflict = testScheduleConflict(days1, times1, days2, times2);

        assertFalse(hasConflict, "No debe detectarse conflicto porque los días son diferentes");
    }

    @Test
    void testMultipleDaysConflict() {
        String[] days1 = {"L", "M", "W"};
        String[] times1 = {"7-9", "9-11", "14-16"};

        String[] days2 = {"M", "W", "V"};
        String[] times2 = {"9-11", "9-11", "9-11"};

        boolean hasConflict = testScheduleConflict(days1, times1, days2, times2);

        assertTrue(hasConflict, "Debe detectarse conflicto en M 9-11");
    }

    /**
     * Simula la validación de conflicto entre dos horarios.
     */
    private static boolean testScheduleConflict(String[] days1, String[] times1,
                                                String[] days2, String[] times2) {

        for (int i = 0; i < days1.length && i < times1.length; i++) {
            String day1 = days1[i].trim().toUpperCase();
            String time1 = times1[i].trim();

            for (int j = 0; j < days2.length && j < times2.length; j++) {
                String day2 = days2[j].trim().toUpperCase();
                String time2 = times2[j].trim();

                if (day1.equals(day2) && time1.equals(time2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
