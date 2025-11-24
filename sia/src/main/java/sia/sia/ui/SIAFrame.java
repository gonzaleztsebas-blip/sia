/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui;
import sia.sia.ui.panels.*;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author ASUS
 */
public class SIAFrame extends JFrame{
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private AdminDashboardPanel adminPanel;
    private StudentDashboardPanel studentPanel;
    private ProfessorDashboardPanel professorPanel;
    
    public SIAFrame(){
        setTitle("Sistema de Información Académica - SIA");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(new LoginPanel(this), "LOGIN");
        adminPanel = new AdminDashboardPanel(this);
        mainPanel.add(adminPanel, "ADMIN_DASHBOARD");
        studentPanel = new StudentDashboardPanel(this);
        mainPanel.add(studentPanel, "STUDENT_DASHBOARD");
        professorPanel = new ProfessorDashboardPanel(this);
        mainPanel.add(professorPanel, "PROFESSOR_DASHBOARD");
        
        add(mainPanel);
        
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void showCard(String cardName, String username) {
        // Actualizar usuario en los paneles correspondientes
        if ("STUDENT_DASHBOARD".equals(cardName)) {
            studentPanel.setCurrentUser(username);
        } else if ("PROFESSOR_DASHBOARD".equals(cardName)) {
            professorPanel.setCurrentUser(username);
        }
        cardLayout.show(mainPanel, cardName);
    }
    
    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }
}
