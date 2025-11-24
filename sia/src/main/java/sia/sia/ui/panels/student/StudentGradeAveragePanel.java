package sia.sia.ui.panels.student;

import sia.sia.business.GradeManager;
import sia.sia.data.Grade;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentGradeAveragePanel extends JPanel {
    private String currentUser;
    
    public StudentGradeAveragePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Promedio Académico"));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JLabel avgLabel = new JLabel("Promedio General: --");
        avgLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> updateAverage(avgLabel));
        
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(avgLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(refreshBtn);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel, BorderLayout.CENTER);
        
        updateAverage(avgLabel);
    }
    
    private void updateAverage(JLabel avgLabel) {
        try {
            List<Grade> grades = GradeManager.getGradesByStudent(currentUser);
            
            if (!grades.isEmpty()) {
                double total = 0;
                for (Grade grade : grades) {
                    total += grade.getGrade();
                }
                double average = total / grades.size();
                avgLabel.setText(String.format("Promedio General: %.2f", average));
            } else {
                avgLabel.setText("Promedio General: Sin calificaciones");
            }
        } catch (Exception e) {
            avgLabel.setText("Promedio General: Error al calcular");
        }
    }
}
