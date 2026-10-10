package controlador;

import dao.PrestamoDAO;
import modelo.Prestamo;

import java.sql.SQLException;
import java.util.List;

public class PrestamoControlador {
    private final PrestamoDAO prestamoDAO;

    public PrestamoControlador(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    public void prestar(int idLibro, int idEstudiante) throws SQLException {
        validarId(idLibro, "Selecciona un libro válido.");
        validarId(idEstudiante, "Selecciona un estudiante válido");

        prestamoDAO.create(idLibro, idEstudiante);
    }

    public void devolver(int idPrestamo) throws SQLException {
        validarId(idPrestamo, "Selecciona un préstamo válido.");

        prestamoDAO.devolver(idPrestamo);
    }

    public List<Prestamo> listarActivos() throws SQLException {
        return prestamoDAO.readActivos();
    }

    private void validarId(int id, String mensaje) {
        if (id <= 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}