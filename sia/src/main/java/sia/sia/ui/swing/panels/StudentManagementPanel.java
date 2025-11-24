/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing.panels;

import javax.swing.*;
import java.awt.*;
import sia.sia.business.StudentManager;
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
        // Aquí irá el formulario para agregar estudiante
        JOptionPane.showMessageDialog(this, "Agregar estudiante - Pendiente");
    }

    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante para editar");
            return;
        }
        // Lógica para editar
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
        }
    }
    
    private void loadStudentData() {
        List<String[]> students = StudentManager.getStudents();

        // DEBUG: Ver la estructura real
        System.out.println("=== DEBUG STUDENT DATA ===");
        for (int i = 0; i < students.size(); i++) {
            String[] student = students.get(i);
            System.out.println("Estudiante " + i + " - Campos: " + student.length);
            for (int j = 0; j < student.length; j++) {
                System.out.println("  Campo " + j + ": '" + student[j] + "'");
            }
            System.out.println("---");
        }

        String[] columnNames = {"Usuario", "Nombre", "Apellido", "Fecha Nacimiento"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (String[] student : students) {
            if (student.length >= 7) {
                // Estructura completa: [user, password, role, id, firstName, lastName, birthDate]
                model.addRow(new Object[]{
                    student[0], // usuario
                    student[4], // nombre  
                    student[5], // apellido
                    student[6]  // fecha nacimiento
                });
            } else if (student.length >= 1) {
                // Estructura mínima - solo mostrar username
                model.addRow(new Object[]{student[0], "N/A", "N/A", "N/A"});
            }
        }

        studentTable.setModel(model);
    }
}