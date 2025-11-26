package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import sia.sia.ui.UIColors;

public class StudentApprovedCreditsPanel extends JPanel {
    private String currentUser;
    private DefaultTableModel detailsModel;
    private DefaultTableModel summaryModel;
    
    // Requisitos típicos por componente
    private static final int[] REQUIRED_CREDITS = {12, 20, 8, 4}; // Fund, Disc, L.E., Niv
    
    public StudentApprovedCreditsPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Créditos Aprobados"));
        
        JButton refreshBtn = new JButton("Créditos");
        refreshBtn.addActionListener(e -> refreshData());
        
        // Panel de resumen
        JPanel summaryPanel = createSummaryPanel();
        
        // Panel de tabla de detalles
        JPanel detailsPanel = createDetailsPanel();
        
        // Panel superior con botón
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshBtn);
        
        add(topPanel, BorderLayout.NORTH);
        add(summaryPanel, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.SOUTH);
        
        refreshData();
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Resumen por Componente"));
        
        summaryModel = new DefaultTableModel(
            new String[]{
                "TIPO",
                "FUNDAM.",
                "DISCIPL.",
                "LIBRE E.",
                "NIVEL",
                "TOTAL"
            }, 0
        );
        
        // Crear filas
        summaryModel.addRow(new Object[]{"EXIGIDOS", 12, 20, 8, 4, 44});
        summaryModel.addRow(new Object[]{"APROBADOS", 0, 0, 0, 0, 0});
        summaryModel.addRow(new Object[]{"APROBADOS PLAN", 0, 0, 0, 0, 0});
        summaryModel.addRow(new Object[]{"PENDIENTES", 12, 20, 8, 4, 44});
        summaryModel.addRow(new Object[]{"TOTAL EST.", 12, 20, 8, 4, 44});
        
        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        summaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Hacer la tabla no editable
        DefaultTableModel model = (DefaultTableModel) summaryTable.getModel();
        model.setColumnIdentifiers(new String[]{"TIPO", "FUNDAM.", "DISCIPL.", "LIBRE E.", "NIVEL", "TOTAL"});
        
        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detalle de Créditos Aprobados"));
        
        detailsModel = new DefaultTableModel(
            new String[]{"Materia", "Créditos", "Componente"}, 0
        );
        
        JTable detailsTable = new JTable(detailsModel);
        detailsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void refreshData() {
        // Actualizar tabla de detalles
        detailsModel.setRowCount(0);
        
        List<Group> enrollments = EnrollmentManager.getCurrentEnrollments(currentUser);
        int[] approvedCreditsArray = new int[4];
        
        for (Group group : enrollments) {
            int[] creditsArray = group.getRepresents().getCredits();
            int groupTotal = 0;
            String component = "";
            
            // Encontrar cuál componente tiene créditos
            if (creditsArray[0] > 0) { 
                groupTotal = creditsArray[0]; 
                component = "Fundamentación"; 
                approvedCreditsArray[0] += groupTotal; 
            }
            else if (creditsArray[1] > 0) { 
                groupTotal = creditsArray[1]; 
                component = "Disciplinar"; 
                approvedCreditsArray[1] += groupTotal; 
            }
            else if (creditsArray[2] > 0) { 
                groupTotal = creditsArray[2]; 
                component = "Libre Elección"; 
                approvedCreditsArray[2] += groupTotal; 
            }
            else if (creditsArray[3] > 0) { 
                groupTotal = creditsArray[3]; 
                component = "Nivelación"; 
                approvedCreditsArray[3] += groupTotal; 
            }
            
            detailsModel.addRow(new Object[]{
                group.getRepresents().getName(),
                groupTotal,
                component
            });
        }
        
        // Calcular pendientes
        int[] pendingCredits = new int[4];
        for (int i = 0; i < 4; i++) {
            pendingCredits[i] = Math.max(0, REQUIRED_CREDITS[i] - approvedCreditsArray[i]);
        }
        
        // Actualizar tabla de resumen
        int totalApproved = approvedCreditsArray[0] + approvedCreditsArray[1] + approvedCreditsArray[2] + approvedCreditsArray[3];
        int totalRequired = REQUIRED_CREDITS[0] + REQUIRED_CREDITS[1] + REQUIRED_CREDITS[2] + REQUIRED_CREDITS[3];
        int totalPending = pendingCredits[0] + pendingCredits[1] + pendingCredits[2] + pendingCredits[3];
        
        summaryModel.setValueAt(approvedCreditsArray[0], 1, 1);
        summaryModel.setValueAt(approvedCreditsArray[1], 1, 2);
        summaryModel.setValueAt(approvedCreditsArray[2], 1, 3);
        summaryModel.setValueAt(approvedCreditsArray[3], 1, 4);
        summaryModel.setValueAt(totalApproved, 1, 5);
        
        summaryModel.setValueAt(approvedCreditsArray[0], 2, 1);
        summaryModel.setValueAt(approvedCreditsArray[1], 2, 2);
        summaryModel.setValueAt(approvedCreditsArray[2], 2, 3);
        summaryModel.setValueAt(approvedCreditsArray[3], 2, 4);
        summaryModel.setValueAt(totalApproved, 2, 5);
        
        summaryModel.setValueAt(pendingCredits[0], 3, 1);
        summaryModel.setValueAt(pendingCredits[1], 3, 2);
        summaryModel.setValueAt(pendingCredits[2], 3, 3);
        summaryModel.setValueAt(pendingCredits[3], 3, 4);
        summaryModel.setValueAt(totalPending, 3, 5);
        
        summaryModel.setValueAt(totalRequired, 4, 1);
        summaryModel.setValueAt(totalRequired, 4, 2);
        summaryModel.setValueAt(totalRequired, 4, 3);
        summaryModel.setValueAt(totalRequired, 4, 4);
        summaryModel.setValueAt(totalRequired, 4, 5);
    }
}
