package dao;

import modelo.Categoria;

import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO {
    void create(Categoria categoria) throws SQLException;
    List<Categoria> readAll() throws SQLException;
    void update(Categoria categoria) throws SQLException;
    void delete(int id) throws SQLException;
}
