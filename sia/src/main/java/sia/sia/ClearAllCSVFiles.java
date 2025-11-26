package sia.sia;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.FileWriter;
import java.io.IOException;

public class ClearAllCSVFiles {

    private static final String[] CSV_FILES = {
        "src/main/resources/dataBase/adminCSV.csv",
        "src/main/resources/dataBase/codesGenerated.txt",
        "src/main/resources/dataBase/courseCSV.csv",
        "src/main/resources/dataBase/enrollmentCSV.csv",
        "src/main/resources/dataBase/gradeCSV.csv",
        "src/main/resources/dataBase/groupCSV.csv",
        "src/main/resources/dataBase/groupsGenerated.txt",
        "src/main/resources/dataBase/idsGenerated.txt",
        "src/main/resources/dataBase/professorCSV.csv",
        "src/main/resources/dataBase/studentCSV.csv",
        "src/main/resources/dataBase/usersCSV.csv"
    };

    public static void main(String[] args) {
        System.out.println("=== LIMPIEZA DE TODOS LOS ARCHIVOS CSV ===\n");

        int successCount = 0;
        int failCount = 0;

        for (String filePath : CSV_FILES) {
            try (FileWriter fw = new FileWriter(filePath)) {
                fw.write(""); // Vaciar el archivo
                System.out.println("✓ Limpiado: " + filePath);
                successCount++;
            } catch (IOException e) {
                System.out.println("✗ Error al limpiar: " + filePath);
                System.out.println("  Razón: " + e.getMessage());
                failCount++;
            }
        }

        System.out.println("\n=== RESUMEN ===");
        System.out.println("Archivos limpiados exitosamente: " + successCount);
        System.out.println("Archivos con error: " + failCount);
        System.out.println("\n✓ Proceso completado.");
    }
}