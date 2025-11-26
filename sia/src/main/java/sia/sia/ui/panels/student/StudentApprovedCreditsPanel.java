package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentApprovedCreditsPanel extends JPanel {
    private String currentUser;
    
    public StudentApprovedCreditsPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Créditos Aprobados"));
        
        JButton refreshBtn = new JButton("Actualizar");
        JLabel totalLabel = new JLabel("Total Créditos: 0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Materia", "Créditos"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        refreshBtn.addActionListener(e -> loadApprovedCredits(model, totalLabel));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(refreshBtn, BorderLayout.WEST);
        topPanel.add(totalLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadApprovedCredits(model, totalLabel);
    }
    
    private void loadApprovedCredits(DefaultTableModel model, JLabel totalLabel) {
        model.setRowCount(0);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        int totalCredits = 0;
        
        for (Group group : enrollments) {
            int[] creditsArray = group.getRepresents().getCredits();
            int groupCredits = 0;
            for (int c : creditsArray) {
                groupCredits += c;
            }
            totalCredits += groupCredits;
            
            model.addRow(new Object[]{
                group.getRepresents().getName(),
                groupCredits
            });
        }
        
        totalLabel.setText("Total Créditos: " + totalCredits);
    }
}
