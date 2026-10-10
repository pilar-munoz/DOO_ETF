package vista;

import controlador.*;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrestamoPanel extends JPanel {
    private final PrestamoControlador controladorPrestamo;
    private final LibroControlador controladorLibro;
    private final EstudianteControlador controladorEstudiante;

    private final JComboBox<String> comboLibros = new JComboBox<>();
    private final JComboBox<String> comboEstudiantes = new JComboBox<>();

    private List<Libro> libros = List.of();
    private List<Estudiante> estudiantes = List.of();

    private final JButton botonPrestar = new JButton("Registrar préstamo");
    private final JButton botonDevolver = new JButton("Registrar devolución");

    // indicador barra progreso
    private final JProgressBar barra = new JProgressBar();
    private final JLabel estado = new JLabel(" ");

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new String[]{"ID", "Libro", "Estudiante", "Fecha préstamo", "Devolver antes de"}, 0);
    private final JTable tabla = new JTable(modeloTabla);

    // resultado carga segundo plano
    private static class Datos {
        final List<Libro> libros;
        final List<Estudiante> estudiantes;
        final List<Prestamo> activos;

        Datos(List<Libro> libros, List<Estudiante> estudiantes, List<Prestamo> activos) {
            this.libros = libros;
            this.estudiantes = estudiantes;
            this.activos = activos;
        }
    }


    public PrestamoPanel(PrestamoControlador controladorPrestamo,
                         LibroControlador controladorLibro,
                         EstudianteControlador controladorEstudiante) {
        this.controladorPrestamo = controladorPrestamo;
        this.controladorLibro = controladorLibro;
        this.controladorEstudiante = controladorEstudiante;

        construirInterfaz();
    }


    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        tabla.setDefaultEditor(Object.class, null);

        // formulario
        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        formulario.add(new JLabel("Libro:"));
        formulario.add(comboLibros);
        formulario.add(new JLabel("Estudiante:"));
        formulario.add(comboEstudiantes);
        formulario.add(botonPrestar);
        formulario.add(botonDevolver);

        // barra de estado
        barra.setIndeterminate(true);
        barra.setVisible(false);

        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelEstado.add(estado);
        panelEstado.add(barra);

        add(formulario, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelEstado, BorderLayout.SOUTH);

        // eventos botón
        botonPrestar.addActionListener(e -> prestar());
        botonDevolver.addActionListener(e -> devolver());
    }


    // bloquea/desbloquea la vista
    private void ocupado(boolean ocupado, String texto) {
        botonPrestar.setEnabled(!ocupado);
        botonDevolver.setEnabled(!ocupado);
        comboLibros.setEnabled(!ocupado);
        comboEstudiantes.setEnabled(!ocupado);
        barra.setVisible(ocupado);
        estado.setText(ocupado ? texto : " ");
    }


    private void prestar() {
        int indiceLibro = comboLibros.getSelectedIndex();
        int indiceEstudiante = comboEstudiantes.getSelectedIndex();

        if (indiceLibro < 0 || indiceEstudiante < 0) {
            JOptionPane.showMessageDialog(this, "Debe existir al menos un libro y un estudiante");
            return;
        }

        // los ids se leen aquí (hilo de Swing), antes de lanzar el hilo de fondo
        int idLibro = libros.get(indiceLibro).getId();
        int idEstudiante = estudiantes.get(indiceEstudiante).getId();

        ocupado(true, "Registrando préstamo y actualizando stock..");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controladorPrestamo.prestar(idLibro, idEstudiante);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    actualizar(); // recarga combos
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    ocupado(false, null);
                } catch (ExecutionException ex) {
                    ocupado(false, null);
                    JOptionPane.showMessageDialog(PrestamoPanel.this, ex.getCause().getMessage());
                }
            }
        }.execute();
    }


    private void devolver() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo activo de la tabla");
            return;
        }

        int idPrestamo = (int) modeloTabla.getValueAt(fila, 0);

        ocupado(true, "Registrando devolución y reponiendo stock...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controladorPrestamo.devolver(idPrestamo);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    actualizar();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    ocupado(false, null);
                } catch (ExecutionException ex) {
                    ocupado(false, null);
                    JOptionPane.showMessageDialog(PrestamoPanel.this, ex.getCause().getMessage());
                }
            }
        }.execute();
    }


    // recarga combos  y tabla de préstamos activos (sin congelar)
    public void actualizar() {
        ocupado(true, "Cargando datos...");

        new SwingWorker<Datos, Void>() {
            @Override
            protected Datos doInBackground() throws Exception {
                // 3 consultas corren
                return new Datos(
                        controladorLibro.listar(),
                        controladorEstudiante.listar(),
                        controladorPrestamo.listarActivos());
            }

            @Override
            protected void done() {
                try {
                    Datos datos = get();
                    libros = datos.libros;
                    estudiantes = datos.estudiantes;

                    comboLibros.removeAllItems();
                    for (Libro l : libros) {
                        comboLibros.addItem(l.getTitulo() + " (stock " + l.getStock() + ")");
                    }

                    comboEstudiantes.removeAllItems();
                    for (Estudiante e : estudiantes) {
                        comboEstudiantes.addItem(e.getRut() + " - " + e.getNombre());
                    }

                    modeloTabla.setRowCount(0);
                    for (Prestamo p : datos.activos) {
                        modeloTabla.addRow(new Object[]{
                                p.getId(), p.getTituloLibro(), p.getNombreEstudiante(),
                                p.getFechaPrestamo(), p.getFechaDevolucion()});
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(PrestamoPanel.this, ex.getCause().getMessage());
                } finally {
                    ocupado(false, null);
                }
            }
        }.execute();
    }
}