package sia.sia.data;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Gestiona la generación de IDs, códigos y números de grupo únicos
 * Persiste los valores generados para evitar duplicados
 * @author luzel
 */
public class CodeNumbersManager {
    
    private static final String IDS_FILE = "src\\main\\resources\\dataBase\\idsGenerated.txt";
    private static final String CODES_FILE = "src\\main\\resources\\dataBase\\codesGenerated.txt";
    private static final String GROUPS_FILE = "src\\main\\resources\\dataBase\\groupsGenerated.txt";
    
    private Set<Long> ids;
    private Set<Long> codes;
    private Set<Long> groupsNumbers;
    private Random random;
    
    public CodeNumbersManager() {
        ids = new HashSet<>();
        codes = new HashSet<>();
        groupsNumbers = new HashSet<>();
        random = new Random();
        
        // Cargar IDs existentes al inicializar
        loadIds();
        loadCodes();
        loadGroupNumbers();
    }
    
    /**
     * Carga los IDs desde el archivo de persistencia
     */
    private void loadIds() {
        File file = new File(IDS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    ids.add(Long.parseLong(line.trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los codigos desde el archivo de persistencia
     */
    private void loadCodes() {
        File file = new File(CODES_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    codes.add(Long.parseLong(line.trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Carga los numeros de grupo desde el archivo de persistencia
     */
    private void loadGroupNumbers() {
        File file = new File(GROUPS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    groupsNumbers.add(Long.parseLong(line.trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda un ID en el archivo de persistencia
     */
    private void saveId(long id) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(IDS_FILE, true))) {
            writer.write(String.valueOf(id));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda un codigo en el archivo de persistencia
     */
    private void saveCode(long code) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CODES_FILE, true))) {
            writer.write(String.valueOf(code));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda un numero de grupo en el archivo de persistencia
     */
    private void saveGroupNumber(long number) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GROUPS_FILE, true))) {
            writer.write(String.valueOf(number));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Genera un nuevo ID único para estudiantes/profesores/admins
     * @return ID único entre 10000 y 99999
     */
    public long createNewId() {
        long newId;
        do {
            newId = 10000 + random.nextInt(90000); // ID entre 10000 y 99999
        } while (ids.contains(newId)); // repetir si ya existe
        
        ids.add(newId);
        saveId(newId);
        return newId;
    }
    
    /**
     * Genera un nuevo código unico para cursos
     * @return Código único entre 10000 y 99999
     */
    public long createNewCode() {
        long newCode;
        do {
            newCode = 10000 + random.nextInt(90000); // Codigo entre 10000 y 99999
        } while (codes.contains(newCode)); // repetir si ya existe
        
        codes.add(newCode);
        saveCode(newCode);
        return newCode;
    }
    
    /**
     * Genera un nuevo número de grupo secuencial
     * @return Número de grupo consecutivo (1, 2, 3, ...)
     */
    public long createNewGroupNumber() {
        long next = 1;
        
        // Encontrar el siguiente número disponible
        while (groupsNumbers.contains(next)) {
            next++;
        }
        
        // Guardar el nuevo número
        groupsNumbers.add(next);
        saveGroupNumber(next);
        return next;
    }
    
    /**
     * Verifica si un ID ya existe
     */
    public boolean idExists(long id) {
        return ids.contains(id);
    }
    
    /**
     * Verifica si un código ya existe
     */
    public boolean codeExists(long code) {
        return codes.contains(code);
    }
    
    /**
     * Verifica si un número de grupo ya existe
     */
    public boolean groupNumberExists(long number) {
        return groupsNumbers.contains(number);
    }
}