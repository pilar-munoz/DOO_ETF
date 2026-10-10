package vista;

import controlador.*;
import modelo.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibroPanel extends JPanel {
    private final LibroControlador controlador;
    private final CategoriaControlador controladorCategoria;

    private final JTextField campoTitulo = new JTextField(12);
    private final JTextField campoAutor = new JTextField(12);
    private final JTextField campoIsbn = new JTextField(12);
    private final JTextField campoEditorial = new JTextField(12);
    private final JTextField campoStock = new JTextField(5);
    private final JComboBox<String> comboCategorias = new JComboBox<>();

    private List<Categoria> categorias = List.of();
    private List<Libro> libros = List.of();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new String[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"}, 0);
    private final JTable tabla = new JTable(modeloTabla);


    public LibroPanel(LibroControlador controlador, CategoriaControlador controladorCategoria) {
        this.controlador = controlador;
        this.controladorCategoria = controladorCategoria;

        construirInterfaz();
    }


    private void construirInterfaz() {
        setLayout(new BorderLayout(10, 10));
        tabla.setDefaultEditor(Object.class, null);

        JPanel formulario = new JPanel(new GridLayout(3, 1));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fila1.add(new JLabel("Título:"));
        fila1.add(campoTitulo);
        fila1.add(new JLabel("Autor:"));
        fila1.add(campoAutor);
        fila1.add(new JLabel("ISBN:"));
        fila1.add(campoIsbn);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fila2.add(new JLabel("Editorial:"));
        fila2.add(campoEditorial);
        fila2.add(new JLabel("Stock:"));
        fila2.add(campoStock);
        fila2.add(new JLabel("Categoría:"));
        fila2.add(comboCategorias);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton botonAgregar = new JButton("Agregar");
        JButton botonModificar = new JButton("Modificar seleccionado");
        JButton botonEliminar = new JButton("Eliminar seleccionado");
        fila3.add(botonAgregar);
        fila3.add(botonModificar);
        fila3.add(botonEliminar);

        formulario.add(fila1);
        formulario.add(fila2);
        formulario.add(fila3);

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
        int indiceCategoria = comboCategorias.getSelectedIndex();
        if (indiceCategoria < 0) {
            JOptionPane.showMessageDialog(this, "Debe existir al menos una categoría");
            return;
        }

        try {
            controlador.crearLibro(
                    campoTitulo.getText().trim(),
                    campoAutor.getText().trim(),
                    campoIsbn.getText().trim(),
                    campoEditorial.getText().trim(),
                    Integer.parseInt(campoStock.getText().trim()),
                    categorias.get(indiceCategoria).getId());

            limpiar();
            actualizar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    private void modificar() {
        int fila = tabla.getSelectedRow();
        int indiceCategoria = comboCategorias.getSelectedIndex();

        if (fila < 0 || indiceCategoria < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla");
            return;
        }

        try {
            controlador.actualizarLibro(
                    libros.get(fila).getId(),
                    campoTitulo.getText().trim(),
                    campoAutor.getText().trim(),
                    campoIsbn.getText().trim(),
                    campoEditorial.getText().trim(),
                    Integer.parseInt(campoStock.getText().trim()),
                    categorias.get(indiceCategoria).getId());

            limpiar();
            actualizar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número entero");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar el libro seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            controlador.eliminarLibro(libros.get(fila).getId());

            limpiar();
            actualizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }


    // pasa los datos de la fila seleccionada al formulario
    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return;
        }

        Libro l = libros.get(fila);
        campoTitulo.setText(l.getTitulo());
        campoAutor.setText(l.getAutor());
        campoIsbn.setText(l.getIsbn());
        campoEditorial.setText(l.getEditorial());
        campoStock.setText(String.valueOf(l.getStock()));

        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == l.getIdCategoria()) {
                comboCategorias.setSelectedIndex(i);
            }
        }
    }


    private void limpiar() {
        campoTitulo.setText("");
        campoAutor.setText("");
        campoIsbn.setText("");
        campoEditorial.setText("");
        campoStock.setText("");
        tabla.clearSelection();
    }


    // recarga combo de categorías y tabla
    public void actualizar() {
        try {
            categorias = controladorCategoria.listar();
            comboCategorias.removeAllItems();
            for (Categoria c : categorias) {
                comboCategorias.addItem(c.getNombre());
            }

            libros = controlador.listar();
            modeloTabla.setRowCount(0);
            for (Libro l : libros) {
                modeloTabla.addRow(new Object[]{
                        l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(), l.getEditorial(),
                        l.getStock(), l.getCategoria().getNombre()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}