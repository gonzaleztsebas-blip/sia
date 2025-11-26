package sia.sia.ui.panels.professor;

import sia.sia.business.GroupManager;
import sia.sia.data.Group;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import sia.sia.ui.UIColors;
import java.util.List;

public class ProfessorMyGroupsPanel extends JPanel {
    private String currentUser;
    
    public ProfessorMyGroupsPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Mis Grupos Asignados"));
        
        JButton refreshBtn = new JButton("Actualizar");
        
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Grupo", "Materia", "Semestre", "Estudiantes"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        refreshBtn.addActionListener(e -> loadMyGroups(model));
        
        add(refreshBtn, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        loadMyGroups(model);
    }
    
    private void loadMyGroups(DefaultTableModel model) {
        model.setRowCount(0);
        
        List<String[]> allGroups = GroupManager.loadGroups();
        for (String[] groupData : allGroups) {
            if (groupData.length >= 6 && groupData[5].equals(currentUser)) {
                Group group = GroupManager.findGroup(groupData[0]);
                if (group != null) {
                    int studentCount = group.getAttendedBy() != null ? group.getAttendedBy().size() : 0;
                    model.addRow(new Object[]{
                        groupData[0],
                        group.getRepresents().getName(),
                        groupData[3],
                        studentCount
                    });
                }
            }
        }
    }
}
