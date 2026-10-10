package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;

import java.sql.SQLException;

public class UsuarioControlador {
    private final UsuarioDAO usuarioDAO;

    public UsuarioControlador(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    // devuelve el usuario + rol o null si las credenciales no coinciden
    public Usuario autenticar(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("Usuario y contraseña son obligatorios.");
        }
        System.out.println("Usuario autenticado: " + usuario);
        return usuarioDAO.autenticar(usuario.trim(), contrasena);
    }
}
