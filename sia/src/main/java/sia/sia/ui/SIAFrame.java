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
    private JPanel headerPanel;
    private AdminDashboardPanel adminPanel;
    private StudentDashboardPanel studentPanel;
    private ProfessorDashboardPanel professorPanel;
    private JLabel userLabel;
    
    public SIAFrame(){
        setTitle("Sistema de Información Académica - SIA");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        // Aplicar estilos globales por defecto para componentes
        UIManager.put("Button.background", UIColors.BUTTON_PRIMARY);
        UIManager.put("Button.foreground", UIColors.BUTTON_PRIMARY_TEXT);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("TextArea.foreground", UIColors.TEXT);
        
        // Crear header
        headerPanel = createHeaderPanel();
        
        // Crear panel principal con CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color (245, 245, 235));
        
        mainPanel.add(new LoginPanel(this), "LOGIN");
        adminPanel = new AdminDashboardPanel(this);
        mainPanel.add(adminPanel, "ADMIN_DASHBOARD");
        studentPanel = new StudentDashboardPanel(this);
        mainPanel.add(studentPanel, "STUDENT_DASHBOARD");
        professorPanel = new ProfessorDashboardPanel(this);
        mainPanel.add(professorPanel, "PROFESSOR_DASHBOARD");
        
        // Configurar frame
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        setBackground(new Color(245, 245, 235));
        
        cardLayout.show(mainPanel, "LOGIN");
        headerPanel.setVisible(false);
    }
    
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 193, 7)); // Amarillo dorado
        header.setPreferredSize(new Dimension(1200, 80));
        header.setBorder(BorderFactory.createLineBorder(new Color(178, 153, 0), 2));
        
        // Panel izquierdo con logo y título
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(255, 193, 7));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Sistema de Información Académica");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(80, 70, 0));
        
        leftPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel derecho con usuario
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(255, 193, 7));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));
        
        userLabel = new JLabel("Usuario: --");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(new Color(80, 70, 0));
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        rightPanel.add(userLabel);
        
        return header;
    }
    
    public void showCard(String cardName, String username) {
        // Actualizar usuario en los paneles correspondientes
        if ("STUDENT_DASHBOARD".equals(cardName)) {
            studentPanel.setCurrentUser(username);
        } else if ("PROFESSOR_DASHBOARD".equals(cardName)) {
            professorPanel.setCurrentUser(username);
        }
        
        // Actualizar header
        if ("LOGIN".equals(cardName)) {
            headerPanel.setVisible(false);
            userLabel.setText("Usuario: --");
        } else {
            headerPanel.setVisible(true);
            userLabel.setText("Usuario: " + username);
        }
        
        cardLayout.show(mainPanel, cardName);
    }
    
    public void showCard(String cardName) {
        // Actualizar header
        if ("LOGIN".equals(cardName)) {
            headerPanel.setVisible(false);
            userLabel.setText("Usuario: --");
        } else {
            headerPanel.setVisible(true);
        }
        
        cardLayout.show(mainPanel, cardName);
    }
}
