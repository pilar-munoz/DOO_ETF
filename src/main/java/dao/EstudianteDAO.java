package dao;

import modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;

public interface EstudianteDAO {
    void create(Estudiante estudiante) throws SQLException;
    List<Estudiante> readAll() throws SQLException;
    void update(Estudiante estudiante) throws SQLException;
    void delete(int id) throws SQLException;
}
