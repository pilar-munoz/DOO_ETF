package dao;

import modelo.Prestamo;

import java.sql.SQLException;
import java.util.List;

public interface PrestamoDAO {
    void create(int idLibro, int idEstudiante) throws SQLException;

    void devolver(int idPrestamo) throws SQLException;

    List<Prestamo> readActivos() throws SQLException;
}