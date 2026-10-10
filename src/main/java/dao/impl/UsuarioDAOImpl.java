package dao.impl;

import config.DatabaseConnection;
import dao.UsuarioDAO;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAOImpl implements UsuarioDAO {
    @Override
    public void create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO biblioteca.usuarios(nombre, rut, correo, contrasena, rol) VALUES (?, ?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol().name());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    usuario.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Usuario> readAll() throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contrasena, rol FROM biblioteca.usuarios ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }

        return usuarios;
    }

    @Override
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE biblioteca.usuarios SET nombre = ?, rut = ?, correo = ?, contrasena = ?, rol = ? " +
                "WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRut());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getRol().name());
            ps.setInt(6, usuario.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un usuario con ID " + usuario.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM biblioteca.usuarios WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un usuario con ID " + id);
            }
        }
    }

    @Override
    public Usuario autenticar(String usuario, String contrasena) throws SQLException {
        String sql = "SELECT id, nombre, rut, correo, contrasena, rol FROM biblioteca.usuarios " +
                "WHERE (correo = ? OR rut = ?) AND contrasena = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, usuario);
            ps.setString(3, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        // no existe o la contraseña no coincide
        return null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contrasena"),
                Usuario.rolesTipo.valueOf(rs.getString("rol").toLowerCase())
        );
    }
}
