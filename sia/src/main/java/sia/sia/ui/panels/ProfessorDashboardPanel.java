/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels;

import sia.sia.ui.SIAFrame;
import sia.sia.ui.UIColors;
import sia.sia.ui.panels.professor.*;
import javax.swing.*;
import java.awt.*;

public class ProfessorDashboardPanel extends JPanel {
    private SIAFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String currentUser;
    
    public ProfessorDashboardPanel(SIAFrame parent) {
        this.parentFrame = parent;
        this.currentUser = "";
        setBackground(UIColors.BACKGROUND);
        initComponents();
    }
    
    public void setCurrentUser(String username) {
        this.currentUser = username;
        // Recreate content panels with the new user
        if (contentPanel != null) {
            contentPanel.removeAll();
            initContentArea();
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        initHeader();
        initSidebar();
        initContentArea();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIColors.FORM_BACKGROUND);
        JLabel titleLabel = new JLabel("Panel del Profesor", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(UIColors.LABEL_TEXT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.setBackground(UIColors.BUTTON);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 11));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> parentFrame.showCard("LOGIN"));
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initSidebar() {
        JPanel sidebarPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setBackground(UIColors.BACKGROUND);
        
        String[] menuItems = {
            "Mis Grupos",
            "Detalles de Grupo",
            "Estudiantes del Grupo",
            "Registrar Calificación",
            "Actualizar Calificación",
            "Ver Calificaciones",
            "Promedio del Grupo",
            "Horario del Grupo"
        };
        
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setBackground(UIColors.BUTTON);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 11));
            button.setFocusPainted(false);
            button.addActionListener(e -> showContent(item));
            sidebarPanel.add(button);
        }
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void initContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIColors.FORM_BACKGROUND);
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.add(new JLabel("Seleccione una opción del menú", JLabel.CENTER));
        contentPanel.add(welcomePanel, "WELCOME");
        
        contentPanel.add(new ProfessorMyGroupsPanel(currentUser), "Mis Grupos");
        contentPanel.add(new ProfessorGroupDetailsPanel(currentUser), "Detalles de Grupo");
        contentPanel.add(new ProfessorGroupStudentsPanel(currentUser), "Estudiantes del Grupo");
        contentPanel.add(new ProfessorRegisterGradePanel(currentUser), "Registrar Calificación");
        contentPanel.add(new ProfessorUpdateGradePanel(currentUser), "Actualizar Calificación");
        contentPanel.add(new ProfessorViewGradesPanel(currentUser), "Ver Calificaciones");
        contentPanel.add(new ProfessorGroupAveragePanel(currentUser), "Promedio del Grupo");
        contentPanel.add(new ProfessorGroupSchedulePanel(currentUser), "Horario del Grupo");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showContent(String menuItem) {
        cardLayout.show(contentPanel, menuItem);
    }
}
