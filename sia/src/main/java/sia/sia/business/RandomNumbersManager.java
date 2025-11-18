package sia.sia.data;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomNumbersManager {

    private Set<Long> ids;
    private Set<Long> codes;  // almacena todos los IDs ya usados
    private Random random;

    public RandomNumbersManager() {
        ids = new HashSet<>();
        random = new Random();
    }

    public long createNewId() {
        long newId;

        do {
            newId = 10000 + random.nextInt(90000); // ID entre 10000 y 99999
        } while (ids.contains(newId)); // repetir si ya existe

        ids.add(newId);
        return newId;
    }
    
    public long createNewCode() {
        long newCode;

        do {
            newCode = 10000 + random.nextInt(90000); // ID entre 10000 y 99999
        } while (ids.contains(newCode)); // repetir si ya existe

        ids.add(newCode);
        return newCode;
    }
}

