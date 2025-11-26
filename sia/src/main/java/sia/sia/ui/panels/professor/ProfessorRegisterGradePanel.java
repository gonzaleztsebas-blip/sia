package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.business.GradeManager;
import sia.sia.data.Group;
import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;

public class ProfessorRegisterGradePanel extends JPanel {
    private String currentUser;
    
    public ProfessorRegisterGradePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Registrar Calificación"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Número del Grupo:");
        JTextField groupField = new JTextField();
        
        JLabel studentLabel = new JLabel("Usuario del Estudiante:");
        JTextField studentField = new JTextField();
        
        JLabel gradeLabel = new JLabel("Calificación (0.0 - 5.0):");
        JTextField gradeField = new JTextField();
        
        JButton registerBtn = new JButton("Registrar");
        registerBtn.addActionListener(e -> {
            String groupNumber = groupField.getText().trim();
            String studentUser = studentField.getText().trim();
            String gradeStr = gradeField.getText().trim();
            
            if (groupNumber.isEmpty() || studentUser.isEmpty() || gradeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Group group = GroupManager.findGroup(groupNumber);
            if (group == null || group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
                JOptionPane.showMessageDialog(this, "Grupo no encontrado o sin permiso", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double grade = Double.parseDouble(gradeStr);
                if (grade < 0.0 || grade > 5.0) {
                    JOptionPane.showMessageDialog(this, "La calificación debe estar entre 0.0 y 5.0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                GradeManager.createGrade(studentUser, groupNumber, grade);
                JOptionPane.showMessageDialog(this, "Calificación registrada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                groupField.setText("");
                studentField.setText("");
                gradeField.setText("");
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
        formPanel.add(gradeLabel);
        formPanel.add(gradeField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(registerBtn);
        
        add(formPanel, BorderLayout.NORTH);
    }
}
