package sia.sia.business;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sia.sia.data.Professor;

/**
 * ProfessorManager - versión profesional
 *
 * Formato esperado en professorCSV.csv:
 * user,password,role,id,firstName,lastName,birthDate
 *
 * Formato esperado en usersCSV.csv:
 * user,password,role
 */
public class ProfessorManager {

    private final static String USER_FILE_PATH = "src\\main\\resources\\dataBase\\usersCSV.csv";
    private final static String PROFESSOR_FILE_PATH = "src\\main\\resources\\dataBase\\professorCSV.csv";

    // cache en memoria (fuente de verdad hasta que se llame reload())
    private static List<String[]> professors = loadProfessors();

    // -------------------- CARGA / RELOAD --------------------
    private static List<String[]> loadProfessors() {
        try (CSVReader reader = new CSVReader(new FileReader(PROFESSOR_FILE_PATH))) {
            List<String[]> rows = reader.readAll();
            return rows != null ? rows : new ArrayList<>();
        } catch (Exception e) {
            // archivo puede no existir; retornamos lista vacía
            return new ArrayList<>();
        }
    }

    /**
     * Fuerza recarga desde disco.
     */
    public static void reload() {
        professors = loadProfessors();
    }

    /**
     * Limpia solo la cache en memoria. No borra archivos en disco (más seguro).
     * Para tests automatizados que necesiten resetear archivos, usar clearFilesForTests().
     */
    public static void clearCache() {
        professors = new ArrayList<>();
    }

    /**
     * BORRA los archivos físicos (USAR SOLO EN TESTS).
     */
    public static void clearFilesForTests() {
        try {
            Files.deleteIfExists(new File(PROFESSOR_FILE_PATH).toPath());
        } catch (Exception e) {
            System.out.println("No se pudo limpiar archivo de profesores (tests): " + e.getMessage());
        }
    }

    // -------------------- CRUD --------------------

    /**
     * Devuelve copia de la lista raw (para evitar modificaciones externas).
     */
    public static List<String[]> getProfessors() {
        return new ArrayList<>(professors);
    }

    /**
     * Crea un nuevo profesor. Añade registro en professorCSV y usersCSV.
     */
    public static boolean createProfessor(String user, String password,
                                          String firstName, String lastName, String birthDate) {

        // validar campos mínimos
        if (user == null || user.isBlank()) {
            System.out.println("ERROR: username inválido.");
            return false;
        }

        // recargar para evitar race conditions
        reload();

        // evitar duplicados por username
        if (findProfessor(user) != null) {
            System.out.println("ERROR: Ese usuario ya existe como profesor.");
            return false;
        }

        // crear id único
        CodeNumbersManager idManager = new CodeNumbersManager();
        long id = idManager.createNewId();

        // construir fila profesor: user,password,role,id,firstName,lastName,birthDate
        String[] profRow = new String[] {
            user,
            password,
            "professor",
            String.valueOf(id),
            firstName == null ? "" : firstName,
            lastName == null ? "" : lastName,
            birthDate == null ? "" : birthDate
        };

        // añadir en memoria
        professors.add(profRow);

        // persistir profesor CSV
        if (!saveProfessorsCSV()) {
            System.out.println("ERROR: No se pudo guardar profesor en CSV.");
            return false;
        }

        // actualizar usersCSV (solo la fila del usuario)
        if (!appendOrUpdateUserRow(user, password, "professor")) {
            System.out.println("WARNING: profesor creado pero fallo al actualizar usersCSV.");
        }

        System.out.println("Profesor creado correctamente: " + user);
        return true;
    }

    /**
     * Elimina profesor por username (borra de professors y actualiza usersCSV).
     */
    public static boolean deleteProfessor(String user) {
        reload();
        int idx = -1;
        for (int i = 0; i < professors.size(); i++) {
            String[] row = professors.get(i);
            if (row.length > 0 && row[0].equals(user)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            System.out.println("No existe el profesor.");
            return false;
        }
        professors.remove(idx);

        if (!saveProfessorsCSV()) {
            System.out.println("ERROR: No se pudo actualizar profesorCSV al eliminar.");
            return false;
        }

        // También eliminar de usersCSV si existe (mantener sincronía)
        removeUserFromUsersCSV(user);

        System.out.println("Profesor eliminado correctamente: " + user);
        return true;
    }

    /**
     * Encuentra y construye un objeto Professor a partir del CSV.
     * Retorna null si no existe o fila corrupta.
     */
    public static Professor findProfessor(String username) {
        // trabajar sobre cache actual (no recarga automática)
        for (String[] row : professors) {
            if (row.length > 0 && row[0].equals(username)) {
                try {
                    String user = row[0];
                    String pass = row.length > 1 ? row[1] : "";
                    // row[2] es role (debe ser "professor")
                    long id = row.length > 3 && !row[3].isBlank() ? Long.parseLong(row[3]) : -1L;
                    String first = row.length > 4 ? row[4] : "";
                    String last = row.length > 5 ? row[5] : "";
                    String birth = row.length > 6 ? row[6] : "";

                    // construir objeto (asumiendo constructor Professor(user, pass, id, first, last, birth))
                    return new Professor(user, pass, id, first, last, birth);
                } catch (Exception e) {
                    // fila corrupta -> ignorar
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Imprime info resumida de un profesor (si existe).
     */
    public static void printFindProfessor(String username) {
        Professor p = findProfessor(username);
        if (p == null) {
            System.out.println("Profesor no encontrado: " + username);
            return;
        }
        System.out.println(Arrays.toString(p.toArray()));
    }

    /**
     * Actualiza datos básicos del profesor (nombre, apellido, birthDate, password opcional).
     * Si quieres actualizar campos más complejos, amplía este método.
     */
    public static boolean updateProfessor(String username, String newPassword,
                                          String newFirst, String newLast, String newBirthDate) {
        reload();
        boolean updated = false;
        for (int i = 0; i < professors.size(); i++) {
            String[] row = professors.get(i);
            if (row.length > 0 && row[0].equals(username)) {
                // asegurar tamaño mínimo
                if (row.length < 7) row = Arrays.copyOf(row, 7);
                if (newPassword != null) row[1] = newPassword;
                if (newFirst != null) row[4] = newFirst;
                if (newLast != null) row[5] = newLast;
                if (newBirthDate != null) row[6] = newBirthDate;
                professors.set(i, row);
                updated = true;
                break;
            }
        }
        if (!updated) {
            System.out.println("No existe el profesor.");
            return false;
        }
        if (!saveProfessorsCSV()) {
            System.out.println("ERROR: No se pudo guardar cambios en professorCSV.");
            return false;
        }
        // también actualizar usersCSV (si cambió la contraseña)
        if (newPassword != null) appendOrUpdateUserRow(username, newPassword, "professor");
        System.out.println("Profesor actualizado.");
        return true;
    }

    /**
     * Lista todas las filas de profesores (raw).
     */
    public static void listProfessors() {
        if (professors.isEmpty()) {
            System.out.println("No hay profesores registrados.");
            return;
        }
        for (String[] row : professors) {
            System.out.println(String.join(", ", normalizeRow(row, 7)));
        }
    }

    // -------------------- CSV Helpers --------------------

    /**
     * Guarda la lista 'professors' en professorCSV de forma segura (tmp -> replace).
     */
    private static boolean saveProfessorsCSV() {
        File original = new File(PROFESSOR_FILE_PATH);
        File tmp = new File(PROFESSOR_FILE_PATH + ".tmp");

        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] row : professors) {
                if (row == null) continue;
                String[] safe = normalizeRow(row, 7);
                writer.writeNext(safe);
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Normaliza una fila al tamaño esperado (cols) llenando con "" si es necesario.
     */
    private static String[] normalizeRow(String[] row, int cols) {
        String[] res = Arrays.copyOf(row, Math.max(row.length, cols));
        for (int i = 0; i < res.length; i++) if (res[i] == null) res[i] = "";
        return res;
    }

    // -------------------- usersCSV Helpers --------------------

    /**
     * Agrega o actualiza la fila del usuario en usersCSV (user,password,role).
     * Si la fila existe, la actualiza; si no existe, la agrega.
     */
    private static boolean appendOrUpdateUserRow(String username, String password, String role) {
        // leer usersCSV
        List<String[]> allUsers = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
            List<String[]> r = reader.readAll();
            if (r != null) allUsers = r;
        } catch (Exception e) {
            // si el archivo no existe, lo creamos luego
            allUsers = new ArrayList<>();
        }

        boolean found = false;
        for (int i = 0; i < allUsers.size(); i++) {
            String[] u = allUsers.get(i);
            if (u.length > 0 && u[0].equals(username)) {
                // actualizar (asegurar 3 cols)
                allUsers.set(i, new String[] { username, password, role });
                found = true;
                break;
            }
        }

        if (!found) {
            allUsers.add(new String[] { username, password, role });
        }

        // escribir de vuelta
        File original = new File(USER_FILE_PATH);
        File tmp = new File(USER_FILE_PATH + ".tmp");

        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] u : allUsers) {
                String[] safe = normalizeRow(u, 3);
                writer.writeNext(safe);
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Elimina la fila del usuario del usersCSV si existe.
     */
    private static void removeUserFromUsersCSV(String username) {
        List<String[]> allUsers = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(USER_FILE_PATH))) {
            List<String[]> r = reader.readAll();
            if (r != null) allUsers = r;
        } catch (Exception e) {
            return; // nothing to do
        }

        boolean changed = false;
        for (int i = 0; i < allUsers.size(); i++) {
            String[] u = allUsers.get(i);
            if (u.length > 0 && u[0].equals(username)) {
                allUsers.remove(i);
                changed = true;
                break;
            }
        }

        if (!changed) return;

        File original = new File(USER_FILE_PATH);
        File tmp = new File(USER_FILE_PATH + ".tmp");

        try (CSVWriter writer = new CSVWriter(new FileWriter(tmp))) {
            for (String[] u : allUsers) {
                writer.writeNext(normalizeRow(u, 3));
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            try {
                Files.move(tmp.toPath(), original.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
