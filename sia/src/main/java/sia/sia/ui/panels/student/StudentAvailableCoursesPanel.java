package sia.sia.ui.panels.student;

import sia.sia.business.EnrollmentManager;
import sia.sia.data.Course;
import sia.sia.ui.UIColors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentAvailableCoursesPanel extends JPanel {
    private String currentUser;
    private String[] componentOptions = {"Fundamentación", "Disciplinar", "Libre Elección", "Nivelación"};
    private JComboBox<String> componentCombo;
    private DefaultTableModel model;
    private JTable table;
    
    public StudentAvailableCoursesPanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Cursos Disponibles"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel componentLabel = new JLabel("Componente de créditos:");

        componentCombo = new JComboBox<>(componentOptions);
        componentCombo.setMaximumRowCount(4); // Limitar filas visibles
        componentCombo.setLightWeightPopupEnabled(false); // Usar ventana pesada

        // Mejorar la apariencia del combo box
        componentCombo.setBackground(Color.WHITE);
        componentCombo.setFont(componentCombo.getFont().deriveFont(Font.PLAIN));

        JButton refreshBtn = new JButton("Actualizar");

        topPanel.add(componentLabel);
        topPanel.add(componentCombo);
        topPanel.add(refreshBtn);

        model = new DefaultTableModel(
            new String[]{"Código", "Nombre", "Créditos", "Componente"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setShowGrid(true);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400)); // Tamaño preferido

        refreshBtn.addActionListener(e -> loadAvailableCourses());
        componentCombo.addActionListener(e -> loadAvailableCourses());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadAvailableCourses();
    }
    
    private void loadAvailableCourses() {
        model.setRowCount(0);
        
        // Obtener el índice del componente seleccionado
        int selectedIndex = componentCombo.getSelectedIndex();
        String selectedComponent = componentOptions[selectedIndex];
        
        List<Course> courses = EnrollmentManager.getAvailableCoursesForStudent(currentUser);
        
        for (Course course : courses) {
            int[] credits = course.getCredits();
            int totalCredits = 0;
            String component = "";
            
            // Identificar el componente del curso
            if (credits[0] > 0) { 
                totalCredits = credits[0]; 
                component = "Fundamentación"; 
            }
            else if (credits[1] > 0) { 
                totalCredits = credits[1]; 
                component = "Disciplinar"; 
            }
            else if (credits[2] > 0) { 
                totalCredits = credits[2]; 
                component = "Libre Elección"; 
            }
            else if (credits[3] > 0) { 
                totalCredits = credits[3]; 
                component = "Nivelación"; 
            }
            
            // FILTRAR: Solo agregar si coincide con el componente seleccionado
            if (component.equals(selectedComponent)) {
                model.addRow(new Object[]{
                    course.getCode(),
                    course.getName(),
                    totalCredits,
                    component
                });
            }
        }
        
        // Si no hay resultados, mostrar mensaje
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{
                "",
                "No hay cursos disponibles para este componente",
                "",
                ""
            });
        }
    }
}