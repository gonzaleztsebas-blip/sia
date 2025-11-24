/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels;
import sia.sia.ui.*;
import java.awt.*;
import javax.swing.*;
import sia.sia.business.CSVManager;
import sia.sia.data.User;

/**
 *
 * @author ASUS
 */
public class LoginPanel extends JPanel{
    private SIAFrame parentFrame;
    
    private JPanel tittlePanel;
    private JPanel formPanel;
    private JPanel footerPanel;
    
    private JTextField txtUser;
    private JPasswordField pswPassword;
    private JButton btnEnter;
    
    public LoginPanel(SIAFrame parent){
        parentFrame = parent;
        setBackground(UIColors.BACKGROUND);
        initComponents();
        addActionListeners();
    }
    
    private void initComponents(){
        setLayout(new BorderLayout());
        setBackground(UIColors.BACKGROUND);
        initTittlePanel();
        add(tittlePanel, BorderLayout.NORTH);
        initFormPanel();
        add(formPanel, BorderLayout.CENTER);
        initFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        JPanel eastPanel = new JPanel();
        eastPanel.setBackground(UIColors.BACKGROUND);
        add(eastPanel, BorderLayout.EAST);
        JPanel westPanel = new JPanel();
        westPanel.setBackground(UIColors.BACKGROUND);
        add(westPanel, BorderLayout.WEST);
    }
    
    private void initTittlePanel(){
        tittlePanel = new JPanel();
        tittlePanel.setLayout(new BorderLayout());
        tittlePanel.setBackground(UIColors.BACKGROUND);
        JPanel northGap = new JPanel();
        northGap.setBackground(UIColors.BACKGROUND);
        tittlePanel.add(northGap, BorderLayout.NORTH);
        JLabel title = new JLabel("Sistema de Información Académica");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(UIColors.LABEL_TEXT);
        tittlePanel.add(title, BorderLayout.CENTER);
        JPanel southGap = new JPanel();
        southGap.setBackground(UIColors.BACKGROUND);
        tittlePanel.add(southGap, BorderLayout.SOUTH);
    }
    
    private void initFormPanel() {
        // Panel que ocupa el centro pero NO se estira a lo ancho
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UIColors.BACKGROUND);

        // panel del formulario
        formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(UIColors.FORM_BACKGROUND);

        formPanel.setPreferredSize(new Dimension(300, 120));

        JLabel userLabel = new JLabel("Usuario: ");
        userLabel.setForeground(UIColors.LABEL_TEXT);
        formPanel.add(userLabel);
        txtUser = new JTextField(12);
        txtUser.setBackground(Color.WHITE);
        txtUser.setForeground(UIColors.LABEL_TEXT);
        formPanel.add(txtUser);

        JLabel passLabel = new JLabel("Contraseña: ");
        passLabel.setForeground(UIColors.LABEL_TEXT);
        formPanel.add(passLabel);
        pswPassword = new JPasswordField(12);
        pswPassword.setBackground(Color.WHITE);
        pswPassword.setForeground(UIColors.LABEL_TEXT);
        formPanel.add(pswPassword);

        formPanel.add(new JLabel(""));
        btnEnter = new JButton("Iniciar Sesión");
        btnEnter.setBackground(UIColors.BUTTON);
        btnEnter.setForeground(Color.WHITE);
        btnEnter.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnter.setFocusPainted(false);
        formPanel.add(btnEnter);

        outer.add(formPanel);  
        // importante: aquí NO uses BorderLayout, usa el wrapper
        formPanel = outer;
    }


    
    private void initFooterPanel() {
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(UIColors.BACKGROUND);

        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 
        footerPanel.setOpaque(true);

        JLabel lblCuenta = new JLabel("Contacte al administrador para crear una nueva cuenta");
        lblCuenta.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCuenta.setForeground(UIColors.LABEL_TEXT);

        footerPanel.add(lblCuenta);
        footerPanel.add(Box.createVerticalStrut(25)); // espacio pequeño
    }
    
    private void addActionListeners(){
        btnEnter.addActionListener( e -> login());
    }
    
    private void login(){
        String username = txtUser.getText();
            String password = new String(pswPassword.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            User currentUser = CSVManager.login(username, password);
            if (currentUser != null) {
                JOptionPane.showMessageDialog(this, "Login exitoso!\nBienvenido: " + username, "Éxito", JOptionPane.INFORMATION_MESSAGE);

                switch(currentUser.getRole().toLowerCase()) {
                    case "admin": parentFrame.showCard("ADMIN_DASHBOARD", username); break;
                    case "student": parentFrame.showCard("STUDENT_DASHBOARD", username); break;
                    case "professor": parentFrame.showCard("PROFESSOR_DASHBOARD", username); break;
                }
                
                txtUser.setText("");
                pswPassword.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }
}
