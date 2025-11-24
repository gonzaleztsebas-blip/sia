/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sia.sia.ui.swing.panels;

import javax.swing.*;
import java.awt.*;
import sia.sia.business.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class EnrollmentManagementPanel extends JPanel {
    private JTable enrollmentTable;
    private JButton btnEnroll, btnUnenroll, btnRefresh;
    private DefaultTableModel tableModel;
    
    public EnrollmentManagementPanel() {
        initComponents();
        loadEnrollmentData();
        addActionListeners();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEnroll = new JButton("Inscribir Estudiante");
        btnUnenroll = new JButton("Retirar Estudiante");
        btnRefresh = new JButton("Actualizar");
        
        toolbar.add(btnEnroll);
        toolbar.add(btnUnenroll);
        toolbar.add(btnRefresh);
        add(toolbar, BorderLayout.NORTH);
        
        // Tabla
        String[] columnNames = {"Estudiante", "Grupo", "Curso", "Semestre", "Estado", "Fecha"};
        tableModel = new DefaultTableModel(columnNames, 0);
        enrollmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addActionListeners() {
        btnEnroll.addActionListener(e -> showEnrollStudentDialog());
        btnUnenroll.addActionListener(e -> unenrollSelectedStudent());
        btnRefresh.addActionListener(e -> loadEnrollmentData());
    }
    
    private void loadEnrollmentData() {
        tableModel.setRowCount(0);
        List<String[]> enrollments = EnrollmentManager.getEnrollments();

        for (String[] enrollment : enrollments) {
            if (enrollment.length >= 5 && "ACTIVE".equals(enrollment[4])) { // ← SOLO ACTIVAS
                String studentName = getStudentName(enrollment[0]);
                String courseName = getCourseNameFromGroup(enrollment[1]);

                tableModel.addRow(new Object[]{
                    studentName + " (" + enrollment[0] + ")",
                    "Grupo " + enrollment[1],
                    courseName,
                    enrollment[2], // semestre
                    enrollment[4], // estado
                    enrollment[3]  // fecha
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
    
    private void showEnrollStudentDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Inscribir Estudiante");
        dialog.setModal(true);
        dialog.setSize(500, 300);
        dialog.setLayout(new BorderLayout(10, 10));

        // --- FORMULARIO ---
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        // ComboBox para estudiante
        JComboBox<String> comboStudent = new JComboBox<>();
        loadStudentsToComboBox(comboStudent);
        
        // ComboBox para grupo
        JComboBox<String> comboGroup = new JComboBox<>();
        loadGroupsToComboBox(comboGroup);
        
        formPanel.add(new JLabel("Estudiante:"));
        formPanel.add(comboStudent);
        formPanel.add(new JLabel("Grupo:"));
        formPanel.add(comboGroup);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        // --- BOTONES ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Inscribir");
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

            if (selectedStudent == null || selectedGroup == null) {
                JOptionPane.showMessageDialog(dialog, "Seleccione estudiante y grupo");
                return;
            }

            // Extraer username del estudiante y número de grupo
            String studentUsername = extractUsernameFromDisplay(selectedStudent);
            String groupNumber = extractGroupNumber(selectedGroup);

            if (studentUsername == null || groupNumber == null) {
                JOptionPane.showMessageDialog(dialog, "Error en los datos seleccionados");
                return;
            }

            // === VALIDACIÓN 1: Mismo curso ===
            if (isStudentAlreadyEnrolledInCourse(studentUsername, groupNumber)) {
                JOptionPane.showMessageDialog(dialog, 
                    "El estudiante ya está inscrito en este curso en otro grupo");
                return;
            }

            // === VALIDACIÓN 2: Mismo horario ===  
            if (hasScheduleConflict(studentUsername, groupNumber)) {
                JOptionPane.showMessageDialog(dialog,
                    "El estudiante tiene cruce de horarios con otro grupo inscrito");
                return;
            }

            // Intentar inscripción
            boolean success = EnrollmentManager.enrollStudent(studentUsername, groupNumber);

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Estudiante inscrito correctamente");
                dialog.dispose();
                loadEnrollmentData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Error en la inscripción. Verifique:\n- Cupos disponibles\n- Horarios\n- Requisitos\n- Límite de créditos");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private boolean isStudentAlreadyEnrolledInCourse(String studentUsername, String newGroupNumber) {
        // Obtener el curso del nuevo grupo
        var newGroup = GroupManager.findGroup(newGroupNumber);
        if (newGroup == null || newGroup.getRepresents() == null) return false;

        String newCourseCode = String.valueOf(newGroup.getRepresents().getCode());

        // Verificar inscripciones activas del estudiante
        List<String[]> enrollments = EnrollmentManager.getEnrollments();
        for (String[] enrollment : enrollments) {
            if (enrollment[0].equals(studentUsername) && "ACTIVE".equals(enrollment[4])) {
                var existingGroup = GroupManager.findGroup(enrollment[1]);
                if (existingGroup != null && existingGroup.getRepresents() != null) {
                    String existingCourseCode = String.valueOf(existingGroup.getRepresents().getCode());
                    if (existingCourseCode.equals(newCourseCode)) {
                        return true; // Ya está inscrito en este curso
                    }
                }
            }
        }
        return false;
    }

    private boolean hasScheduleConflict(String studentUsername, String newGroupNumber) {
        var newGroup = GroupManager.findGroup(newGroupNumber);
        if (newGroup == null) return false;

        // Obtener grupos actuales del estudiante
        var studentGroups = GroupManager.getGroupsByStudent(studentUsername);

        // Comparar horarios
        String[] newDays = newGroup.getDaysOfWeek();
        String[] newTimes = newGroup.getTimesOfDay();

        for (var existingGroup : studentGroups) {
            String[] existingDays = existingGroup.getDaysOfWeek();
            String[] existingTimes = existingGroup.getTimesOfDay();

            // Verificar si hay días en común
            for (String newDay : newDays) {
                for (String existingDay : existingDays) {
                    if (newDay.equals(existingDay)) {
                        // Mismo día, verificar horarios
                        for (String newTime : newTimes) {
                            for (String existingTime : existingTimes) {
                                if (newTime.equals(existingTime)) {
                                    return true; // Conflicto de horario
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
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
                String display = "Grupo " + group[0] + " - " + courseName + " (" + group[3] + ")";
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
        // Formato: "Nombre Apellido (username)"
        int start = display.lastIndexOf("(");
        int end = display.lastIndexOf(")");
        if (start != -1 && end != -1 && end > start) {
            return display.substring(start + 1, end);
        }
        return null;
    }
    
    private String extractGroupNumber(String display) {
        // Formato: "Grupo X - Curso (Semestre)"
        if (display.startsWith("Grupo ")) {
            String[] parts = display.split(" ");
            if (parts.length >= 2) {
                return parts[1]; // El número del grupo
            }
        }
        return null;
    }
    
    private void unenrollSelectedStudent() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una inscripción para retirar");
            return;
        }

        String studentDisplay = (String) enrollmentTable.getValueAt(selectedRow, 0);
        String groupDisplay = (String) enrollmentTable.getValueAt(selectedRow, 1);
        String estado = (String) enrollmentTable.getValueAt(selectedRow, 4);

        if (!"ACTIVE".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Solo se pueden retirar inscripciones activas");
            return;
        }

        String studentUsername = extractUsernameFromDisplay(studentDisplay);
        String groupNumber = extractGroupNumber(groupDisplay);

        if (studentUsername == null || groupNumber == null) {
            JOptionPane.showMessageDialog(this, "Error en los datos seleccionados");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Retirar a " + studentUsername + " del grupo " + groupNumber + "?", 
            "Confirmar Retiro", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = EnrollmentManager.unenrollStudent(studentUsername, groupNumber);
            if (success) {
                JOptionPane.showMessageDialog(this, "Estudiante retirado correctamente");
                loadEnrollmentData();
            } else {
                JOptionPane.showMessageDialog(this, "Error al retirar estudiante");
            }
        }
    }
}
