/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.panels.admin;

import javax.swing.*;
import java.awt.*;
import sia.sia.business.GroupManager;
import sia.sia.business.CourseManager;
import sia.sia.business.ProfessorManager;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class GroupManagementPanel extends JPanel {
    private JTable groupTable;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    private DefaultTableModel tableModel;
    
    public GroupManagementPanel() {
        initComponents();
        loadGroupData();
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
        String[] columnNames = {"Número", "Curso", "Semestre", "Profesor", "Estudiantes", "Horario"};
        tableModel = new DefaultTableModel(columnNames, 0);
        groupTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(groupTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addActionListeners() {
        btnAdd.addActionListener(e -> showAddGroupDialog());
        btnEdit.addActionListener(e -> editSelectedGroup());
        btnDelete.addActionListener(e -> deleteSelectedGroup());
        btnRefresh.addActionListener(e -> loadGroupData());
    }
    
    private void loadGroupData() {
        tableModel.setRowCount(0);
        List<String[]> groups = GroupManager.loadGroups();
        
        for (String[] group : groups) {
            if (group.length >= 7) {
                String courseName = getCourseName(group[4]);
                String professorName = getProfessorName(group[5]);
                int studentCount = group[6] != null && !group[6].isEmpty() && !group[6].equals("null") ? group[6].split(";").length : 0;
                String schedule = getScheduleDisplay(group[1], group[2]); // días y horarios
                
                tableModel.addRow(new Object[]{
                    group[0], // número
                    courseName, // curso
                    group[3], // semestre
                    professorName, // profesor
                    studentCount + " estudiantes", // conteo estudiantes
                    schedule // horario
                });
            }
        }
    }
    
    private String getCourseName(String courseCode) {
        if (courseCode == null || courseCode.equals("null") || courseCode.isEmpty()) {
            return "Sin curso";
        }
        var course = CourseManager.findCourse(Long.parseLong(courseCode));
        return course != null ? course.getName() : courseCode;
    }
    
    private String getProfessorName(String professorUser) {
        if (professorUser == null || professorUser.equals("null") || professorUser.isEmpty()) {
            return "Sin asignar";
        }
        var professor = ProfessorManager.findProfessor(professorUser);
        return professor != null ? professor.getFirstName() + " " + professor.getLastName() : professorUser;
    }
    
    private String getScheduleDisplay(String days, String times) {
        if (days == null || times == null || days.equals("null") || times.equals("null")) {
            return "Sin horario";
        }
        return days + " | " + times;
    }
    
    private void showAddGroupDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Agregar Grupo");
        dialog.setModal(true);
        dialog.setSize(500, 450);
        dialog.setLayout(new BorderLayout(10, 10));

        // --- FORMULARIO ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // ComboBox para curso
        JComboBox<String> comboCourse = new JComboBox<>();
        loadCoursesToComboBox(comboCourse);

        // ComboBox para profesor
        JComboBox<String> comboProfessor = new JComboBox<>();
        loadProfessorsToComboBox(comboProfessor);

        JTextField txtSemester = new JTextField();
        JTextField txtDays = new JTextField();
        JTextField txtTimes = new JTextField();

        formPanel.add(new JLabel("Curso:"));
        formPanel.add(comboCourse);
        formPanel.add(new JLabel("Profesor:"));
        formPanel.add(comboProfessor);
        formPanel.add(new JLabel("Semestre (ej: 2025-1):"));
        formPanel.add(txtSemester);
        formPanel.add(new JLabel("Días (L;M;W;J;V):"));
        formPanel.add(txtDays);
        formPanel.add(new JLabel("Horarios (7-9;7-9;7-9):"));
        formPanel.add(txtTimes);

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
            String selectedCourse = (String) comboCourse.getSelectedItem();
            String selectedProfessor = (String) comboProfessor.getSelectedItem();
            String semester = txtSemester.getText().trim();
            String days = txtDays.getText().trim();
            String times = txtTimes.getText().trim();

            if (selectedCourse == null || semester.isEmpty() || days.isEmpty() || times.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Complete todos los campos obligatorios");
                return;
            }

            // Convertir nombre del curso a código
            String courseCode = getCourseCodeByName(selectedCourse);
            if (courseCode == null) {
                JOptionPane.showMessageDialog(dialog, "Curso no válido");
                return;
            }

            // Validar que días y horarios tengan la misma cantidad
            String[] daysArray = days.split(";");
            String[] timesArray = times.split(";");
            if (daysArray.length != timesArray.length) {
                JOptionPane.showMessageDialog(dialog, "La cantidad de días y horarios debe coincidir");
                return;
            }

            // Crear grupo
            GroupManager.createGroup(daysArray, timesArray, semester, courseCode);

            // Asignar profesor si se seleccionó uno
            if (selectedProfessor != null && !selectedProfessor.equals("Sin asignar")) {
                String professorUsername = getProfessorUsernameByName(selectedProfessor);
                if (professorUsername != null) {
                    // Obtener el último grupo creado (este mismo)
                    String lastGroupNumber = getLastCreatedGroupNumber();
                    if (lastGroupNumber != null) {
                        GroupManager.assignProfessor(lastGroupNumber, professorUsername);
                    }
                }
            }

            JOptionPane.showMessageDialog(dialog, "Grupo creado correctamente");
            dialog.dispose();
            loadGroupData();
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private String getLastCreatedGroupNumber() {
        List<String[]> groups = GroupManager.loadGroups();
        if (!groups.isEmpty()) {
            return groups.get(groups.size() - 1)[0]; // Último grupo creado
        }
        return null;
    }
    
    private void loadCoursesToComboBox(JComboBox<String> combo) {
        combo.removeAllItems();
        List<String[]> courses = CourseManager.getCourses();
        for (String[] course : courses) {
            if (course.length >= 2) {
                combo.addItem(course[1]); // Nombre del curso
            }
        }
    }
    
    private String getCourseCodeByName(String courseName) {
        List<String[]> courses = CourseManager.getCourses();
        for (String[] course : courses) {
            if (course.length >= 2 && course[1].equals(courseName)) {
                return course[0]; // Código del curso
            }
        }
        return null;
    }
    
    private void editSelectedGroup() {
        int selectedRow = groupTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo para editar");
            return;
        }

        String groupNumber = (String) groupTable.getValueAt(selectedRow, 0);
        
        // Diálogo simple para editar semestre y profesor
        showEditGroupDialog(groupNumber);
    }
    
    private void showEditGroupDialog(String groupNumber) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Editar Grupo: " + groupNumber);
        dialog.setModal(true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField txtSemester = new JTextField();
        JComboBox<String> comboProfessor = new JComboBox<>();
        JTextField txtDays = new JTextField();
        JTextField txtTimes = new JTextField();

        loadProfessorsToComboBox(comboProfessor);

        formPanel.add(new JLabel("Nuevo Semestre:"));
        formPanel.add(txtSemester);
        formPanel.add(new JLabel("Asignar Profesor:"));
        formPanel.add(comboProfessor);
        formPanel.add(new JLabel("Nuevos Días:"));
        formPanel.add(txtDays);
        formPanel.add(new JLabel("Nuevos Horarios:"));
        formPanel.add(txtTimes);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Guardar Cambios");
        JButton btnCancel = new JButton("Cancelar");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(formPanel);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            String newSemester = txtSemester.getText().trim();
            String selectedProfessor = (String) comboProfessor.getSelectedItem();
            String newDays = txtDays.getText().trim();
            String newTimes = txtTimes.getText().trim();

            if (!newSemester.isEmpty()) {
                GroupManager.updateSemester(groupNumber, newSemester);
            }

            if (selectedProfessor != null && !selectedProfessor.equals("Sin asignar")) {
                GroupManager.assignProfessor(groupNumber, getProfessorUsernameByName(selectedProfessor));
            }

            if (!newDays.isEmpty() && !newTimes.isEmpty()) {
                String[] daysArray = newDays.split(";");
                String[] timesArray = newTimes.split(";");
                if (daysArray.length == timesArray.length) {
                    GroupManager.updateSchedule(groupNumber, daysArray, timesArray);
                } else {
                    JOptionPane.showMessageDialog(dialog, "La cantidad de días y horarios debe coincidir");
                    return;
                }
            }

            JOptionPane.showMessageDialog(dialog, "Grupo actualizado correctamente");
            dialog.dispose();
            loadGroupData();
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void loadProfessorsToComboBox(JComboBox<String> combo) {
        combo.removeAllItems();
        combo.addItem("Sin asignar");
        List<String[]> professors = ProfessorManager.getProfessors();
        for (String[] professor : professors) {
            if (professor.length >= 6) {
                String fullName = professor[4] + " " + professor[5];
                combo.addItem(fullName);
            }
        }
    }
    
    private String getProfessorUsernameByName(String fullName) {
        List<String[]> professors = ProfessorManager.getProfessors();
        for (String[] professor : professors) {
            if (professor.length >= 6) {
                String professorFullName = professor[4] + " " + professor[5];
                if (professorFullName.equals(fullName)) {
                    return professor[0]; // Username
                }
            }
        }
        return null;
    }
    
    private void deleteSelectedGroup() {
        int selectedRow = groupTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un grupo para eliminar");
            return;
        }

        String groupNumber = (String) groupTable.getValueAt(selectedRow, 0);
        String courseName = (String) groupTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar grupo " + groupNumber + " de " + courseName + "?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            GroupManager.deleteGroup(groupNumber);
            loadGroupData();
            JOptionPane.showMessageDialog(this, "Grupo eliminado correctamente");
        }
    }
}
