/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing.panels;

import javax.swing.*;
import java.awt.*;
import sia.sia.data.*;
import sia.sia.business.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;


public class GradeManagementPanel extends JPanel {
    private JTable gradeTable;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnViewStudent, btnViewGroup;
    private DefaultTableModel tableModel;
    
    public GradeManagementPanel() {
        initComponents();
        loadGradeData();
        addActionListeners();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Registrar Nota");
        btnEdit = new JButton("Editar Nota");
        btnDelete = new JButton("Eliminar Nota");
        btnRefresh = new JButton("Actualizar");
        btnViewStudent = new JButton("Ver Historial Estudiante");
        btnViewGroup = new JButton("Ver Notas Grupo");
        
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        toolbar.add(btnRefresh);
        toolbar.add(btnViewStudent);
        toolbar.add(btnViewGroup);
        add(toolbar, BorderLayout.NORTH);
        
        // Tabla
        String[] columnNames = {"Estudiante", "Grupo", "Curso", "Nota", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gradeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addActionListeners() {
        btnAdd.addActionListener(e -> showAddGradeDialog());
        btnEdit.addActionListener(e -> editSelectedGrade());
        btnDelete.addActionListener(e -> deleteSelectedGrade());
        btnRefresh.addActionListener(e -> loadGradeData());
        btnViewStudent.addActionListener(e -> viewStudentGrades());
        btnViewGroup.addActionListener(e -> viewGroupGrades());
    }
    
    private void loadGradeData() {
        tableModel.setRowCount(0);
        List<String[]> grades = GradeManager.getGrades();
        
        for (String[] grade : grades) {
            if (grade.length >= 3) {
                String studentName = getStudentName(grade[0]);
                String courseName = getCourseNameFromGroup(grade[1]);
                double gradeValue = Double.parseDouble(grade[2]);
                String status = gradeValue >= 3.0 ? "APROBADO" : "REPROBADO";
                
                tableModel.addRow(new Object[]{
                    studentName + " (" + grade[0] + ")",
                    "Grupo " + grade[1],
                    courseName,
                    String.format("%.2f", gradeValue),
                    status
                });
            }
        }
    }
    
    private String getStudentName(String username) {
        var student = StudentManager.findStudent(username);
        return student != null ? student.getFirstName() + " " + student.getLastName() : username;
    }
    
    private String getCourseNameFromGroup(String groupNumber) {
        var group = GroupManager.findGroup(groupNumber);
        return group != null && group.getRepresents() != null ? group.getRepresents().getName() : "Curso no encontrado";
    }
    
    private void showAddGradeDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Registrar Calificación");
        dialog.setModal(true);
        dialog.setSize(500, 300);
        dialog.setLayout(new BorderLayout(10, 10));

        // --- FORMULARIO ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        
        // ComboBox para estudiante
        JComboBox<String> comboStudent = new JComboBox<>();
        loadStudentsToComboBox(comboStudent);
        
        // ComboBox para grupo
        JComboBox<String> comboGroup = new JComboBox<>();
        loadGroupsToComboBox(comboGroup);
        
        JTextField txtGrade = new JTextField();

        formPanel.add(new JLabel("Estudiante:"));
        formPanel.add(comboStudent);
        formPanel.add(new JLabel("Grupo:"));
        formPanel.add(comboGroup);
        formPanel.add(new JLabel("Calificación (0.0 - 5.0):"));
        formPanel.add(txtGrade);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // --- BOTONES ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // --- ARMADO FINAL ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(formPanel);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // --- LÓGICA ---
        btnSave.addActionListener(e -> {
            String selectedStudent = (String) comboStudent.getSelectedItem();
            String selectedGroup = (String) comboGroup.getSelectedItem();
            String gradeStr = txtGrade.getText().trim();

            if (selectedStudent == null || selectedGroup == null || gradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos");
                return;
            }

            try {
                double grade = Double.parseDouble(gradeStr);
                if (grade < 0.0 || grade > 5.0) {
                    JOptionPane.showMessageDialog(dialog, "La calificación debe estar entre 0.0 y 5.0");
                    return;
                }

                // Extraer username y número de grupo
                String studentUsername = extractUsernameFromDisplay(selectedStudent);
                String groupNumber = extractGroupNumber(selectedGroup);

                if (studentUsername == null || groupNumber == null) {
                    JOptionPane.showMessageDialog(dialog, "Error en los datos seleccionados");
                    return;
                }

                // Registrar calificación
                GradeManager.createGrade(studentUsername, groupNumber, grade);
                JOptionPane.showMessageDialog(dialog, "Calificación registrada correctamente");
                dialog.dispose();
                loadGradeData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "La calificación debe ser un número válido");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void loadStudentsToComboBox(JComboBox<String> combo) {
        combo.removeAllItems();
        List<String[]> students = StudentManager.getStudents();
        for (String[] student : students) {
            if (student.length >= 6) {
                String display = student[4] + " " + student[5] + " (" + student[0] + ")";
                combo.addItem(display);
            }
        }
    }
    
    private void loadGroupsToComboBox(JComboBox<String> combo) {
        combo.removeAllItems();
        List<String[]> groups = GroupManager.loadGroups();
        for (String[] group : groups) {
            if (group.length >= 5) {
                String courseName = getCourseName(group[4]);
                String display = "Grupo " + group[0] + " - " + courseName;
                combo.addItem(display);
            }
        }
    }
    
    private String getCourseName(String courseCode) {
        if (courseCode == null || courseCode.equals("null") || courseCode.isEmpty()) {
            return "Sin curso";
        }
        try {
            var course = CourseManager.findCourse(Long.parseLong(courseCode));
            return course != null ? course.getName() : courseCode;
        } catch (NumberFormatException e) {
            return courseCode;
        }
    }
    
    private String extractUsernameFromDisplay(String display) {
        int start = display.lastIndexOf("(");
        int end = display.lastIndexOf(")");
        if (start != -1 && end != -1 && end > start) {
            return display.substring(start + 1, end);
        }
        return null;
    }
    
    private String extractGroupNumber(String display) {
        if (display.startsWith("Grupo ")) {
            String[] parts = display.split(" ");
            if (parts.length >= 2) {
                return parts[1];
            }
        }
        return null;
    }
    
    private void editSelectedGrade() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una calificación para editar");
            return;
        }

        String studentDisplay = (String) gradeTable.getValueAt(selectedRow, 0);
        String groupDisplay = (String) gradeTable.getValueAt(selectedRow, 1);
        String currentGrade = (String) gradeTable.getValueAt(selectedRow, 3);

        String studentUsername = extractUsernameFromDisplay(studentDisplay);
        String groupNumber = extractGroupNumber(groupDisplay);

        if (studentUsername == null || groupNumber == null) {
            JOptionPane.showMessageDialog(this, "Error en los datos seleccionados");
            return;
        }

        // Diálogo para editar calificación
        String newGradeStr = JOptionPane.showInputDialog(this, 
            "Nueva calificación para " + studentUsername + ":\n(Grupo " + groupNumber + ")", 
            currentGrade);

        if (newGradeStr != null && !newGradeStr.trim().isEmpty()) {
            try {
                double newGrade = Double.parseDouble(newGradeStr.trim());
                if (newGrade < 0.0 || newGrade > 5.0) {
                    JOptionPane.showMessageDialog(this, "La calificación debe estar entre 0.0 y 5.0");
                    return;
                }
                
                GradeManager.updateGrade(studentUsername, groupNumber, newGrade);
                JOptionPane.showMessageDialog(this, "Calificación actualizada correctamente");
                loadGradeData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La calificación debe ser un número válido");
            }
        }
    }
    
    private void deleteSelectedGrade() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una calificación para eliminar");
            return;
        }

        String studentDisplay = (String) gradeTable.getValueAt(selectedRow, 0);
        String groupDisplay = (String) gradeTable.getValueAt(selectedRow, 1);

        String studentUsername = extractUsernameFromDisplay(studentDisplay);
        String groupNumber = extractGroupNumber(groupDisplay);

        if (studentUsername == null || groupNumber == null) {
            JOptionPane.showMessageDialog(this, "Error en los datos seleccionados");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar calificación de " + studentUsername + " del grupo " + groupNumber + "?", 
            "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            GradeManager.deleteGrade(studentUsername, groupNumber);
            JOptionPane.showMessageDialog(this, "Calificación eliminada correctamente");
            loadGradeData();
        }
    }
    
    private void viewStudentGrades() {
        String username = JOptionPane.showInputDialog(this, 
            "Ingrese el username del estudiante:");

        if (username != null && !username.trim().isEmpty()) {
            showStudentGradesDialog(username.trim());
        }
    }

    private void showStudentGradesDialog(String username) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Historial Académico - " + username);
        dialog.setModal(true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        // Obtener calificaciones del estudiante
        List<Grade> grades = GradeManager.getGradesByStudent(username);

        // Crear tabla para mostrar historial
        String[] columnNames = {"Curso", "Grupo", "Nota", "Estado", "Semestre"};
        DefaultTableModel historyModel = new DefaultTableModel(columnNames, 0);
        JTable historyTable = new JTable(historyModel);

        // Llenar tabla
        double sum = 0.0;
        for (Grade grade : grades) {
            String courseName = grade.getGroup().getRepresents().getName();
            String groupNumber = String.valueOf(grade.getGroup().getNumber());
            String gradeValue = String.format("%.2f", grade.getGrade());
            String status = grade.getGrade() >= 3.0 ? "APROBADO" : "REPROBADO";
            String semester = grade.getGroup().getSemester();

            historyModel.addRow(new Object[]{courseName, groupNumber, gradeValue, status, semester});
            sum += grade.getGrade();
        }

        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Panel de información
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        double average = grades.isEmpty() ? 0.0 : sum / grades.size();
        infoPanel.add(new JLabel("Estudiante: " + getStudentName(username)));
        infoPanel.add(new JLabel("Promedio General: " + String.format("%.2f", average)));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void viewGroupGrades() {
        String groupNumber = JOptionPane.showInputDialog(this, 
            "Ingrese el número del grupo:");

        if (groupNumber != null && !groupNumber.trim().isEmpty()) {
            showGroupGradesDialog(groupNumber.trim());
        }
    }

    private void showGroupGradesDialog(String groupNumber) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Calificaciones Grupo " + groupNumber);
        dialog.setModal(true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        // Obtener calificaciones del grupo
        List<Grade> grades = GradeManager.getGradesByGroup(groupNumber);

        // Crear tabla
        String[] columnNames = {"Estudiante", "Nota", "Estado"};
        DefaultTableModel groupModel = new DefaultTableModel(columnNames, 0);
        JTable groupTable = new JTable(groupModel);

        // Llenar tabla
        double sum = 0.0;
        for (Grade grade : grades) {
            String studentName = getStudentName(grade.getStudent().getUser());
            String gradeValue = String.format("%.2f", grade.getGrade());
            String status = grade.getGrade() >= 3.0 ? "APROBADO" : "REPROBADO";

            groupModel.addRow(new Object[]{studentName, gradeValue, status});
            sum += grade.getGrade();
        }

        JScrollPane scrollPane = new JScrollPane(groupTable);

        // Panel de información
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        double average = grades.isEmpty() ? 0.0 : sum / grades.size();
        var group = GroupManager.findGroup(groupNumber);
        String courseName = group != null && group.getRepresents() != null ? group.getRepresents().getName() : "Curso no encontrado";

        infoPanel.add(new JLabel("Curso: " + courseName));
        infoPanel.add(new JLabel("Promedio del Grupo: " + String.format("%.2f", average)));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}