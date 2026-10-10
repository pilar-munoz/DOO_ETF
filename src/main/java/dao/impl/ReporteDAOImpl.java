package dao.impl;

import config.DatabaseConnection;
import dao.ReporteDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAOImpl implements ReporteDAO {
    @Override
    public List<String[]> librosEnPrestamo() throws SQLException {
        String sql = "SELECT l.titulo, e.nombre, p.fecha_prestamo, p.fecha_devolucion, " +
                "CASE WHEN p.fecha_devolucion < CURDATE() THEN 'ATRASADO' ELSE 'En plazo' END AS estado " +
                "FROM biblioteca.prestamos p " +
                "JOIN biblioteca.libros l ON l.id = p.id_libro " +
                "JOIN biblioteca.estudiantes e ON e.id = p.id_estudiante " +
                "WHERE p.devuelto = FALSE " +
                "ORDER BY p.fecha_devolucion, p.id";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return leer(ps);
        }
    }

    @Override
    public List<String[]> librosMasPrestados() throws SQLException {
        String sql = "SELECT l.titulo, l.autor, COUNT(p.id) AS total " +
                "FROM biblioteca.libros l " +
                "JOIN biblioteca.prestamos p ON p.id_libro = l.id " +
                "GROUP BY l.id, l.titulo, l.autor " +
                "ORDER BY total DESC, l.titulo " +
                "LIMIT 10";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return leer(ps);
        }
    }

    @Override
    public List<String[]> historialEstudiante(int idEstudiante) throws SQLException {
        String sql = "SELECT l.titulo, p.fecha_prestamo, p.fecha_devolucion, " +
                "CASE WHEN p.devuelto THEN 'Devuelto' " +
                "WHEN p.fecha_devolucion < CURDATE() THEN 'ATRASADO' " +
                "ELSE 'En préstamo' END AS estado " +
                "FROM biblioteca.prestamos p " +
                "JOIN biblioteca.libros l ON l.id = p.id_libro " +
                "WHERE p.id_estudiante = ? " +
                "ORDER BY p.fecha_prestamo DESC, p.id DESC";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            return leer(ps);
        }
    }

    // ejecuta la consulta y deja cada fila como array textos
    private List<String[]> leer(PreparedStatement ps) throws SQLException {
        List<String[]> filas = new ArrayList<>();

        try (ResultSet rs = ps.executeQuery()) {
            int columnas = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                String[] fila = new String[columnas];
                for (int i = 0; i < columnas; i++) {
                    fila[i] = rs.getString(i + 1);
                }
                filas.add(fila);
            }
        }

        return filas;
    }
}