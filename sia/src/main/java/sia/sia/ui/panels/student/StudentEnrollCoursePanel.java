package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.business.GroupManager;
import sia.sia.data.Course;
import sia.sia.data.Group;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import sia.sia.ui.UIColors;

public class StudentEnrollCoursePanel extends JPanel {
    private String currentUser;
    
    public StudentEnrollCoursePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Inscribir Materia"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel courseLabel = new JLabel("Seleccionar Curso:");
        JComboBox<String> courseCombo = new JComboBox<>();
        courseCombo.setMaximumRowCount(5);
        courseCombo.setLightWeightPopupEnabled(false); // ← SOLUCIÓN PARA courseCombo
        
        JLabel groupLabel = new JLabel("Seleccionar Grupo:");
        JComboBox<String> groupCombo = new JComboBox<>();
        groupCombo.setMaximumRowCount(5);
        groupCombo.setLightWeightPopupEnabled(false); // ← SOLUCIÓN PARA groupCombo
        
        JButton enrollBtn = new JButton("Inscribir");
        enrollBtn.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() < 0 || groupCombo.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar curso y grupo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String groupNumber = groupCombo.getSelectedItem().toString().split(" ")[1];
            EnrollmentManager.enrollStudent(currentUser, groupNumber);
            JOptionPane.showMessageDialog(this, "Inscripción realizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollCourseOptions(courseCombo, groupCombo);
        });
        
        courseCombo.addActionListener(e -> {
            if (courseCombo.getSelectedIndex() >= 0) {
                String courseCode = courseCombo.getSelectedItem().toString().split(" ")[0];
                loadGroupsForCourse(courseCode, groupCombo);
            }
        });
        
        formPanel.add(courseLabel);
        formPanel.add(courseCombo);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(groupLabel);
        formPanel.add(groupCombo);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(enrollBtn);
        
        add(formPanel, BorderLayout.NORTH);
        
        JButton refreshBtn = new JButton("Actualizar Cursos");
        refreshBtn.addActionListener(e -> loadEnrollCourseOptions(courseCombo, groupCombo));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadEnrollCourseOptions(courseCombo, groupCombo);
    }
    
    private void loadEnrollCourseOptions(JComboBox<String> courseCombo, JComboBox<String> groupCombo) {
        courseCombo.removeAllItems();
        groupCombo.removeAllItems();
        
        List<Course> courses = EnrollmentManager.getAvailableCoursesForStudent(currentUser);
        for (Course course : courses) {
            courseCombo.addItem(course.getCode() + " - " + course.getName());
        }
    }
    
    private void loadGroupsForCourse(String courseCode, JComboBox<String> groupCombo) {
        groupCombo.removeAllItems();
        
        List<String[]> allGroups = GroupManager.loadGroups();
        for (String[] groupData : allGroups) {
            if (groupData[4].equals(courseCode)) {
                Group group = GroupManager.findGroup(groupData[0]);
                if (group != null) {
                    int spots = GroupManager.getAvailableSpots(groupData[0]);
                    groupCombo.addItem("Grupo " + groupData[0] + " (Cupos: " + spots + ")");
                }
            }
        }
    }
}