/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing;
import javax.swing.*;

/**
 *
 * @author ASUS
 */
public class TestLogin {
     public static void main(String[] args) {
        // Ejecutar en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            SIAFrame frame = new SIAFrame();
            frame.setVisible(true);
        });
    }
}
