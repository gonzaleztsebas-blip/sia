package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Group;
import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;
import java.util.List;

public class StudentWithdrawCoursePanel extends JPanel {
    private String currentUser;
    
    public StudentWithdrawCoursePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Retirar Materia"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Seleccionar Materia a Retirar:");
        JComboBox<String> groupCombo = new JComboBox<>();
        
        JButton withdrawBtn = new JButton("Retirar");
        withdrawBtn.addActionListener(e -> {
            if (groupCombo.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un grupo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String groupNumber = groupCombo.getSelectedItem().toString().split(" ")[1];
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de retirar esta materia?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                EnrollmentManager.unenrollStudent(currentUser, groupNumber);
                JOptionPane.showMessageDialog(this, "Materia retirada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                loadWithdrawOptions(groupCombo);
            }
        });
        
        formPanel.add(groupLabel);
        formPanel.add(groupCombo);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(withdrawBtn);
        
        add(formPanel, BorderLayout.NORTH);
        
        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> loadWithdrawOptions(groupCombo));
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadWithdrawOptions(groupCombo);
    }
    
    private void loadWithdrawOptions(JComboBox<String> groupCombo) {
        groupCombo.removeAllItems();
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        for (Group group : enrollments) {
            groupCombo.addItem("Grupo " + group.getNumber() + " - " + group.getRepresents().getName());
        }
    }
}
