package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.business.GradeManager;
import sia.sia.data.Group;
import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;

public class ProfessorGroupAveragePanel extends JPanel {
    private String currentUser;
    
    public ProfessorGroupAveragePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Promedio del Grupo"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Número del Grupo:");
        JTextField groupField = new JTextField();
        
        JLabel averageLabel = new JLabel("Promedio: -");
        averageLabel.setHorizontalAlignment(JLabel.CENTER);
        averageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton calculateBtn = new JButton("Calcular Promedio");
        calculateBtn.addActionListener(e -> {
            String groupNumber = groupField.getText().trim();
            if (groupNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un número de grupo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Group group = GroupManager.findGroup(groupNumber);
            if (group == null || group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
                JOptionPane.showMessageDialog(this, "Grupo no encontrado o sin permiso", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double average = GradeManager.calculateGroupAverage(groupNumber);
            averageLabel.setText(String.format("Promedio: %.2f", average));
        });
        
        formPanel.add(groupLabel);
        formPanel.add(groupField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(calculateBtn);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(averageLabel);
        
        add(formPanel, BorderLayout.NORTH);
    }
}
