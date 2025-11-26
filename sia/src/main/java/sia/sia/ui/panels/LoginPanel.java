package sia.sia.ui.panels;

import sia.sia.ui.*;
import java.awt.*;
import javax.swing.*;
import sia.sia.business.CSVManager;
import sia.sia.data.User;

public class LoginPanel extends JPanel {

    private final SIAFrame parentFrame;

    private JTextField txtUser;
    private JPasswordField pswPassword;
    private JButton btnEnter;

    public LoginPanel(SIAFrame parent) {
        this.parentFrame = parent;
        setBackground(UIColors.BACKGROUND);
        initComponents();
        addActionListeners();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(UIColors.BACKGROUND);
        container.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 5);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel leftPanel = createLeftPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.75;
        gbc.weighty = 1;
        container.add(leftPanel, gbc);

        JPanel rightPanel = createRightPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        container.add(rightPanel, gbc);

        add(container);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIColors.PANEL_SOFT);
        panel.setBorder(BorderFactory.createLineBorder(UIColors.PANEL_BORDER, 2));

        // Header
        JPanel header = new JPanel();
        header.setBackground(UIColors.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel title = new JLabel("INICIE SESIÓN");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(UIColors.LABEL_TEXT);
        header.add(title);

        // Form
        JPanel form = new JPanel(new GridLayout(3, 1, 10, 15));
        form.setBackground(UIColors.PANEL_SOFT);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Usuario
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(UIColors.PANEL_SOFT);

        JLabel userLabel = new JLabel("usuario");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(UIColors.LABEL_TEXT);


        txtUser = new JTextField();
        txtUser.setBackground(Color.WHITE);
        txtUser.setForeground(UIColors.TEXT);

        userPanel.add(userLabel, BorderLayout.NORTH);
        userPanel.add(txtUser, BorderLayout.CENTER);

        // Contraseña
        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(UIColors.PANEL_SOFT);

        JLabel passLabel = new JLabel("clave");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passLabel.setForeground(UIColors.LABEL_TEXT);

        pswPassword = new JPasswordField();
        pswPassword.setBackground(Color.WHITE);
        pswPassword.setForeground(UIColors.TEXT);

        passPanel.add(passLabel, BorderLayout.NORTH);
        passPanel.add(pswPassword, BorderLayout.CENTER);

        // Botón
        btnEnter = new JButton("iniciar sesión");
        btnEnter.setBackground(UIColors.BUTTON_PRIMARY);
        btnEnter.setForeground(UIColors.BUTTON_PRIMARY_TEXT);
        btnEnter.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnter.setBorder(BorderFactory.createLineBorder(UIColors.PANEL_BORDER));

        form.add(userPanel);
        form.add(passPanel);
        form.add(btnEnter);

        panel.add(header, BorderLayout.NORTH);
        panel.add(form, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIColors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));

        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(UIColors.PANEL_SOFT);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIColors.PANEL_BORDER),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel welcomeTitle = new JLabel("Bienvenido Visitante,");
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeTitle.setForeground(UIColors.LABEL_TEXT);

        JTextArea welcomeText = new JTextArea(
            "Si usted es un usuario activo de la Universidad,\n" +
            "por favor inicie su sesión.\n\n" +
            "Si usted fue admitido, consulte el proceso de registro.\n\n" +
            "Si no cumple ningún caso anterior, seleccione el\n" +
            "servicio que desea consultar."
        );
        welcomeText.setEditable(false);
        welcomeText.setBackground(UIColors.PANEL_SOFT);
        welcomeText.setForeground(UIColors.TEXT);
        welcomeText.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);

        welcomePanel.add(welcomeTitle, BorderLayout.NORTH);
        welcomePanel.add(welcomeText, BorderLayout.CENTER);

        panel.add(welcomePanel, BorderLayout.NORTH);

        return panel;
    }

    private void addActionListeners() {
        btnEnter.addActionListener(e -> login());
    }

    private void login() {
        String username = txtUser.getText();
        String password = new String(pswPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User currentUser = CSVManager.login(username, password);

        if (currentUser != null) {
            JOptionPane.showMessageDialog(this, "Login exitoso!\nBienvenido: " + username,
                                          "Éxito", JOptionPane.INFORMATION_MESSAGE);

            parentFrame.showCard(currentUser.getRole().toUpperCase() + "_DASHBOARD", username);

            txtUser.setText("");
            pswPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
