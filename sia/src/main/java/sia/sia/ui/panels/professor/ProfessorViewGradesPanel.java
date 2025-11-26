package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.business.GradeManager;
import sia.sia.data.Group;
import sia.sia.data.Student;
import sia.sia.data.Grade;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import sia.sia.ui.UIColors;

public class ProfessorViewGradesPanel extends JPanel {
    private String currentUser;
    
    public ProfessorViewGradesPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Ver Calificaciones del Grupo"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Número del Grupo:");
        JTextField groupField = new JTextField();
        
        JButton viewBtn = new JButton("Ver Calificaciones");
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Usuario", "Nombre", "Apellido", "Calificación"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        viewBtn.addActionListener(e -> {
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
            
            model.setRowCount(0);
            List<Student> students = group.getAttendedBy();
            if (students != null) {
                for (Student student : students) {
                    Grade grade = GradeManager.findGrade(student.getUser(), groupNumber);
                    String gradeStr = grade != null ? String.format("%.2f", grade.getGrade()) : "Pendiente";
                    model.addRow(new Object[]{
                        student.getUser(),
                        student.getFirstName(),
                        student.getLastName(),
                        gradeStr
                    });
                }
            }
        });
        
        formPanel.add(groupLabel);
        formPanel.add(groupField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(viewBtn);
        
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}
