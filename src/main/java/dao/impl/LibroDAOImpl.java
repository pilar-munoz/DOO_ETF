package dao.impl;

import config.DatabaseConnection;
import dao.LibroDAO;
import modelo.Categoria;
import modelo.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAOImpl implements LibroDAO {
    @Override
    public void create(Libro libro) throws SQLException {
        String sql = "INSERT INTO biblioteca.libros(titulo, autor, isbn, editorial, stock, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    libro.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Libro> readAll() throws SQLException {
        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, l.editorial, l.stock, l.id_categoria, " +
                "c.nombre AS categoria " +
                "FROM biblioteca.libros l " +
                "LEFT JOIN biblioteca.categorias c ON c.id = l.id_categoria " +
                "ORDER BY l.id";
        List<Libro> libros = new ArrayList<>();

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapear(rs));
            }
        }

        return libros;
    }

    // synchronized para modificar el stock del libro
    @Override
    public synchronized void update(Libro libro) throws SQLException {
        String sql = "UPDATE biblioteca.libros SET titulo = ?, autor = ?, isbn = ?, editorial = ?, " +
                "stock = ?, id_categoria = ? WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getStock());
            ps.setInt(6, libro.getIdCategoria());
            ps.setInt(7, libro.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un libro con ID " + libro.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM biblioteca.libros WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un libro con ID " + id);
            }
        }
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getInt("stock"),
                new Categoria(rs.getInt("id_categoria"), rs.getString("categoria"))
        );
    }
}