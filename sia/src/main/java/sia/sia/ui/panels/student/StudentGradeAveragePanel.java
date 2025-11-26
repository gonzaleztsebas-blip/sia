package sia.sia.ui.panels.student;

import sia.sia.business.GradeManager;
import sia.sia.data.Grade;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import sia.sia.ui.UIColors;

public class StudentGradeAveragePanel extends JPanel {
    private String currentUser;
    
    public StudentGradeAveragePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Promedio Académico"));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JLabel paLabel = new JLabel("PA (Promedio Acumulado): --");
        paLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel pappaLabel = new JLabel("PAPPA (Promedio Aritmético Ponderado Acumulado): --");
        pappaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> updateAverage(paLabel, pappaLabel));
        
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(paLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(pappaLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(refreshBtn);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel, BorderLayout.CENTER);
        
        updateAverage(paLabel, pappaLabel);
    }
    
    private void updateAverage(JLabel paLabel, JLabel pappaLabel) {
        try {
            List<Grade> grades = GradeManager.getGradesByStudent(currentUser);
            
            if (!grades.isEmpty()) {
                // Calcular PA (Promedio Acumulado) - promedio simple de todas las calificaciones
                double totalPA = 0;
                for (Grade grade : grades) {
                    totalPA += grade.getGrade();
                }
                double pa = totalPA / grades.size();
                
                // Calcular PAPPA - simulando ponderación por período (asumiendo últimas 4 calificaciones como período actual)
                double totalPAPPA = 0;
                int pappaCount = Math.min(4, grades.size());
                for (int i = 0; i < pappaCount; i++) {
                    totalPAPPA += grades.get(grades.size() - 1 - i).getGrade();
                }
                double pappa = totalPAPPA / pappaCount;
                
                paLabel.setText(String.format("PA (Promedio Acumulado): %.2f", pa));
                pappaLabel.setText(String.format("PAPPA (Ponderado Por Período): %.2f", pappa));
            } else {
                paLabel.setText("PA (Promedio Acumulado): Sin calificaciones");
                pappaLabel.setText("PAPPA (Ponderado Por Período): Sin calificaciones");
            }
        } catch (Exception e) {
            paLabel.setText("PA (Promedio Acumulado): Error al calcular");
            pappaLabel.setText("PAPPA (Ponderado Por Período): Error al calcular");
        }
    }
}
