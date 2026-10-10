package dao;

import java.sql.SQLException;
import java.util.List;

public interface ReporteDAO {
    // cada fila == array de textos, sirve para mostrar en una tabla
    List<String[]> librosEnPrestamo() throws SQLException;
    List<String[]> librosMasPrestados() throws SQLException;
    List<String[]> historialEstudiante(int idEstudiante) throws SQLException;
}