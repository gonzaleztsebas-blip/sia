package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentSchedulePanel extends JPanel {
    private String currentUser;
    
    public StudentSchedulePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Mi Horario"));
        
        JButton refreshBtn = new JButton("Actualizar");
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Materia", "Grupo", "Día", "Hora"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        refreshBtn.addActionListener(e -> loadSchedule(model));
        
        add(refreshBtn, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadSchedule(model);
    }
    
    private void loadSchedule(DefaultTableModel model) {
        model.setRowCount(0);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        for (Group group : enrollments) {
            String[] days = group.getDaysOfWeek();
            String[] times = group.getTimesOfDay();
            
            if (days != null && times != null) {
                for (int i = 0; i < days.length && i < times.length; i++) {
                    model.addRow(new Object[]{
                        group.getRepresents().getName(),
                        String.valueOf(group.getNumber()),
                        getDayName(days[i]),
                        times[i]
                    });
                }
            }
        }
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
