package sia.sia.ui.panels.student;

import sia.sia.business.StudentManager;
import sia.sia.data.Student;
import javax.swing.*;
import java.awt.*;
import sia.sia.ui.UIColors;

public class StudentCreditsBreakdownPanel extends JPanel {
    private String currentUser;
    
    public StudentCreditsBreakdownPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new GridLayout(2, 2, 15, 15));
        setBackground(UIColors.PANEL_SOFT);
        setBorder(BorderFactory.createTitledBorder("Desglose de Créditos Aprobados"));
        
        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.addActionListener(e -> updateCreditsBreakdown());
        
        add(createCreditCard("Fundamentación", "0"));
        add(createCreditCard("Disciplinar", "0"));
        add(createCreditCard("Libre Elección", "0"));
        add(createCreditCard("Nivelación", "0"));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(refreshBtn);
        
        // Usar BorderLayout para permitir agregar el botón abajo
        setLayout(new BorderLayout());
        JPanel creditsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        creditsPanel.add(createCreditCard("Fundamentación", "0"));
        creditsPanel.add(createCreditCard("Disciplinar", "0"));
        creditsPanel.add(createCreditCard("Libre Elección", "0"));
        creditsPanel.add(createCreditCard("Nivelación", "0"));
        
        add(creditsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        updateCreditsBreakdown();
    }
    
    private JPanel createCreditCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        card.setBackground(new Color(245, 247, 250)); // UIColors.BACKGROUND
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setForeground(new Color(44, 62, 80)); // UIColors.LABEL_TEXT
        labelComp.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.BOLD, 24));
        valueComp.setForeground(new Color(52, 152, 219)); // UIColors.BUTTON
        valueComp.setHorizontalAlignment(JLabel.CENTER);
        
        card.add(labelComp, BorderLayout.NORTH);
        card.add(valueComp, BorderLayout.CENTER);
        
        return card;
    }
    
    private void updateCreditsBreakdown() {
        Student student = StudentManager.findStudent(currentUser);
        if (student != null) {
            int[] credits = student.getApprovedCredits();
            
            removeAll();
            setLayout(new BorderLayout());
            
            JPanel creditsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
            creditsPanel.setBorder(BorderFactory.createTitledBorder("Desglose de Créditos Aprobados"));
            
            creditsPanel.add(createCreditCardWithValue("Fundamentación", String.valueOf(credits[0])));
            creditsPanel.add(createCreditCardWithValue("Disciplinar", String.valueOf(credits[1])));
            creditsPanel.add(createCreditCardWithValue("Libre Elección", String.valueOf(credits[2])));
            creditsPanel.add(createCreditCardWithValue("Nivelación", String.valueOf(credits[3])));
            
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton refreshBtn = new JButton("Actualizar");
            refreshBtn.addActionListener(e -> updateCreditsBreakdown());
            bottomPanel.add(refreshBtn);
            
            add(creditsPanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
            
            revalidate();
            repaint();
        }
    }
    
    private JPanel createCreditCardWithValue(String label, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        card.setBackground(new Color(245, 247, 250)); // UIColors.BACKGROUND
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setForeground(new Color(44, 62, 80)); // UIColors.LABEL_TEXT
        labelComp.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.BOLD, 24));
        valueComp.setForeground(new Color(52, 152, 219)); // UIColors.BUTTON
        valueComp.setHorizontalAlignment(JLabel.CENTER);
        
        card.add(labelComp, BorderLayout.NORTH);
        card.add(valueComp, BorderLayout.CENTER);
        
        return card;
    }
}
