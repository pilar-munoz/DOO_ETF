package dao;

import modelo.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface UsuarioDAO {
    void create(Usuario usuario) throws SQLException;
    List<Usuario> readAll() throws SQLException;
    void update(Usuario usuario) throws SQLException;
    void delete(int id) throws SQLException;

    // devuelve el usuario si correo/RUT y contraseña coinciden, o nulo si no existe
    Usuario autenticar(String usuario, String contrasena) throws SQLException;
}
