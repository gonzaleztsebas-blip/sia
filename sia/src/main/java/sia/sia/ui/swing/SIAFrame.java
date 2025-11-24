/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing;
import sia.sia.ui.swing.panels.*;
import javax.swing.*;
import java.awt.*;
import sia.sia.data.User;

/**
 *
 * @author ASUS
 */
public class SIAFrame extends JFrame{
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;
    
    public SIAFrame(){
        setTitle("Sistema de Información Académica - SIA");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(new AdminDashboardPanel(this), "ADMIN_DASHBOARD");
        mainPanel.add(new StudentDashboardPanel(this), "STUDENT_DASHBOARD");
        mainPanel.add(new ProfessorDashboardPanel(this), "PROFESSOR_DASHBOARD");
        
        add(mainPanel);
        
        cardLayout.show(mainPanel, "ADMIN_DASHBOARD");
    }
    
    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }
}
