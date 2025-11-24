package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentCurrentEnrollmentsPanel extends JPanel {
    private String currentUser;
    
    public StudentCurrentEnrollmentsPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Inscripciones Actuales"));
        
        JButton refreshBtn = new JButton("Actualizar");
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Grupo", "Materia", "Créditos", "Profesor"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        refreshBtn.addActionListener(e -> loadCurrentEnrollments(model));
        
        add(refreshBtn, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadCurrentEnrollments(model);
    }
    
    private void loadCurrentEnrollments(DefaultTableModel model) {
        model.setRowCount(0);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        for (Group group : enrollments) {
            model.addRow(new Object[]{
                String.valueOf(group.getNumber()),
                group.getRepresents().getName(),
                group.getRepresents().getCredits(),
                group.getTaughtBy() != null ? group.getTaughtBy().getFirstName() : "N/A"
            });
        }
    }
}
