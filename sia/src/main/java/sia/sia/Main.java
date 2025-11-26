/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package sia.sia;

import sia.sia.ui.SIAFrame;
import javax.swing.*;

/**
 * Clase principal del Sistema de Informacion Academico
 * Ejecuta la interfaz Swing
 * @author luzel
 */
public class Main {

    public static void main(String[] args) {
        // Ejecutar en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            SIAFrame frame = new SIAFrame();
            frame.setVisible(true);
        });
    }
}
