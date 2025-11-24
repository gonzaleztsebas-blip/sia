/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing.panels;
import sia.sia.ui.swing.*;
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
    private JButton btnRegist;
    
    public LoginPanel(SIAFrame parent){
        parentFrame = parent;
        initComponents();
        addActionListeners();
    }
    
    private void initComponents(){
        setLayout(new BorderLayout());
        initTittlePanel();
        add(tittlePanel, BorderLayout.NORTH);
        initFormPanel();
        add(formPanel, BorderLayout.CENTER);
        initFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        add(new JPanel(), BorderLayout.EAST);
        add(new JPanel(), BorderLayout.WEST);
    }
    
    private void initTittlePanel(){
        tittlePanel = new JPanel();
        tittlePanel.setLayout(new BorderLayout());
        tittlePanel.add(new JLabel(""), BorderLayout.NORTH);
        JLabel title = new JLabel("Sistema de Información Académica");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        tittlePanel.add(title, BorderLayout.CENTER);
        tittlePanel.add(new JLabel(""), BorderLayout.SOUTH);
    }
    
    private void initFormPanel() {
        // Panel que ocupa el centro pero NO se estira a lo ancho
        JPanel outer = new JPanel(new GridBagLayout());

        // panel del formulario
        formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(250, 250, 252));

        formPanel.setPreferredSize(new Dimension(300, 120));

        formPanel.add(new JLabel("Usuario: "));
        txtUser = new JTextField(12);
        formPanel.add(txtUser);

        formPanel.add(new JLabel("Contraseña: "));
        pswPassword = new JPasswordField(12);
        formPanel.add(pswPassword);

        formPanel.add(new JLabel(""));
        btnEnter = new JButton("Iniciar Sesión");
        formPanel.add(btnEnter);

        outer.add(formPanel);  
        // importante: aquí NO uses BorderLayout, usa el wrapper
        formPanel = outer;
    }


    
    private void initFooterPanel() {
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));

        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 
        footerPanel.setOpaque(false); // opcional, para que se vea más limpio

        JLabel lblCuenta = new JLabel("¿No tienes cuenta?");
        lblCuenta.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnRegist = new JButton("Regístrate");
        btnRegist.setAlignmentX(Component.CENTER_ALIGNMENT);

        footerPanel.add(lblCuenta);
        footerPanel.add(Box.createVerticalStrut(5)); // espacio pequeño
        footerPanel.add(btnRegist);
        footerPanel.add(Box.createVerticalStrut(15)); // empuja hacia arriba
    }
    
    private void addActionListeners(){
        btnEnter.addActionListener( e -> login());
        
        btnRegist.addActionListener(e -> {
            parentFrame.showCard("REGISTER");
        });
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
                    case "admin": parentFrame.showCard("ADMIN_DASHBOARD"); break;
                    case "student": parentFrame.showCard("STUDENT_DASHBOARD"); break;
                    case "professor": parentFrame.showCard("PROFESSOR_DASHBOARD"); break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
            }
    }
}
