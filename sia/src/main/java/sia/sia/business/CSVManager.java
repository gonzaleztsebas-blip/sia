package sia.sia.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import sia.sia.data.*;

public class CSVManager {

    private final static String USER_FILE_PATH = "src/main/resources/dataBase/usersCSV.csv";
    private final static String STUDENT_FILE_PATH = "src/main/resources/dataBase/studentCSV.csv";
    private final static String PROFESSOR_FILE_PATH = "src/main/resources/dataBase/professorCSV.csv";
    private final static String ADMIN_FILE_PATH = "src/main/resources/dataBase/adminCSV.csv";

    // ----------------------------------------- LOGIN --------------------------------------------------

    public static User login(String username, String password) {
        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {

            List<String[]> rows = reader.readAll();

            for (String[] row : rows) {
                if (row[0].equals(username) && row[1].equals(password)) {
                    String role = row[2];
                    switch (role) {
                        case "admin": return new Admin(username, password);
                        case "professor": return new Professor(username, password);
                        case "student": return new Student(username, password);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ----------------------------------------- SIGN UP --------------------------------------------------

    public static boolean signUp(String username, String password, String role) {

        if (userExists(username)) {
            return false;
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(USER_FILE_PATH, true))) {

            String[] newRow = {username, password, role};
            writer.writeNext(newRow);

            // guardar en el archivo correspondiente
            appendToRoleCSV(newRow);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void appendToRoleCSV(String[] row) throws IOException {

        String role = row[2];
        String targetFile;

        switch (role) {
            case "student":
                targetFile = STUDENT_FILE_PATH;
                break;
            case "professor":
                targetFile = PROFESSOR_FILE_PATH;
                break;
            case "admin":
                targetFile = ADMIN_FILE_PATH;
                break;
            default:
                return;
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(targetFile, true))) {
            writer.writeNext(row);
        }
    }

    // ----------------------------------------- UTILS --------------------------------------------------

    public static boolean userExists(String username) {
        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {

            for (String[] row : reader.readAll()) {
                if (row[0].equals(username)) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
