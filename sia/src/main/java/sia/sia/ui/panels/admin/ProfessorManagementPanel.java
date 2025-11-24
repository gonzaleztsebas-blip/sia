/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels.admin;

import javax.swing.*;
import java.awt.*;
import sia.sia.business.ProfessorManager;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ProfessorManagementPanel extends JPanel {
    private JTable professorTable;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private DefaultTableModel tableModel;
    
    public ProfessorManagementPanel() {
        initComponents();
        loadProfessorData();
        addActionListeners();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Agregar");
        btnEdit = new JButton("Editar");
        btnDelete = new JButton("Eliminar");
        btnRefresh = new JButton("Actualizar");
        
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        add(toolbar, BorderLayout.NORTH);
        
        // Tabla
        String[] columnNames = {"Usuario", "Nombre", "Apellido", "Fecha Nacimiento"};
        tableModel = new DefaultTableModel(columnNames, 0);
        professorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(professorTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addActionListeners() {
        btnAdd.addActionListener(e -> showAddProfessorDialog());
        btnEdit.addActionListener(e -> editSelectedProfessor());
        btnDelete.addActionListener(e -> deleteSelectedProfessor());
        btnRefresh.addActionListener(e -> loadProfessorData());
    }
    
    private void loadProfessorData() {
        tableModel.setRowCount(0);
        List<String[]> professors = ProfessorManager.getProfessors();
        
        for (String[] professor : professors) {
            if (professor.length >= 7) {
                tableModel.addRow(new Object[]{
                    professor[0], // usuario
                    professor[4], // nombre
                    professor[5], // apellido
                    professor[6]  // fecha nacimiento
                });
            }
        }
    }
    
    private void showAddProfessorDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Agregar Profesor");
        dialog.setModal(true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(400, 300);
        
        JTextField txtUser = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtFirstName = new JTextField();
        JTextField txtLastName = new JTextField();
        JTextField txtBirthDate = new JTextField();
        
        dialog.add(new JLabel("Usuario:"));
        dialog.add(txtUser);
        dialog.add(new JLabel("Contraseña:"));
        dialog.add(txtPassword);
        dialog.add(new JLabel("Nombre:"));
        dialog.add(txtFirstName);
        dialog.add(new JLabel("Apellido:"));
        dialog.add(txtLastName);
        dialog.add(new JLabel("Fecha Nacimiento (YYYY-MM-DD):"));
        dialog.add(txtBirthDate);
        
        JButton btnSave = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        
        btnSave.addActionListener(e -> {
            String user = txtUser.getText();
            String password = new String(txtPassword.getPassword());
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String birthDate = txtBirthDate.getText();
            
            if (user.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos");
                return;
            }
            
            ProfessorManager.createProfessor(user, password, firstName, lastName, birthDate);
            JOptionPane.showMessageDialog(dialog, "Profesor creado correctamente");
            dialog.dispose();
            loadProfessorData();
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void editSelectedProfessor() {
        int selectedRow = professorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un profesor para editar");
            return;
        }

        String username = (String) professorTable.getValueAt(selectedRow, 0);
        String currentFirstName = (String) professorTable.getValueAt(selectedRow, 1);
        String currentLastName = (String) professorTable.getValueAt(selectedRow, 2);
        String currentBirthDate = (String) professorTable.getValueAt(selectedRow, 3);
        
        showEditProfessorDialog(username, currentFirstName, currentLastName, currentBirthDate);
    }
    
    private void showEditProfessorDialog(String username, String currentFirstName, String currentLastName, String currentBirthDate) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Editar Profesor: " + username);
        dialog.setModal(true);
        dialog.setLayout(new GridLayout(5, 2));
        dialog.setSize(400, 250);
        
        JTextField txtUser = new JTextField(username);
        txtUser.setEditable(false);
        JTextField txtFirstName = new JTextField(currentFirstName);
        JTextField txtLastName = new JTextField(currentLastName);
        JTextField txtBirthDate = new JTextField(currentBirthDate);
        
        dialog.add(new JLabel("Usuario:"));
        dialog.add(txtUser);
        dialog.add(new JLabel("Nombre:"));
        dialog.add(txtFirstName);
        dialog.add(new JLabel("Apellido:"));
        dialog.add(txtLastName);
        dialog.add(new JLabel("Fecha Nacimiento:"));
        dialog.add(txtBirthDate);
        
        JButton btnSave = new JButton("Guardar Cambios");
        JButton btnCancel = new JButton("Cancelar");
        
        btnSave.addActionListener(e -> {
            String newFirstName = txtFirstName.getText();
            String newLastName = txtLastName.getText();
            String newBirthDate = txtBirthDate.getText();
            
            if (newFirstName.isEmpty() || newLastName.isEmpty() || newBirthDate.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos");
                return;
            }
            
            ProfessorManager.updateProfessor(username, newFirstName, newLastName, newBirthDate);
            JOptionPane.showMessageDialog(dialog, "Profesor actualizado correctamente");
            dialog.dispose();
            loadProfessorData();
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void deleteSelectedProfessor() {
        int selectedRow = professorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un profesor para eliminar");
            return;
        }

        String username = (String) professorTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar profesor " + username + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ProfessorManager.deleteProfessor(username);
            loadProfessorData();
            JOptionPane.showMessageDialog(this, "Profesor eliminado correctamente");
        }
    }
}
