package sia.sia.ui;

import java.awt.Color;

/**
 * Clase con los colores de la interfaz según el diseño
 */
public class UIColors {
    // Fondo general: azul claro
    public static final Color BACKGROUND = new Color(168, 200, 220);
    
    // Botón primario: azul oscuro
    public static final Color BUTTON = new Color(46, 91, 127);
    
    // Botón secundario: beige
    public static final Color BUTTON_SECONDARY = new Color(230, 213, 186);
    
    // Texto de botón primario: blanco
    public static final Color BUTTON_PRIMARY_TEXT = Color.WHITE;
    
    // Texto de botón secundario: azul oscuro
    public static final Color BUTTON_SECONDARY_TEXT = new Color(46, 91, 127);
    
    // Fondo de formularios: blanco
    public static final Color FORM_BACKGROUND = new Color(255, 255, 255);
    
    // Texto de labels: gris azulado oscuro (mantenido para buena legibilidad)
    public static final Color LABEL_TEXT = new Color(44, 62, 80);
    
    // Colores adicionales derivados
    public static final Color BUTTON_HOVER = new Color(30, 70, 110);  // Azul más oscuro
    public static final Color BUTTON_PRESSED = new Color(20, 50, 80); // Azul más oscuro aún
    
    // Panel interno con transparencia (cuando se necesite)
    public static Color getTransparentPanel() {
        return new Color(255, 255, 255, 200); // Blanco con transparencia
    }
}
