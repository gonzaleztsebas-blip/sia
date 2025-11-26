
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sia.sia.business.ScheduleValidator;

public class ScheduleValidationTest {

    @Test
    void testNoConflict() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"M", "J"};
        String[] times2 = {"9-11", "9-11"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertFalse(hasConflict, "No debería detectarse conflicto");
    }

    @Test
    void testSameDaySameTime() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "M"};
        String[] times2 = {"7-9", "9-11"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertTrue(hasConflict, "Debe detectarse conflicto en L 7-9");
    }

    @Test
    void testSameDayDifferentTime() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "M"};
        String[] times2 = {"9-11", "9-11"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertFalse(hasConflict, "No debe detectarse conflicto porque cambia la hora");
    }

    @Test
    void testDifferentDaySameTime() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"M", "J"};
        String[] times2 = {"7-9", "7-9"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertFalse(hasConflict, "No debe detectarse conflicto porque los días son diferentes");
    }

    @Test
    void testMultipleDaysConflict() {
        String[] days1 = {"L", "M", "W"};
        String[] times1 = {"7-9", "9-11", "14-16"};

        String[] days2 = {"M", "W", "V"};
        String[] times2 = {"9-11", "9-11", "9-11"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertTrue(hasConflict, "Debe detectarse conflicto en M 9-11");
    }

    @Test
    void testCaseInsensitive() {
        String[] days1 = {"l", "w", "v"}; // minúsculas
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "W", "V"}; // mayúsculas
        String[] times2 = {"7-9", "7-9", "7-9"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertTrue(hasConflict, "Debe detectarse conflicto aunque los días estén en diferente caso");
    }

    @Test
    void testTrimSpaces() {
        String[] days1 = {" L ", " W ", " V "}; // con espacios
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L", "W", "V"}; // sin espacios
        String[] times2 = {"7-9", "7-9", "7-9"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertTrue(hasConflict, "Debe detectarse conflicto aunque haya espacios en los días");
    }

    @Test
    void testEmptyArrays() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {}; // vacío
        String[] times2 = {};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertFalse(hasConflict, "No debe detectarse conflicto con arrays vacíos");
    }

    @Test
    void testNullArrays() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = null; // null
        String[] times2 = null;

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertFalse(hasConflict, "No debe detectarse conflicto con arrays null");
    }

    @Test
    void testDifferentLengths() {
        String[] days1 = {"L", "W", "V"};
        String[] times1 = {"7-9", "7-9", "7-9"};

        String[] days2 = {"L"}; // solo un día
        String[] times2 = {"7-9"};

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertTrue(hasConflict, "Debe detectarse conflicto aunque los arrays tengan diferente longitud");
    }

    @Test
    void testComplexTimeConflict() {
        String[] days1 = {"L", "M", "W"};
        String[] times1 = {"7-9", "8-10", "9-11"};

        String[] days2 = {"M", "W", "V"};
        String[] times2 = {"8-10", "9-11", "7-9"}; // Mismo horario en M y W

        boolean hasConflict = ScheduleValidator.hasConflictBetweenGroups(days1, times1, days2, times2);
        assertTrue(hasConflict, "Debe detectarse conflicto en M 8-10 y W 9-11");
    }
}
