package sia.sia.ui;

import java.awt.Color;

/**
 * Colores centralizados para replicar el estilo institucional del SIA
 */
public class UIColors {

    // === Colores base del diseño ===

    // Fondo general (beige institucional)
    public static final Color BACKGROUND = new Color(210, 200, 170);

    // Encabezado amarillo fuerte (barra superior)
    public static final Color HEADER = new Color(246, 194, 57);

    // Barra secundaria gris-verdosa
    public static final Color SUBHEADER = new Color(180, 170, 120);

    // Panel de contenido (amarillo suave)
    public static final Color PANEL_SOFT = new Color(248, 232, 182);

    // Borde clásico de recuadros en SIA
    public static final Color PANEL_BORDER = new Color(180, 170, 140);


    // === Botones ===

    // Botón principal (mismo amarillo del encabezado)
    public static final Color BUTTON_PRIMARY = HEADER;

    // Texto del botón principal (negro por contraste real del SIA)
    public static final Color BUTTON_PRIMARY_TEXT = Color.BLACK;

    // Hover del botón (un poco más oscuro)
    public static final Color BUTTON_PRIMARY_HOVER = new Color(222, 170, 40);

    // Presionado (oscuro fuerte)
    public static final Color BUTTON_PRIMARY_PRESSED = new Color(190, 145, 30);


    // === Texto ===

    // Texto general
    public static final Color TEXT = new Color(60, 60, 60);

    // Texto destacado o labels
    public static final Color LABEL_TEXT = Color.BLACK;


    // === Utilidades ===

    // Transparencia para paneles o overlays
    public static Color transparent(int alpha) {
        return new Color(255, 255, 255, alpha);
    }
}

