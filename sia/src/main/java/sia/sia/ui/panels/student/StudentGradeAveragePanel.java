package sia.sia.ui.panels.student;

import sia.sia.business.GradeManager;
import sia.sia.business.StudentManager;
import sia.sia.data.Grade;
import sia.sia.data.Course;
import sia.sia.data.Student;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentGradeAveragePanel extends JPanel {
    private String currentUser;
    private JLabel papaValueLabel;
    private JLabel paValueLabel;
    private JLabel creditsValueLabel;
    
    public StudentGradeAveragePanel(String currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Promedios Académicos"));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Crear los componentes directamente
        papaValueLabel = new JLabel("Calculando...");
        paValueLabel = new JLabel("Calculando...");
        creditsValueLabel = new JLabel("Calculando...");
        
        // Panel para PAPA
        JPanel papaPanel = createMetricPanel("PAPA", "Promedio Académico Ponderado Acumulado", 
                                           "Incluye TODAS las materias", papaValueLabel);
        
        // Panel para PA
        JPanel paPanel = createMetricPanel("PA", "Promedio Académico", 
                                         "Solo materias APROBADAS (≥ 3.0)", paValueLabel);
        
        // Panel para créditos aprobados
        JPanel creditsPanel = createMetricPanel("Créditos Aprobados", "Total de créditos aprobados", 
                                              "Fundamentación + Disciplinar + Libre Elección + Nivelación", creditsValueLabel);
        
        JButton refreshBtn = new JButton("Actualizar Promedios");
        refreshBtn.addActionListener(e -> {
            updateAveragesWithRecalculation();
        });
        
        mainPanel.add(papaPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(paPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(creditsPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(refreshBtn);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel, BorderLayout.CENTER);
        
        updateAveragesWithRecalculation();
    }
    
    private JPanel createMetricPanel(String title, String description, String detail, JLabel valueLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Título principal
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Configurar el label del valor
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 100, 0));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Descripción
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Detalle
        JLabel detailLabel = new JLabel(detail);
        detailLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        detailLabel.setForeground(Color.LIGHT_GRAY);
        detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(detailLabel);
        
        return panel;
    }
    
    private void updateAveragesWithRecalculation() {
        try {
            // OPCIÓN 1: Usar el cálculo directo (más confiable)
            calculateAveragesDirectly();
            
        } catch (Exception e) {
            e.printStackTrace();
            setErrorLabels();
        }
    }
    
    private void calculateAveragesDirectly() {
        try {
            List<Grade> grades = GradeManager.getGradesByStudent(currentUser);
            
            if (grades.isEmpty()) {
                papaValueLabel.setText("0.00");
                paValueLabel.setText("0.00");
                creditsValueLabel.setText("0");
                papaValueLabel.setForeground(Color.GRAY);
                paValueLabel.setForeground(Color.GRAY);
                creditsValueLabel.setForeground(Color.GRAY);
                return;
            }
            
            // Calcular PAPA (todas las materias)
            double sumaPonderadaTotal = 0.0;
            int creditosTotales = 0;
            
            // Calcular PA (solo aprobadas)
            double sumaPonderadaAprobadas = 0.0;
            int creditosAprobados = 0;
            
            // Contar créditos aprobados por tipo
            int[] creditosAprobadosPorTipo = new int[4];
            
            for (Grade grade : grades) {
                Course course = grade.getGroup().getRepresents();
                int[] courseCredits = course.getCredits();
                int totalCourseCredits = courseCredits[0] + courseCredits[1] + courseCredits[2] + courseCredits[3];
                
                // PAPA: todas las materias
                sumaPonderadaTotal += grade.getGrade() * totalCourseCredits;
                creditosTotales += totalCourseCredits;
                
                // PA: solo aprobadas
                if (grade.getGrade() >= 3.0) {
                    sumaPonderadaAprobadas += grade.getGrade() * totalCourseCredits;
                    creditosAprobados += totalCourseCredits;
                    
                    // Sumar créditos por tipo para los aprobados
                    for (int i = 0; i < 4; i++) {
                        creditosAprobadosPorTipo[i] += courseCredits[i];
                    }
                }
            }
            
            double papa = creditosTotales > 0 ? sumaPonderadaTotal / creditosTotales : 0.0;
            double pa = creditosAprobados > 0 ? sumaPonderadaAprobadas / creditosAprobados : 0.0;
            int totalCreditsAprobados = creditosAprobadosPorTipo[0] + creditosAprobadosPorTipo[1] + 
                                      creditosAprobadosPorTipo[2] + creditosAprobadosPorTipo[3];
            
            // Actualizar labels
            papaValueLabel.setText(String.format("%.2f", papa));
            papaValueLabel.setForeground(getColorForGrade(papa));
            
            paValueLabel.setText(String.format("%.2f", pa));
            paValueLabel.setForeground(getColorForGrade(pa));
            
            creditsValueLabel.setText(String.valueOf(totalCreditsAprobados));
            creditsValueLabel.setForeground(totalCreditsAprobados > 0 ? new Color(0, 100, 0) : Color.GRAY);
            
            // Opcional: Mostrar detalle de créditos en tooltip
            String creditsTooltip = String.format(
                "Fundamentación: %d, Disciplinar: %d, Libre Elección: %d, Nivelación: %d",
                creditosAprobadosPorTipo[0], creditosAprobadosPorTipo[1], 
                creditosAprobadosPorTipo[2], creditosAprobadosPorTipo[3]
            );
            creditsValueLabel.setToolTipText(creditsTooltip);
            
        } catch (Exception e) {
            e.printStackTrace();
            setErrorLabels();
        }
    }
    
    private void updateAveragesFromStudent() {
        try {
            // Forzar recarga del StudentManager
            StudentManager.reload();
            
            // Obtener el estudiante actualizado
            Student student = StudentManager.findStudent(currentUser);
            
            if (student != null) {
                // Obtener PAPA y PA directamente del estudiante
                double papa = student.getPapa();
                double pa = student.getPa();
                int totalCredits = student.getTotalApprovedCredits();
                
                // Actualizar labels
                papaValueLabel.setText(String.format("%.2f", papa));
                papaValueLabel.setForeground(getColorForGrade(papa));
                
                paValueLabel.setText(String.format("%.2f", pa));
                paValueLabel.setForeground(getColorForGrade(pa));
                
                creditsValueLabel.setText(String.valueOf(totalCredits));
                creditsValueLabel.setForeground(totalCredits > 0 ? new Color(0, 100, 0) : Color.GRAY);
                
            } else {
                setErrorLabels();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Si falla, intentar con cálculo directo
            calculateAveragesDirectly();
        }
    }
    
    private Color getColorForGrade(double grade) {
        if (grade >= 4.0) return new Color(0, 128, 0); // Verde - Excelente
        if (grade >= 3.5) return new Color(0, 100, 200); // Azul - Bueno
        if (grade >= 3.0) return new Color(255, 165, 0); // Naranja - Aprobado
        return new Color(220, 0, 0); // Rojo - Reprobado
    }
    
    private void setErrorLabels() {
        papaValueLabel.setText("Error");
        papaValueLabel.setForeground(Color.RED);
        
        paValueLabel.setText("Error");
        paValueLabel.setForeground(Color.RED);
        
        creditsValueLabel.setText("Error");
        creditsValueLabel.setForeground(Color.RED);
    }
}