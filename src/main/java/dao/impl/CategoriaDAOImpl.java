package dao.impl;

import config.DatabaseConnection;
import dao.CategoriaDAO;
import modelo.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl implements CategoriaDAO {
    @Override
    public void create(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO biblioteca.categorias(nombre) VALUES (?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    categoria.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Categoria> readAll() throws SQLException {
        String sql = "SELECT id, nombre FROM biblioteca.categorias ORDER BY id";
        List<Categoria> categorias = new ArrayList<>();

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapear(rs));
            }
        }

        return categorias;
    }

    @Override
    public void update(Categoria categoria) throws SQLException {
        String sql = "UPDATE biblioteca.categorias SET nombre = ? WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe una categoría con ID " + categoria.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM biblioteca.categorias WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe una categoría con ID " + id);
            }
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id"),
                rs.getString("nombre")
        );
    }
}
