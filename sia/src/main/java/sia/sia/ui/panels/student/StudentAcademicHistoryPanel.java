package sia.sia.ui.panels.student;

import sia.sia.business.GradeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
        GradeManager.listStudentGrades(currentUser);
    }
}
