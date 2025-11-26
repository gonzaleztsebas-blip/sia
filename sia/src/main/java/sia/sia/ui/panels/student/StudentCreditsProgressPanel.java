package sia.sia.ui.panels.student;

import sia.sia.business.StudentManager;
import sia.sia.data.Student;
import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;

public class StudentCreditsProgressPanel extends JPanel {
    private String currentUser;
    
    // Requisitos típicos (pueden ser configurables)
    private static final int[] REQUIRED_CREDITS = {
        12,  // Fundamentación
        20,  // Disciplinar
        8,   // Libre Elección
        4    // Nivelación
    };
    
    private static final String[] CREDIT_LABELS = {
        "Fundamentación",
        "Disciplinar",
        "Libre Elección",
        "Nivelación"
    };
    
    public StudentCreditsProgressPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Progreso de Créditos"));
        
        JPanel progressPanel = new JPanel(new GridLayout(4, 1, 5, 10));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < CREDIT_LABELS.length; i++) {
            progressPanel.add(createProgressBar(CREDIT_LABELS[i], i));
        }
        
        JButton refreshBtn = new JButton("Actualizar");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshBtn.addActionListener(e -> {
            removeAll();
            initComponents();
            revalidate();
            repaint();
        });
        bottomPanel.add(refreshBtn);
        
        add(progressPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createProgressBar(String label, int typeIndex) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        
        Student student = StudentManager.findStudent(currentUser);
        int current = 0;
        if (student != null) {
            current = student.getApprovedCredits()[typeIndex];
        }
        
        int required = REQUIRED_CREDITS[typeIndex];
        int percentage = (int) ((current * 100.0) / required);
        percentage = Math.min(percentage, 100);
        
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Arial", Font.BOLD, 11));
        labelComp.setPreferredSize(new Dimension(100, 20));
        
        JProgressBar progressBar = new JProgressBar(0, required);
        progressBar.setValue(current);
        progressBar.setStringPainted(true);
        progressBar.setString(current + " / " + required + " (" + percentage + "%)");
        progressBar.setForeground(percentage >= 100 ? new Color(46, 204, 113) : new Color(52, 152, 219));
        
        panel.add(labelComp, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
}
