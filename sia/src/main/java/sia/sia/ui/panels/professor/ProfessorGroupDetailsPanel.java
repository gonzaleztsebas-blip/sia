package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.data.Group;
import javax.swing.*;
import java.awt.*;

public class ProfessorGroupDetailsPanel extends JPanel {
    private String currentUser;
    
    public ProfessorGroupDetailsPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Detalles de Grupo"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Número del Grupo:");
        JTextField groupField = new JTextField();
        
        JButton viewBtn = new JButton("Ver Detalles");
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        
        viewBtn.addActionListener(e -> {
            String groupNumber = groupField.getText().trim();
            if (groupNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un número de grupo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Group group = GroupManager.findGroup(groupNumber);
            if (group == null) {
                JOptionPane.showMessageDialog(this, "Grupo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (group.getTaughtBy() == null || !group.getTaughtBy().getUser().equals(currentUser)) {
                JOptionPane.showMessageDialog(this, "No tiene permiso para ver este grupo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StringBuilder details = new StringBuilder();
            details.append("Número: ").append(group.getNumber()).append("\n");
            details.append("Curso: ").append(group.getRepresents().getName()).append("\n");
            details.append("Código: ").append(group.getRepresents().getCode()).append("\n");
            details.append("Créditos: ").append(group.getRepresents().getCredits()).append("\n");
            details.append("Semestre: ").append(group.getSemester()).append("\n\n");
            details.append("Horario:\n");
            
            String[] days = group.getDaysOfWeek();
            String[] times = group.getTimesOfDay();
            if (days != null && times != null) {
                for (int i = 0; i < days.length && i < times.length; i++) {
                    details.append("  ").append(getDayName(days[i])).append(": ").append(times[i]).append("\n");
                }
            }
            
            int studentCount = group.getAttendedBy() != null ? group.getAttendedBy().size() : 0;
            details.append("\nEstudiantes inscritos: ").append(studentCount);
            
            detailsArea.setText(details.toString());
        });
        
        formPanel.add(groupLabel);
        formPanel.add(groupField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(viewBtn);
        
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
    }
    
    private String getDayName(String code) {
        switch (code.trim().toUpperCase()) {
            case "L": return "Lunes";
            case "M": return "Martes";
            case "W": return "Miércoles";
            case "J": return "Jueves";
            case "V": return "Viernes";
            case "S": return "Sábado";
            case "D": return "Domingo";
            default: return code;
        }
    }
}
