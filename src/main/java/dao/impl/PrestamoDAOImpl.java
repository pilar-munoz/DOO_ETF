package dao.impl;

import config.DatabaseConnection;
import dao.PrestamoDAO;
import modelo.Prestamo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAOImpl implements PrestamoDAO {
    // días de plazo para considerar atraso -se guarda en fecha_devolucion al momento de prestar
    private static final int PLAZO_DIAS = 7;

    // synchronized para que un solo hilo a la vez modifique el stock
    @Override
    public synchronized void create(int idLibro, int idEstudiante) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            // 1) descuenta stock solo si queda uno
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE biblioteca.libros SET stock = stock - 1 WHERE id = ? AND stock > 0")) {
                ps.setInt(1, idLibro);

                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No hay stock disponible (o el libro no existe).");
                }
            }

            // 2) registra préstamo (también historial)
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO biblioteca.prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion) " +
                            "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL ? DAY))")) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idLibro);
                ps.setInt(3, PLAZO_DIAS);
                ps.executeUpdate();
            }

            connection.commit();
        } catch (SQLException | RuntimeException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public synchronized void devolver(int idPrestamo) throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            // 1) marca devuelto solo si estudiante sigue activo
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE biblioteca.prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE")) {
                ps.setInt(1, idPrestamo);

                if (ps.executeUpdate() == 0) {
                    throw new SQLException("El préstamo no existe o ya fue devuelto.");
                }
            }

            // 2) repone el stock del libro
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE biblioteca.libros l JOIN biblioteca.prestamos p ON p.id_libro = l.id " +
                            "SET l.stock = l.stock + 1 WHERE p.id = ?")) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }

            connection.commit();
        } catch (SQLException | RuntimeException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // synchronized para compartir la misma conexión
    @Override
    public synchronized List<Prestamo> readActivos() throws SQLException {
        String sql = "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, " +
                "p.devuelto, l.titulo, e.nombre " +
                "FROM biblioteca.prestamos p " +
                "JOIN biblioteca.libros l ON l.id = p.id_libro " +
                "JOIN biblioteca.estudiantes e ON e.id = p.id_estudiante " +
                "WHERE p.devuelto = FALSE " +
                "ORDER BY p.fecha_prestamo DESC, p.id DESC";

        List<Prestamo> lista = new ArrayList<>();

        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // las fechas pueden venir nulas
                Date prestamo = rs.getDate("fecha_prestamo");
                Date devolucion = rs.getDate("fecha_devolucion");

                lista.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_estudiante"),
                        rs.getInt("id_libro"),
                        prestamo != null ? prestamo.toLocalDate() : null,
                        devolucion != null ? devolucion.toLocalDate() : null,
                        rs.getBoolean("devuelto"),
                        rs.getString("titulo"),
                        rs.getString("nombre")));
            }
        }

        return lista;
    }
}