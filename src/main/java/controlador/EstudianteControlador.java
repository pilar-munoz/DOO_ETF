package controlador;

import dao.EstudianteDAO;
import modelo.Estudiante;

import java.sql.SQLException;
import java.util.List;

public class EstudianteControlador {
    private final EstudianteDAO estudianteDAO;

    public EstudianteControlador(EstudianteDAO estudianteDAO) {
        this.estudianteDAO = estudianteDAO;
    }

    public void crearEstudiante(String nombre, String rut, String curso, String correo) throws SQLException {
        validar(nombre, rut);
        estudianteDAO.create(new Estudiante(0, nombre.trim(), rut.trim(), curso.trim(), correo.trim()));
    }

    public List<Estudiante> listar() throws SQLException {
        return estudianteDAO.readAll();
    }

    public void actualizarEstudiante(int id, String nombre, String rut, String curso, String correo) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecciona un estudiante válido.");
        }

        validar(nombre, rut);
        estudianteDAO.update(new Estudiante(id, nombre.trim(), rut.trim(), curso.trim(), correo.trim()));
    }

    public void eliminarEstudiante(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Selecciona un estudiante válido.");
        }
        estudianteDAO.delete(id);
    }

    private void validar(String nombre, String rut) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (rut == null || rut.isBlank()) {
            throw new IllegalArgumentException("El RUT es obligatorio.");
        }
    }
}
