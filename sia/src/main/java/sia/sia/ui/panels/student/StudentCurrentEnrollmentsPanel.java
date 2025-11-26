package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import sia.sia.ui.UIColors;
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
            new String[]{"Grupo", "Materia", "Créditos", "Componente", "Profesor"}, 0
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
            int[] credits = group.getRepresents().getCredits();
            int totalCredits = 0;
            String component = "";
            
            // Encontrar cuál componente tiene créditos
            if (credits[0] > 0) { totalCredits = credits[0]; component = "Fundamentación"; }
            else if (credits[1] > 0) { totalCredits = credits[1]; component = "Disciplinar"; }
            else if (credits[2] > 0) { totalCredits = credits[2]; component = "Libre Elección"; }
            else if (credits[3] > 0) { totalCredits = credits[3]; component = "Nivelación"; }
            
            model.addRow(new Object[]{
                String.valueOf(group.getNumber()),
                group.getRepresents().getName(),
                totalCredits,
                component,
                group.getTaughtBy() != null ? group.getTaughtBy().getFirstName() : "N/A"
            });
        }
    }
}
