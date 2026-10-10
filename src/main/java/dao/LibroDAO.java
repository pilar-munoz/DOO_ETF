package dao;

import modelo.Libro;

import java.sql.SQLException;
import java.util.List;

public interface LibroDAO {
    void create(Libro libro) throws SQLException;
    List<Libro> readAll() throws SQLException;
    void update(Libro libro) throws SQLException;
    void delete(int id) throws SQLException;
}
