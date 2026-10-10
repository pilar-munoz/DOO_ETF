package vista;

//import controlador.*;

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {
    private final UsuarioControlador controlador;
    private final Consumer<Usuario> alIngresar;

    private final JTextField campoUsuario = new JTextField(15);
    private final JPasswordField campoClave = new JPasswordField(15);
    private final JLabel mensaje = new JLabel(" ", SwingConstants.CENTER);

    public LoginPanel(UsuarioControlador controlador, Consumer<Usuario> alIngresar) {
        this.controlador = controlador;
        this.alIngresar = alIngresar;

        construirInterfaz();
    }


    private void construirInterfaz() {
        setLayout(new GridBagLayout());

        JPanel formulario = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel titulo = new JLabel("Iniciar sesión", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JButton botonIngresar = new JButton("Ingresar");
        mensaje.setForeground(Color.RED);

        formulario.add(titulo);
        formulario.add(campoUsuario);
        formulario.add(campoClave);
        formulario.add(botonIngresar);
        formulario.add(mensaje);

        // etiquetas dentro de los campos
        campoUsuario.setBorder(BorderFactory.createTitledBorder("Usuario (correo o RUT)"));
        campoClave.setBorder(BorderFactory.createTitledBorder("Contraseña"));

        add(formulario);

        botonIngresar.addActionListener(e -> ingresar());
        campoClave.addActionListener(e -> ingresar());
    }


    private void ingresar() {
        String usuario = campoUsuario.getText().trim();
        String clave = new String(campoClave.getPassword());

        if (usuario.isEmpty() || clave.isEmpty()) {
            mensaje.setText("Complete usuario y contraseña");
            return;
        }

        try {
            Usuario usuarioAutenticado = controlador.autenticar(usuario, clave);

            if (usuarioAutenticado != null) {
                limpiar();
                alIngresar.accept(usuarioAutenticado);
            } else {
                mensaje.setText("Credenciales incorrectas");
                campoClave.setText("");
            }

        } catch (Exception ex) {
            mensaje.setText(ex.getMessage());
        }
    }



    // deja el formulario vacío / ingreso o cerrar sesión
    public void limpiar() {
        campoUsuario.setText("");
        campoClave.setText("");
        mensaje.setText(" ");
    }
}