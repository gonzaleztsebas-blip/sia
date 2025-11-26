package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.data.*;
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
        groupField.setMaximumSize(new Dimension(200, 25));
        
        JButton viewBtn = new JButton("Ver Detalles");
        JTextArea detailsArea = new JTextArea(15, 30);
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        
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
            
            // Construir detalles del grupo
            StringBuilder details = new StringBuilder();
            details.append("=== INFORMACIÓN DEL GRUPO ===\n\n");
            details.append("Número: ").append(group.getNumber()).append("\n");
            
            if (group.getRepresents() != null) {
                Course course = group.getRepresents();
                details.append("Curso: ").append(course.getName()).append("\n");
                details.append("Código: ").append(course.getCode()).append("\n");
                
                // MANEJO CORRECTO DE CRÉDITOS - usando el array de 4 elementos
                int[] credits = course.getCredits();
                details.append("Créditos:\n");
                details.append("  • Fundamentación: ").append(credits[Course.FUNDAMENTACION]).append("\n");
                details.append("  • Disciplinar: ").append(credits[Course.DISCIPLINAR]).append("\n");
                details.append("  • Libre Elección: ").append(credits[Course.LIBRE_ELECCION]).append("\n");
                details.append("  • Nivelación: ").append(credits[Course.NIVELACION]).append("\n");
                details.append("  • TOTAL: ").append(credits[0] + credits[1] + credits[2] + credits[3]).append("\n");
            } else {
                details.append("Curso: No asignado\n");
            }
            
            details.append("Semestre: ").append(group.getSemester()).append("\n\n");
            details.append("=== HORARIO ===\n");
            
            String[] days = group.getDaysOfWeek();
            String[] times = group.getTimesOfDay();
            if (days != null && times != null && days.length > 0 && times.length > 0) {
                for (int i = 0; i < days.length && i < times.length; i++) {
                    if (days[i] != null && times[i] != null) {
                        details.append("  • ").append(getDayName(days[i])).append(": ").append(times[i]).append("\n");
                    }
                }
            } else {
                details.append("  Horario no definido\n");
            }
            
            int studentCount = group.getAttendedBy() != null ? group.getAttendedBy().size() : 0;
            details.append("\n=== ESTUDIANTES ===\n");
            details.append("Total inscritos: ").append(studentCount).append("\n");
            
            // Mostrar lista de estudiantes si hay
            if (studentCount > 0) {
                details.append("Lista de estudiantes:\n");
                for (Student student : group.getAttendedBy()) {
                    details.append("  • ").append(student.getFirstName())
                           .append(" ").append(student.getLastName())
                           .append(" (").append(student.getUser()).append(")\n");
                }
            }
            
            detailsArea.setText(details.toString());
        });
        
        // Mejorar el layout
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(groupLabel);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(groupField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(viewBtn);
        
        formPanel.add(inputPanel);
        
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
    }
    
    private String getDayName(String code) {
        if (code == null) return "No definido";
        
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