package controlador;

import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;

import java.sql.SQLException;
import java.util.List;

public class LibroControlador {
    private final LibroDAO libroDAO;

    public LibroControlador(LibroDAO libroDAO) {
        this.libroDAO = libroDAO;
    }

    public void crearLibro(String titulo, String autor, String isbn, String editorial,
                           int stock, int idCategoria) throws SQLException {
        validar(titulo, autor, isbn, stock, idCategoria);

        // la categoría viaja solo con su id (el nombre lo trae la consulta al listar)
        libroDAO.create(new Libro(0, titulo.trim(), autor.trim(), isbn.trim(), editorial.trim(),
                stock, new Categoria(idCategoria, "")));
    }

    public List<Libro> listar() throws SQLException {
        return libroDAO.readAll();
    }

    public void actualizarLibro(int id, String titulo, String autor, String isbn, String editorial,
                                int stock, int idCategoria) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecciona un libro válido.");
        }

        validar(titulo, autor, isbn, stock, idCategoria);
        libroDAO.update(new Libro(id, titulo.trim(), autor.trim(), isbn.trim(), editorial.trim(),
                stock, new Categoria(idCategoria, "")));
    }

    public void eliminarLibro(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecciona un libro válido.");
        }
        libroDAO.delete(id);
    }

    private void validar(String titulo, String autor, String isbn, int stock, int idCategoria) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio.");
        }
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException("El autor es obligatorio.");
        }
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("El ISBN es obligatorio.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        if (idCategoria <= 0) {
            throw new IllegalArgumentException("Selecciona una categoría válida.");
        }
    }
}