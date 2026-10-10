import controlador.*;
import dao.*;
import dao.impl.*;
import vista.VentanaInterfaz;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // instancias DAO y controladores
        UsuarioDAO daoUsuario = new UsuarioDAOImpl();
        UsuarioControlador usuarioControlador = new UsuarioControlador(daoUsuario);

        LibroDAO daoLibro = new LibroDAOImpl();
        LibroControlador libroControlador = new LibroControlador(daoLibro);

        EstudianteDAO daoEstudiante = new EstudianteDAOImpl();
        EstudianteControlador estudianteControlador = new EstudianteControlador(daoEstudiante);

        CategoriaDAO daoCategoria = new CategoriaDAOImpl();
        CategoriaControlador categoriaControlador = new CategoriaControlador(daoCategoria);

        PrestamoDAO daoPrestamo = new PrestamoDAOImpl();
        PrestamoControlador prestamoControlador = new PrestamoControlador(daoPrestamo);

        ReporteDAO daoReporte = new ReporteDAOImpl();
        ReporteControlador reporteControlador = new ReporteControlador(daoReporte);

        // inicia interfaz- entrega controladores
        SwingUtilities.invokeLater(() ->
                new VentanaInterfaz(usuarioControlador, libroControlador, estudianteControlador,
                        categoriaControlador, prestamoControlador, reporteControlador).setVisible(true));
    }
}