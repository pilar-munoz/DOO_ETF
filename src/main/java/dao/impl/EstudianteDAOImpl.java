package dao.impl;

import config.DatabaseConnection;
import dao.EstudianteDAO;
import modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAOImpl implements EstudianteDAO {
    @Override
    public void create(Estudiante estudiante) throws SQLException {
        String sql = "INSERT INTO biblioteca.estudiantes(nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    estudiante.setId(claves.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Estudiante> readAll() throws SQLException {
        String sql = "SELECT id, nombre, rut, curso, correo FROM biblioteca.estudiantes ORDER BY id";
        List<Estudiante> estudiantes = new ArrayList<>();

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                estudiantes.add(mapear(rs));
            }
        }

        return estudiantes;
    }

    @Override
    public void update(Estudiante estudiante) throws SQLException {
        String sql = "UPDATE biblioteca.estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, estudiante.getNombre());
            ps.setString(2, estudiante.getRut());
            ps.setString(3, estudiante.getCurso());
            ps.setString(4, estudiante.getCorreo());
            ps.setInt(5, estudiante.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un estudiante con ID " + estudiante.getId());
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM biblioteca.estudiantes WHERE id = ?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No existe un estudiante con ID " + id);
            }
        }
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("curso"),
                rs.getString("correo")
        );
    }
}
