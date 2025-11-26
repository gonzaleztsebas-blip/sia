package sia.sia.ui.panels.student;

import sia.sia.business.GradeManager;
import sia.sia.data.Grade;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import sia.sia.ui.UIColors;

public class StudentAcademicHistoryPanel extends JPanel {
    private String currentUser;
    
    public StudentAcademicHistoryPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Historial Académico"));
        
        JButton refreshBtn = new JButton("Actualizar");
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Materia", "Grupo", "Calificación", "Estado"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        refreshBtn.addActionListener(e -> loadAcademicHistory(model));
        
        add(refreshBtn, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadAcademicHistory(model);
    }
    
    private void loadAcademicHistory(DefaultTableModel model) {
        model.setRowCount(0);
        
        List<Grade> grades = GradeManager.getGradesByStudent(currentUser);
        for (Grade grade : grades) {
            String courseName = grade.getGroup().getRepresents().getName();
            long groupNumber = grade.getGroup().getNumber();
            double gradeValue = grade.getGrade();
            String status = gradeValue >= 3.0 ? "APROBADO" : "REPROBADO";
            
            model.addRow(new Object[]{
                courseName,
                groupNumber,
                String.format("%.2f", gradeValue),
                status
            });
        }
    }
}
