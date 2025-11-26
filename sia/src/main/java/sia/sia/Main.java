/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package sia.sia;

import java.util.Scanner;
import sia.sia.business.CSVManager;
import sia.sia.data.User;
import sia.sia.ui.AdminMenu;
import sia.sia.ui.StudentMenu;
import sia.sia.ui.ProfessorMenu;

/**
 * Clase principal del Sistema de Informacion Academico
 * Maneja el login y redireccion a los diferentes menus
 * @author luzel
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        showWelcomeBanner();
        
        boolean running = true;
        
        while (running) {
            showMainMenu();
            
            try {
                int option = Integer.parseInt(scanner.nextLine().trim());
                
                switch (option) {
                    case 1 -> {
                        if (login()) {
                            // Si el login es exitoso, manejar el menú del usuario
                            running = handleUserMenu();
                        }
                    }
                    case 2 -> register();
                    case 3 -> showAbout();
                    case 0 -> {
                        showGoodbye();
                        running = false;
                    }
                    default -> System.out.println("\nOpcion invalida. Intente nuevamente.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("\nPor favor ingrese un numero valido.");
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }
        }
        
        scanner.close();
    }

    private static boolean handleUserMenu() {
        boolean userLoggedIn = true;
        
        while (userLoggedIn) {
            boolean menuResult = redirectToMenu(currentUser.getUser());
            
            if (!menuResult) {
                // El usuario decidió cerrar sesión
                userLoggedIn = false;
                currentUser = null;
                System.out.println("\nSesion cerrada exitosamente.");
            }
        }
        
        return true; // Continuar con el menú principal
    }

    private static void showWelcomeBanner() {
        System.out.println("\n==================================================");
        System.out.println("                                                  ");
        System.out.println("     SISTEMA DE INFORMACION ACADEMICO - SIA      ");
        System.out.println("     Universidad Nacional de Colombia            ");
        System.out.println("                                                  ");
        System.out.println("==================================================");
        System.out.println("            Version 1.0 - 2025                    ");
        System.out.println("==================================================\n");
    }

    private static void showMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("            MENU PRINCIPAL");
        System.out.println("==========================================");
        System.out.println("1. Iniciar Sesion");
        System.out.println("2. Registrarse");
        System.out.println("3. Acerca del Sistema");
        System.out.println("0. Salir");
        System.out.println("==========================================");
        System.out.print("\nSeleccione una opcion: ");
    }

    private static boolean login() {
        System.out.println("\n==========================================");
        System.out.println("           INICIAR SESION");
        System.out.println("==========================================");
        
        System.out.print("Usuario: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("\nEl usuario no puede estar vacio.");
            return false;
        }
        
        System.out.print("Contrasena: ");
        String password = scanner.nextLine().trim();
        
        if (password.isEmpty()) {
            System.out.println("\nLa contrasena no puede estar vacia.");
            return false;
        }
        
        // Intentar login
        currentUser = CSVManager.login(username, password);
        
        if (currentUser != null) {
            System.out.println("\n==========================================");
            System.out.println("  LOGIN EXITOSO");
            System.out.println("==========================================");
            System.out.println("Bienvenido: " + username);
            System.out.println("Rol: " + currentUser.getRole());
            System.out.println("==========================================\n");
            
            // Pausar antes de mostrar menu
            System.out.println("Presione Enter para continuar...");
            scanner.nextLine();
            
            return true;
            
        } else {
            System.out.println("\n==========================================");
            System.out.println("  ERROR DE AUTENTICACION");
            System.out.println("==========================================");
            System.out.println("Usuario o contrasena incorrectos.");
            System.out.println("Por favor intente nuevamente.");
            System.out.println("==========================================\n");
            return false;
        }
    }

    private static void register() {
        System.out.println("\n==========================================");
        System.out.println("           REGISTRO DE USUARIO");
        System.out.println("==========================================");
        
        System.out.print("Nuevo usuario: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("\nEl usuario no puede estar vacio.");
            return;
        }
        
        if (username.length() < 4) {
            System.out.println("\nEl usuario debe tener al menos 4 caracteres.");
            return;
        }
        
        System.out.print("Contrasena: ");
        String password = scanner.nextLine().trim();
        
        if (password.isEmpty()) {
            System.out.println("\nLa contrasena no puede estar vacia.");
            return;
        }
        
        if (password.length() < 6) {
            System.out.println("\nLa contrasena debe tener al menos 6 caracteres.");
            return;
        }
        
        System.out.print("Confirmar contrasena: ");
        String confirmPassword = scanner.nextLine().trim();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("\nLas contrasenas no coinciden.");
            return;
        }
        
        System.out.println("\nSeleccione el tipo de usuario:");
        System.out.println("1. Estudiante");
        System.out.println("2. Profesor");
        System.out.println("3. Administrador");
        System.out.print("\nOpcion: ");
        
        String role;
        try {
            int roleOption = Integer.parseInt(scanner.nextLine().trim());
            
            switch (roleOption) {
                case 1 -> role = "student";
                case 2 -> role = "professor";
                case 3 -> role = "admin";
                default -> {
                    System.out.println("\nOpcion invalida.");
                    return;
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("\nOpcion invalida.");
            return;
        }
        
        // Intentar registrar
        boolean success = CSVManager.signUp(username, password, role);
        
        if (success) {
            System.out.println("\n==========================================");
            System.out.println("  REGISTRO EXITOSO");
            System.out.println("==========================================");
            System.out.println("Usuario: " + username);
            System.out.println("Rol: " + role);
            System.out.println("\nYa puede iniciar sesion con sus credenciales.");
            System.out.println("==========================================\n");
        } else {
            System.out.println("\n==========================================");
            System.out.println("  ERROR DE REGISTRO");
            System.out.println("==========================================");
            System.out.println("El usuario ya existe.");
            System.out.println("Por favor intente con otro nombre de usuario.");
            System.out.println("==========================================\n");
        }
    }

    private static boolean redirectToMenu(String username) {
        String role = currentUser.getRole();
        
        switch (role.toLowerCase()) {
            case "admin" -> {
                return AdminMenu.show(username);
            }
            case "student" -> {
                return StudentMenu.show(username);
            }
            case "professor" -> {
                return ProfessorMenu.show(username);
            }
            default -> {
                System.out.println("\nRol no reconocido: " + role);
                System.out.println("Contacte al administrador del sistema.");
                return false; // Cerrar sesión
            }
        }
    }

    private static void showAbout() {
        System.out.println("\n==================================================");
        System.out.println("         ACERCA DEL SISTEMA");
        System.out.println("==================================================");
        System.out.println();
        System.out.println("Sistema de Informacion Academico (SIA)");
        System.out.println("Universidad Nacional de Colombia");
        System.out.println();
        System.out.println("Version: 1.0");
        System.out.println("Ano: 2025");
        System.out.println();
        System.out.println("FUNCIONALIDADES:");
        System.out.println("  - Gestion de estudiantes, profesores y cursos");
        System.out.println("  - Inscripcion de materias con validaciones");
        System.out.println("  - Registro y consulta de calificaciones");
        System.out.println("  - Historial academico completo");
        System.out.println("  - Control de horarios y cupos");
        System.out.println("  - Validacion de requisitos y creditos");
        System.out.println();
        System.out.println("TIPOS DE USUARIO:");
        System.out.println("  - Administrador: Gestion completa del sistema");
        System.out.println("  - Profesor: Registro de notas y gestion de grupos");
        System.out.println("  - Estudiante: Inscripcion y consulta academica");
        System.out.println();
        System.out.println("==================================================\n");
    }

    private static void showGoodbye() {
        System.out.println("\n==================================================");
        System.out.println("                                                  ");
        System.out.println("    Gracias por usar el Sistema SIA               ");
        System.out.println("    Universidad Nacional de Colombia              ");
        System.out.println("                                                  ");
        System.out.println("           Hasta pronto!                          ");
        System.out.println("                                                  ");
        System.out.println("==================================================\n");
    }

    /**
     * Metodo auxiliar para limpiar la pantalla (opcional)
     * Funciona en algunos terminales
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Si falla, simplemente imprime lineas vacias
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Metodo para testing - iniciar sesion directa sin UI
     */
    public static void loginDirect(String username, String password) {
        currentUser = CSVManager.login(username, password);
        if (currentUser != null) {
            redirectToMenu(username);
        } else {
            System.out.println("Error de autenticacion");
        }
    }

    /**
     * Metodo para testing - obtener usuario actual
     */
    public static User getCurrentUser() {
        return currentUser;
    }
}