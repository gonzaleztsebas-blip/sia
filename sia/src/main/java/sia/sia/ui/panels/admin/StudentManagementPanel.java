/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels.admin;

import javax.swing.*;
import java.awt.*;
import sia.sia.business.StudentManager;
import sia.sia.data.Student;
import java.util.List;
import javax.swing.table.DefaultTableModel;


public class StudentManagementPanel extends JPanel {
    private JTable studentTable;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    
    public StudentManagementPanel() {
        initComponents();
        loadStudentData();
        addActionListeners();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Toolbar con botones
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
        
        // Tabla de estudiantes
        String[] columnNames = {"Usuario", "Nombre", "Apellido", "Fecha Nacimiento"};
        Object[][] data = {}; // Datos vacíos por ahora
        
        studentTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addActionListeners() {
        btnAdd.addActionListener(e -> showAddStudentDialog());
        btnEdit.addActionListener(e -> editSelectedStudent());
        btnDelete.addActionListener(e -> deleteSelectedStudent());
        btnRefresh.addActionListener(e -> loadStudentData());
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Agregar Estudiante");
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

            StudentManager.createStudent(user, password, firstName, lastName, birthDate);
            JOptionPane.showMessageDialog(dialog, "Estudiante creado correctamente");
            dialog.dispose();
            loadStudentData();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante para editar");
            return;
        }

        String username = (String) studentTable.getValueAt(selectedRow, 0);
        String currentFirstName = (String) studentTable.getValueAt(selectedRow, 1);
        String currentLastName = (String) studentTable.getValueAt(selectedRow, 2);
        String currentBirthDate = (String) studentTable.getValueAt(selectedRow, 3);

        showEditStudentDialog(username, currentFirstName, currentLastName, currentBirthDate);
    }

    private void showEditStudentDialog(String username, String currentFirstName, String currentLastName, String currentBirthDate) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Editar Estudiante: " + username);
        dialog.setModal(true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        // Campos (usuario no editable)
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

            // Obtener contraseña existente del estudiante
            Student student = StudentManager.findStudent(username);
            String password = student != null ? student.getPassword() : "";

            StudentManager.updateStudent(username, password, newFirstName, newLastName, newBirthDate);

            // DEBUG: Verificar cambios inmediatamente
            StudentManager.reload(); // Forzar recarga
            loadStudentData();      // Refrescar tabla

            JOptionPane.showMessageDialog(dialog, "Estudiante actualizado correctamente");
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante para eliminar");
            return;
        }

        String username = (String) studentTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar estudiante " + username + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            StudentManager.deleteStudent(username);
            loadStudentData(); // Refrescar tabla
            JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente");
        }
    }
    
    private void loadStudentData() {
        List<String[]> students = StudentManager.getStudents();

        String[] columnNames = {"Usuario", "Nombre", "Apellido", "Fecha Nacimiento"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] student : students) {
            model.addRow(new Object[]{
                    student[0], // usuario
                    student[4], // nombre  
                    student[5], // apellido
                    student[6]  // fecha nacimiento
            });
        }

        studentTable.setModel(model);
    }
}
