package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentAvailableCoursesPanel extends JPanel {
    private String currentUser;
    
    public StudentAvailableCoursesPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Cursos Disponibles"));
        
        JButton refreshBtn = new JButton("Actualizar");
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Código", "Nombre", "Créditos"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        refreshBtn.addActionListener(e -> loadAvailableCourses(model));
        
        add(refreshBtn, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadAvailableCourses(model);
    }
    
    private void loadAvailableCourses(DefaultTableModel model) {
        model.setRowCount(0);
        
        List<Course> courses = EnrollmentManager.getAvailableCoursesForStudent(currentUser);
        for (Course course : courses) {
            model.addRow(new Object[]{
                course.getCode(),
                course.getName(),
                course.getCredits()
            });
        }
    }
}
