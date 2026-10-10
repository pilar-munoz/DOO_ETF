package controlador;

import dao.ReporteDAO;

import java.sql.SQLException;
import java.util.List;

public class ReporteControlador {
    private final ReporteDAO reporteDAO;

    public ReporteControlador(ReporteDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

    public List<String[]> librosEnPrestamo() throws SQLException {
        return reporteDAO.librosEnPrestamo();
    }

    public List<String[]> librosMasPrestados() throws SQLException {
        return reporteDAO.librosMasPrestados();
    }

    public List<String[]> historialEstudiante(int idEstudiante) throws SQLException {
        if (idEstudiante <= 0) {
            throw new IllegalArgumentException("Selecciona un estudiante válido.");
        }
        return reporteDAO.historialEstudiante(idEstudiante);
    }
}