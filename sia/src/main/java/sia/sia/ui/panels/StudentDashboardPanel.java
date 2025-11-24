/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels;

import sia.sia.ui.SIAFrame;
import sia.sia.ui.panels.student.*;
import javax.swing.*;
import java.awt.*;

public class StudentDashboardPanel extends JPanel {
    private SIAFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String currentUser;
    
    public StudentDashboardPanel(SIAFrame parent) {
        this.parentFrame = parent;
        this.currentUser = "";
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
        JLabel titleLabel = new JLabel("Panel del Estudiante", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.addActionListener(e -> parentFrame.showCard("LOGIN"));
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initSidebar() {
        JPanel sidebarPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] menuItems = {
            "Historial Académico",
            "Inscripciones Actuales",
            "Inscribir Materia",
            "Retirar Materia",
            "Cursos Disponibles",
            "Mi Horario",
            "Promedio General",
            "Créditos Aprobados"
        };
        
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.addActionListener(e -> showContent(item));
            sidebarPanel.add(button);
        }
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void initContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.add(new JLabel("Seleccione una opción del menú", JLabel.CENTER));
        contentPanel.add(welcomePanel, "WELCOME");
        
        contentPanel.add(new StudentAcademicHistoryPanel(currentUser), "Historial Académico");
        contentPanel.add(new StudentCurrentEnrollmentsPanel(currentUser), "Inscripciones Actuales");
        contentPanel.add(new StudentEnrollCoursePanel(currentUser), "Inscribir Materia");
        contentPanel.add(new StudentWithdrawCoursePanel(currentUser), "Retirar Materia");
        contentPanel.add(new StudentAvailableCoursesPanel(currentUser), "Cursos Disponibles");
        contentPanel.add(new StudentSchedulePanel(currentUser), "Mi Horario");
        contentPanel.add(new StudentGradeAveragePanel(currentUser), "Promedio General");
        contentPanel.add(new StudentApprovedCreditsPanel(currentUser), "Créditos Aprobados");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showContent(String menuItem) {
        cardLayout.show(contentPanel, menuItem);
    }
}
