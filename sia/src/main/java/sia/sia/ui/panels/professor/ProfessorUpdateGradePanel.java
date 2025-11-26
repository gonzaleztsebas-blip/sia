package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.business.GradeManager;
import sia.sia.data.Group;
import sia.sia.data.Grade;
import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;

public class ProfessorUpdateGradePanel extends JPanel {
    private String currentUser;
    
    public ProfessorUpdateGradePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Actualizar Calificación"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Número del Grupo:");
        JTextField groupField = new JTextField();
        
        JLabel studentLabel = new JLabel("Usuario del Estudiante:");
        JTextField studentField = new JTextField();
        
        JLabel currentGradeLabel = new JLabel("Calificación Actual: -");
        
        JLabel newGradeLabel = new JLabel("Nueva Calificación (0.0 - 5.0):");
        JTextField newGradeField = new JTextField();
        
        JButton viewBtn = new JButton("Ver Calificación Actual");
        viewBtn.addActionListener(e -> {
            String groupNumber = groupField.getText().trim();
            String studentUser = studentField.getText().trim();
            
            if (groupNumber.isEmpty() || studentUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese grupo y estudiante", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Grade currentGrade = GradeManager.findGrade(studentUser, groupNumber);
            if (currentGrade != null) {
                currentGradeLabel.setText(String.format("Calificación Actual: %.2f", currentGrade.getGrade()));
            } else {
                currentGradeLabel.setText("Calificación Actual: Pendiente");
            }
        });
        
        JButton updateBtn = new JButton("Actualizar");
        updateBtn.addActionListener(e -> {
            String groupNumber = groupField.getText().trim();
            String studentUser = studentField.getText().trim();
            String newGradeStr = newGradeField.getText().trim();
            
            if (groupNumber.isEmpty() || studentUser.isEmpty() || newGradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Group group = GroupManager.findGroup(groupNumber);
            if (group == null || group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
                JOptionPane.showMessageDialog(this, "Grupo no encontrado o sin permiso", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double newGrade = Double.parseDouble(newGradeStr);
                if (newGrade < 0.0 || newGrade > 5.0) {
                    JOptionPane.showMessageDialog(this, "La calificación debe estar entre 0.0 y 5.0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GradeManager.updateGrade(studentUser, groupNumber, newGrade);
                JOptionPane.showMessageDialog(this, "Calificación actualizada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                groupField.setText("");
                studentField.setText("");
                newGradeField.setText("");
                currentGradeLabel.setText("Calificación Actual: -");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Calificación inválida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        formPanel.add(groupLabel);
        formPanel.add(groupField);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(studentLabel);
        formPanel.add(studentField);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(viewBtn);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(currentGradeLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(newGradeLabel);
        formPanel.add(newGradeField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(updateBtn);
        
        add(formPanel, BorderLayout.NORTH);
    }
}
