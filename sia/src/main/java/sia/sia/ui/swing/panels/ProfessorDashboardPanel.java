/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing.panels;

import sia.sia.ui.swing.SIAFrame;
import javax.swing.*;
import java.awt.*;

public class ProfessorDashboardPanel extends JPanel {
    private SIAFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public ProfessorDashboardPanel(SIAFrame parent) {
        this.parentFrame = parent;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        initHeader();
        initSidebar();
        initContentArea();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Panel del Profesor", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.addActionListener(e -> parentFrame.showCard("LOGIN"));
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initSidebar() {
        JPanel sidebarPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] menuItems = {
            "Mis Grupos",
            "Registrar Calificaciones", 
            "Ver Calificaciones",
            "Horarios de Clase",
            "" // Espaciador
        };
        
        for (String item : menuItems) {
            if (item.isEmpty()) {
                sidebarPanel.add(new JLabel());
            } else {
                JButton button = new JButton(item);
                button.addActionListener(e -> showContent(item));
                sidebarPanel.add(button);
            }
        }
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void initContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.add(new JLabel("Bienvenido Profesor", JLabel.CENTER));
        contentPanel.add(welcomePanel, "WELCOME");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showContent(String menuItem) {
        JPanel tempPanel = new JPanel(new BorderLayout());
        tempPanel.add(new JLabel(menuItem + " - En construcción", JLabel.CENTER));
        contentPanel.add(tempPanel, menuItem);
        cardLayout.show(contentPanel, menuItem);
    }
}