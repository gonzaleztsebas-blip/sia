/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing.panels;
import sia.sia.ui.swing.*;
import javax.swing.*;
import java.awt.*;
import sia.sia.business.CSVManager;

/**
 *
 * @author ASUS
 */
public class RegisterPanel extends JPanel{
    private SIAFrame parentFrame;
    
    // Componentes
    private JPanel tittlePanel, formPanel, footerPanel;
    private JTextField txtUsername;
    private JPasswordField pswPassword, pswConfirmPassword;
    private JComboBox<String> comboRole;
    private JButton btnRegister, btnBack;
    
    public RegisterPanel(SIAFrame parent) {
        this.parentFrame = parent;
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
        formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(250, 250, 252));

        formPanel.setPreferredSize(new Dimension(350, 200));

        formPanel.add(new JLabel("Usuario: "));
        txtUsername = new JTextField(12);
        formPanel.add(txtUsername);

        formPanel.add(new JLabel("Contraseña: "));
        pswPassword = new JPasswordField(12);
        formPanel.add(pswPassword);
        
        formPanel.add(new JLabel("Confirmar Contraseña: "));
        pswConfirmPassword = new JPasswordField(12);
        formPanel.add(pswConfirmPassword);
        
        String[] roles = {"student", "professor", "admin"};
        formPanel.add(new JLabel("Seleccione su rol: "));
        comboRole = new JComboBox<>(roles);
        formPanel.add(comboRole);

        formPanel.add(new JLabel(""));
        btnRegister = new JButton("Registrarse");
        formPanel.add(btnRegister);

        outer.add(formPanel);  
        // importante: aquí NO uses BorderLayout, usa el wrapper
        formPanel = outer;
    }


    
    private void initFooterPanel() {
        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));

        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 
        footerPanel.setOpaque(false); // opcional, para que se vea más limpio

        btnBack = new JButton("Volver");
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);

        footerPanel.add(btnBack);
        footerPanel.add(Box.createVerticalStrut(15)); // empuja hacia arriba
    }
    
    private void addActionListeners(){
        btnRegister.addActionListener(e-> register());
        
        btnBack.addActionListener(e -> {
            parentFrame.showCard("LOGIN");
        });
    }
    
    private void register(){
        String username = txtUsername.getText();
        String password = new String(pswPassword.getPassword());
        String confirmedPassword = new String(pswConfirmPassword.getPassword());
           
        if (username.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
           
        if(!password.equals(confirmedPassword)){
            JOptionPane.showMessageDialog(this, "La contraseña no es valida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
           
        String role = comboRole.getSelectedItem().toString();
          
        boolean success = CSVManager.signUp(username, password, role);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registro exitoso!\nAhora puede iniciar sesión", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            parentFrame.showCard("LOGIN"); // Volver al login
        } else {
            JOptionPane.showMessageDialog(this, "El usuario ya existe", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
