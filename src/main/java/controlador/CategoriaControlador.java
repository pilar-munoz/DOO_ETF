package controlador;

import dao.CategoriaDAO;
import modelo.Categoria;

import java.sql.SQLException;
import java.util.List;

public class CategoriaControlador {
    private final CategoriaDAO categoriaDAO;

    public CategoriaControlador(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public void crearCategoria(String nombre) throws SQLException {
        validar(nombre);
        categoriaDAO.create(new Categoria(0, nombre.trim()));
    }

    public List<Categoria> listar() throws SQLException {
        return categoriaDAO.readAll();
    }

    public void actualizarCategoria(int id, String nombre) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecciona una categoría válida.");
        }

        validar(nombre);
        categoriaDAO.update(new Categoria(id, nombre.trim()));
    }

    public void eliminarCategoria(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecciona una categoría válida.");
        }
        categoriaDAO.delete(id);
    }

    private void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
    }
}