package vista;

import controlador.*;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EstudiantePanel extends JPanel {
    private final EstudianteControlador controlador;

    private final JTextField campoRut = new JTextField(10);
    private final JTextField campoNombre = new JTextField(15);
    private final JTextField campoCurso = new JTextField(8);
    private final JTextField campoCorreo = new JTextField(15);

    // lista estudiantes mismo orden fila tabla
    private List<Estudiante> estudiantes = List.of();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new String[]{"ID", "RUT", "Nombre", "Curso", "Correo"}, 0);
    private final JTable tabla = new JTable(modeloTabla);


    public EstudiantePanel(EstudianteControlador controlador) {
        this.controlador = controlador;

        construirInterfaz();
    }


    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));

        // celdas no editables
        tabla.setDefaultEditor(Object.class, null);

        // formulario en 2 filas
        JPanel formulario = new JPanel(new GridLayout(2, 1));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fila1.add(new JLabel("RUT:"));
        fila1.add(campoRut);
        fila1.add(new JLabel("Nombre:"));
        fila1.add(campoNombre);
        fila1.add(new JLabel("Curso:"));
        fila1.add(campoCurso);
        fila1.add(new JLabel("Correo:"));
        fila1.add(campoCorreo);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton botonAgregar = new JButton("Agregar");
        JButton botonModificar = new JButton("Modificar seleccionado");
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        fila2.add(botonAgregar);
        fila2.add(botonModificar);
        fila2.add(botonEliminar);

        formulario.add(fila1);
        formulario.add(fila2);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // eventos botón
        botonAgregar.addActionListener(e -> agregar());
        botonModificar.addActionListener(e -> modificar());
        botonEliminar.addActionListener(e -> eliminar());

        // al seleccionar una fila se cargan sus datos en el formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccion();
            }
        });
    }


    private void agregar() {
        try {
            controlador.crearEstudiante(
                    campoNombre.getText().trim(),
                    campoRut.getText().trim(),
                    campoCurso.getText().trim(),
                    campoCorreo.getText().trim());

            limpiar();
            actualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla");
            return;
        }

        try {
            controlador.actualizarEstudiante(
                    estudiantes.get(fila).getId(),
                    campoNombre.getText().trim(),
                    campoRut.getText().trim(),
                    campoCurso.getText().trim(),
                    campoCorreo.getText().trim());

            limpiar();
            actualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar el estudiante seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarEstudiante(estudiantes.get(fila).getId());

            limpiar();
            actualizar();
        } catch (Exception ex) {
            // si eje el estudiante tiene préstamos asociados
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    // pasa los datos de la fila seleccionada al formulario
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }

        Estudiante e = estudiantes.get(fila);
        campoRut.setText(e.getRut());
        campoNombre.setText(e.getNombre());
        campoCurso.setText(e.getCurso());
        campoCorreo.setText(e.getCorreo());
    }


    private void limpiar() {
        campoRut.setText("");
        campoNombre.setText("");
        campoCurso.setText("");
        campoCorreo.setText("");
        tabla.clearSelection();
    }


    // recarga la tabla
    public void actualizar() {
        try {
            estudiantes = controlador.listar();

            modeloTabla.setRowCount(0);
            for (Estudiante e : estudiantes) {
                modeloTabla.addRow(new Object[]{e.getId(), e.getRut(), e.getNombre(), e.getCurso(), e.getCorreo()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}