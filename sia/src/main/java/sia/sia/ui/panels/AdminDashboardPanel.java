/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels;

import sia.sia.ui.SIAFrame;
import sia.sia.ui.UIColors;
import sia.sia.ui.panels.admin.*;
import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private SIAFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    public AdminDashboardPanel(SIAFrame parent) {
        this.parentFrame = parent;
        setBackground(UIColors.BACKGROUND);
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
        headerPanel.setBackground(UIColors.FORM_BACKGROUND);
        
        JLabel titleLabel = new JLabel("Panel de Administración", JLabel.CENTER);
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
        JPanel sidebarPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setBackground(UIColors.BACKGROUND);
        
        String[] menuItems = {
            "Gestión Estudiantes",
            "Gestión Profesores", 
            "Gestión Cursos",
            "Gestión Grupos",
            "Gestión Inscripciones",
            "Gestión Calificaciones",
            "" // Espaciador
        };
        
        for (String item : menuItems) {
            if (item.isEmpty()) {
                sidebarPanel.add(new JLabel()); // Espaciador
            } else {
                JButton button = new JButton(item);
                button.setBackground(UIColors.BUTTON);
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Arial", Font.BOLD, 11));
                button.setFocusPainted(false);
                button.addActionListener(e -> showContent(item));
                sidebarPanel.add(button);
            }
        }
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void initContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIColors.FORM_BACKGROUND);
        
        // Panel de bienvenida inicial
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.add(new JLabel("Seleccione una opción del menú", JLabel.CENTER));
        contentPanel.add(welcomePanel, "WELCOME");
        
        add(contentPanel, BorderLayout.CENTER);
        
        contentPanel.add(new StudentManagementPanel(), "Gestión Estudiantes");
        contentPanel.add(new ProfessorManagementPanel(), "Gestión Profesores");
        contentPanel.add(new CourseManagementPanel(), "Gestión Cursos");
        contentPanel.add(new GroupManagementPanel(), "Gestión Grupos");
        contentPanel.add(new EnrollmentManagementPanel(), "Gestión Inscripciones");
        contentPanel.add(new GradeManagementPanel(), "Gestión Calificaciones");
    }
    
    private void showContent(String menuItem) {
        // En lugar de crear un panel temporal, mostrar el que ya está registrado
        cardLayout.show(contentPanel, menuItem);
    }
}
