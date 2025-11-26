/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels.admin;

import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;
import sia.sia.business.CourseManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class CourseManagementPanel extends JPanel {
    private JTable courseTable;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private DefaultTableModel tableModel;
    
    public CourseManagementPanel() {
        initComponents();
        loadCourseData();
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
        String[] columnNames = {"Código", "Nombre", "Créditos", "Requisitos"};
        tableModel = new DefaultTableModel(columnNames, 0);
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addActionListeners() {
        btnAdd.addActionListener(e -> showAddCourseDialog());
        btnEdit.addActionListener(e -> editSelectedCourse());
        btnDelete.addActionListener(e -> deleteSelectedCourse());
        btnRefresh.addActionListener(e -> loadCourseData());
    }
    
    private String getCourseNameByCode(String courseCode) {
        List<String[]> courses = CourseManager.getCourses();
        for (String[] course : courses) {
            if (course.length >= 2 && course[0].equals(courseCode)) {
                return course[1]; // Devolver nombre
            }
        }
        return null;
    }
    
    private String getCourseCodeByName(String courseName) {
        List<String[]> courses = CourseManager.getCourses();
        for (String[] course : courses) {
            if (course.length >= 2 && course[1].equals(courseName)) {
                return course[0]; // Devolver código
            }
        }
        return null;
    }
    
    private void showAddCourseDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Agregar Curso");
        dialog.setModal(true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(UIColors.PANEL_SOFT);

        // --- FORMULARIO ---
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField txtName = new JTextField();
        JTextField txtCredits = new JTextField();

        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Créditos:"));
        formPanel.add(txtCredits);

        // --- REQUISITOS ---
        JPanel requisitePanel = new JPanel();
        requisitePanel.setLayout(new BorderLayout(5, 5));
        requisitePanel.add(new JLabel("Seleccione requisitos:"), BorderLayout.NORTH);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        List<JCheckBox> checkBoxes = new ArrayList<>();

        List<String[]> courses = CourseManager.getCourses();
        for (String[] course : courses) {
            if (course.length >= 2) {
                JCheckBox checkBox = new JCheckBox(course[1]);
                checkBoxes.add(checkBox);
                checkBoxPanel.add(checkBox);
            }
        }

        JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        requisitePanel.add(scrollPane, BorderLayout.CENTER);

        // --- BOTONES ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIColors.PANEL_SOFT);
        JButton btnSave = new JButton("Guardar");
        btnSave.setBackground(UIColors.BUTTON_PRIMARY); btnSave.setForeground(UIColors.BUTTON_PRIMARY_TEXT);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(UIColors.BUTTON_PRIMARY); btnCancel.setForeground(UIColors.BUTTON_PRIMARY_TEXT);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // --- ARMADO FINAL ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(UIColors.PANEL_SOFT);

        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(requisitePanel);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // --- LÓGICA ---
        btnSave.addActionListener(e -> {
        String name = txtName.getText().trim();
        String creditsStr = txtCredits.getText().trim();

        if (name.isEmpty() || creditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Complete nombre y créditos");
            return;
        }

        try {
            int credits = Integer.parseInt(creditsStr);
            int[] creditsArray = new int[] {credits, 0, 0, 0}; // Fundamentacion, Disciplinar, LibreEleccion, Nivelacion
            List<String> requisitesList = new ArrayList<>();

            // DEBUG: Ver qué se está guardando
            System.out.println("=== DEBUG REQUISITOS ===");
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String courseName = checkBox.getText();
                    String courseCode = getCourseCodeByName(courseName);
                    System.out.println("Nombre: " + courseName + " -> Código: " + courseCode);

                    if (courseCode != null) {
                        requisitesList.add(courseCode);
                    }
                }
            }
            System.out.println("Lista final: " + requisitesList);

            CourseManager.createCourse(name, creditsArray, requisitesList);
            JOptionPane.showMessageDialog(dialog, "Curso creado correctamente");
            dialog.dispose();
            loadCourseData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Créditos debe ser un número");
        }
    });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void loadCourseData() {
        tableModel.setRowCount(0);
        List<String[]> courses = CourseManager.getCourses();

        for (String[] course : courses) {
            if (course.length >= 4) {
                // Formatear requisitos con corchetes
                String requisitosFormateados = formatRequisites(course[3]);
                tableModel.addRow(new Object[]{
                    course[0], // código del curso
                    course[1], // nombre
                    course[2], // créditos
                    requisitosFormateados  // requisitos formateados
                });
            }
        }
    }

    private String formatRequisites(String requisitesStr) {
        if (requisitesStr == null || requisitesStr.isEmpty() || requisitesStr.equals("[]")) {
            return "[]";
        }

        // Si ya tiene corchetes, dejarlo igual
        if (requisitesStr.startsWith("[") && requisitesStr.endsWith("]")) {
            return requisitesStr;
        }

        // Si no tiene corchetes, agregarlos
        return "[" + requisitesStr + "]";
    }
    
    private void editSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso para editar");
            return;
        }

        String code = (String) courseTable.getValueAt(selectedRow, 0);
        String currentName = (String) courseTable.getValueAt(selectedRow, 1);
        String currentCredits = (String) courseTable.getValueAt(selectedRow, 2);
        String currentRequisites = (String) courseTable.getValueAt(selectedRow, 3);
        
        showEditCourseDialog(code, currentName, currentCredits, currentRequisites);
    }
    
    private void showEditCourseDialog(String code, String currentName, String currentCredits, String currentRequisites) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Editar Curso: " + code);
    dialog.setModal(true);
    dialog.setSize(500, 450);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.getContentPane().setBackground(UIColors.PANEL_SOFT);

    // --- FORMULARIO ---
    JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    JTextField txtCode = new JTextField(code);
    txtCode.setEditable(false);
    JTextField txtName = new JTextField(currentName);
    JTextField txtCredits = new JTextField(currentCredits);

    formPanel.add(new JLabel("Código:"));
    formPanel.add(txtCode);
    formPanel.add(new JLabel("Nombre:"));
    formPanel.add(txtName);
    formPanel.add(new JLabel("Créditos:"));
    formPanel.add(txtCredits);

    // --- REQUISITOS ---
    JPanel requisitePanel = new JPanel();
    requisitePanel.setLayout(new BorderLayout(5, 5));
    requisitePanel.add(new JLabel("Seleccione requisitos:"), BorderLayout.NORTH);

    JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

    List<JCheckBox> checkBoxes = new ArrayList<>();

    // Cargar cursos como CheckBoxes
    List<String[]> courses = CourseManager.getCourses();
    for (String[] course : courses) {
        if (course.length >= 2 && !course[0].equals(code)) { // Excluir el curso actual
            JCheckBox checkBox = new JCheckBox(course[1]);
            checkBoxes.add(checkBox);
            checkBoxPanel.add(checkBox);
        }
    }

    // Marcar los requisitos actuales
    if (currentRequisites != null && !currentRequisites.equals("[]") && !currentRequisites.isEmpty()) {
        String[] currentRequisitesArray = currentRequisites.replace("[", "").replace("]", "").split(";");
        for (String requisiteCode : currentRequisitesArray) {
            String requisiteName = getCourseNameByCode(requisiteCode.trim());
            if (requisiteName != null) {
                for (JCheckBox checkBox : checkBoxes) {
                    if (checkBox.getText().equals(requisiteName)) {
                        checkBox.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

    JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
    scrollPane.setPreferredSize(new Dimension(450, 200));
    requisitePanel.add(scrollPane, BorderLayout.CENTER);

    // --- BOTONES ---
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(UIColors.PANEL_SOFT);
    JButton btnSave = new JButton("Guardar Cambios");
    btnSave.setBackground(UIColors.BUTTON_PRIMARY); btnSave.setForeground(UIColors.BUTTON_PRIMARY_TEXT);
    JButton btnCancel = new JButton("Cancelar");
    btnCancel.setBackground(UIColors.BUTTON_PRIMARY); btnCancel.setForeground(UIColors.BUTTON_PRIMARY_TEXT);
    buttonPanel.add(btnSave);
    buttonPanel.add(btnCancel);

    // --- ARMADO FINAL ---
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    contentPanel.setBackground(UIColors.PANEL_SOFT);

    contentPanel.add(formPanel);
    contentPanel.add(Box.createVerticalStrut(15));
    contentPanel.add(requisitePanel);

    dialog.add(contentPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    // --- LÓGICA ---
    btnSave.addActionListener(e -> {
        String newName = txtName.getText().trim();
        String newCreditsStr = txtCredits.getText().trim();

        if (newName.isEmpty() || newCreditsStr.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Complete nombre y créditos");
            return;
        }

        try {
            int newCredits = Integer.parseInt(newCreditsStr);
            int[] newCreditsArray = new int[] {newCredits, 0, 0, 0}; // Fundamentacion, Disciplinar, LibreEleccion, Nivelacion
            List<String> newRequisitesList = new ArrayList<>();

            // Obtener cursos seleccionados
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String requisiteCode = getCourseCodeByName(checkBox.getText());
                    if (requisiteCode != null) {
                        newRequisitesList.add(requisiteCode);
                    }
                }
            }

            CourseManager.updateCourse(Long.parseLong(code), newName, newCreditsArray, newRequisitesList);
            JOptionPane.showMessageDialog(dialog, "Curso actualizado correctamente");
            dialog.dispose();
            loadCourseData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Créditos debe ser un número");
        }
    });

    btnCancel.addActionListener(e -> dialog.dispose());

    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}
    
    private void deleteSelectedCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso para eliminar");
            return;
        }

        String code = (String) courseTable.getValueAt(selectedRow, 0);
        String name = (String) courseTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar curso " + name + " (" + code + ")?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            CourseManager.deleteCourse(Long.parseLong(code));
            loadCourseData();
            JOptionPane.showMessageDialog(this, "Curso eliminado correctamente");
        }
    }
}
