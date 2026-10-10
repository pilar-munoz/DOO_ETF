package vista;

import controlador.*;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportePanel extends JPanel {
    private final ReporteControlador controlador;
    private final EstudianteControlador controladorEstudiante;

    private final JComboBox<String> comboReportes = new JComboBox<>(new String[]{
            "Libros actualmente en préstamo",
            "Libros más prestados",
            "Historial por estudiante"});

    private final JComboBox<String> comboEstudiantes = new JComboBox<>();
    private List<Estudiante> estudiantes = List.of();

    // las columnas cambian según reporte
    private final DefaultTableModel modeloTabla = new DefaultTableModel();
    private final JTable tabla = new JTable(modeloTabla);


    public ReportePanel(ReporteControlador controlador, EstudianteControlador controladorEstudiante) {
        this.controlador = controlador;
        this.controladorEstudiante = controladorEstudiante;

        construirInterfaz();
    }


    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        tabla.setDefaultEditor(Object.class, null);

        // selector reporte
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selector.add(new JLabel("Reporte:"));
        selector.add(comboReportes);
        selector.add(new JLabel("Estudiante:"));
        selector.add(comboEstudiantes);

        JButton botonGenerar = new JButton("Generar");
        selector.add(botonGenerar);

        comboEstudiantes.setEnabled(false);

        add(selector, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // eventos botón y combo
        botonGenerar.addActionListener(e -> generar());
        comboReportes.addActionListener(e ->
                comboEstudiantes.setEnabled(comboReportes.getSelectedIndex() == 2));
    }


    // genera reporte seleccionado
    private void generar() {
        try {
            String[] columnas;
            List<String[]> filas;

            int tipo = comboReportes.getSelectedIndex();

            if (tipo == 0) {
                columnas = new String[]{"Libro", "Estudiante", "Fecha préstamo", "Devolver antes de", "Estado"};
                filas = controlador.librosEnPrestamo();
            } else if (tipo == 1) {
                columnas = new String[]{"Título", "Autor", "Veces prestado"};
                filas = controlador.librosMasPrestados();
            } else {
                int indice = comboEstudiantes.getSelectedIndex();
                if (indice < 0) {
                    JOptionPane.showMessageDialog(this, "Seleccione un estudiante");
                    return;
                }

                columnas = new String[]{"Libro", "Fecha préstamo", "Devolver antes de", "Estado"};
                filas = controlador.historialEstudiante(estudiantes.get(indice).getId());
            }

            modeloTabla.setRowCount(0);
            modeloTabla.setColumnIdentifiers(columnas);
            for (String[] fila : filas) {
                modeloTabla.addRow(fila);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    // recarga estudiantes
    public void actualizar() {
        try {
            estudiantes = controladorEstudiante.listar();

            comboEstudiantes.removeAllItems();
            for (Estudiante e : estudiantes) {
                comboEstudiantes.addItem(e.getRut() + " - " + e.getNombre());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}