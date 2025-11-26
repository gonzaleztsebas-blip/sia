package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.data.Group;
import javax.swing.*;
import java.awt.*;

public class ProfessorGroupSchedulePanel extends JPanel {
    private String currentUser;
    
    public ProfessorGroupSchedulePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Horario del Grupo"));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel groupLabel = new JLabel("Número del Grupo:");
        JTextField groupField = new JTextField();
        
        JButton viewBtn = new JButton("Ver Horario");
        JTextArea scheduleArea = new JTextArea();
        scheduleArea.setEditable(false);
        scheduleArea.setLineWrap(true);
        
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
            
            StringBuilder schedule = new StringBuilder();
            schedule.append("Curso: ").append(group.getRepresents().getName()).append("\n");
            schedule.append("Grupo: ").append(group.getNumber()).append("\n");
            schedule.append("Semestre: ").append(group.getSemester()).append("\n\n");
            schedule.append("Horario:\n");
            
            String[] days = group.getDaysOfWeek();
            String[] times = group.getTimesOfDay();
            if (days != null && times != null) {
                for (int i = 0; i < days.length && i < times.length; i++) {
                    schedule.append("  ").append(getDayName(days[i])).append(": ").append(times[i]).append("\n");
                }
            } else {
                schedule.append("  Sin horario definido");
            }
            
            scheduleArea.setText(schedule.toString());
        });
        
        formPanel.add(groupLabel);
        formPanel.add(groupField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(viewBtn);
        
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(scheduleArea), BorderLayout.CENTER);
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
