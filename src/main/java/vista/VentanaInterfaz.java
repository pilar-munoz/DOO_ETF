package vista;

import controlador.*;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class VentanaInterfaz extends JFrame {
    private final UsuarioControlador controladorUsuario;
    private final LibroControlador controladorLibro;
    private final EstudianteControlador controladorEstudiante;
    private final CategoriaControlador controladorCategoria;
    private final PrestamoControlador controladorPrestamo;
    private final ReporteControlador controladorReporte;

    // panel raíz para alternar luego del login
    private final JPanel panelRaiz = new JPanel(new CardLayout());

    // panel principal aplicación
    private final JPanel panelContenido = new JPanel(new CardLayout());

    // botones de navegación (campo rol habilita o deshabilita algunos)
    private final JButton botonLibros = new JButton("Libros");
    private final JButton botonEstudiantes = new JButton("Estudiantes");
    private final JButton botonPrestamos = new JButton("Préstamos");
    private final JButton botonReportes = new JButton("Reportes");
    private final JButton botonSalir = new JButton("Cerrar sesión");

    // mensaje del panel de inicio (muestra el usuario que ingresó)
    private final JLabel mensajeInicio = new JLabel("Seleccione una opción", SwingConstants.CENTER);

    // paneles por vista
    private final LoginPanel loginPanel;
    private final LibroPanel libroPanel;
    private final EstudiantePanel estudiantePanel;
    private final PrestamoPanel prestamoPanel;
    private final ReportePanel reportePanel;


    public VentanaInterfaz(
            UsuarioControlador usuarioControlador,
            LibroControlador libroControlador,
            EstudianteControlador estudianteControlador,
            CategoriaControlador categoriaControlador,
            PrestamoControlador prestamoControlador,
            ReporteControlador reporteControlador) {
        //controladores
        this.controladorUsuario = usuarioControlador;
        this.controladorLibro = libroControlador;
        this.controladorEstudiante = estudianteControlador;
        this.controladorCategoria = categoriaControlador;
        this.controladorPrestamo = prestamoControlador;
        this.controladorReporte = reporteControlador;

        //paneles por vista
        this.loginPanel = new LoginPanel(controladorUsuario, usuario -> ingresar(usuario));
        this.libroPanel = new LibroPanel(controladorLibro, controladorCategoria);
        this.estudiantePanel = new EstudiantePanel(controladorEstudiante);
        this.prestamoPanel = new PrestamoPanel(controladorPrestamo, controladorLibro, controladorEstudiante);
        this.reportePanel = new ReportePanel(controladorReporte, controladorEstudiante);

        // config general
        setTitle("Gestión de Biblioteca");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        construirInterfaz();
    }


    private void construirInterfaz() {
        JPanel panelNavegacion = new JPanel(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Gestión Biblioteca", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        // agrega el título al panel de navegación
        panelNavegacion.add(titulo, BorderLayout.NORTH);

        // panel para botones según modelo
        JPanel panelBotones = new JPanel(new GridLayout(1, 5, 10, 10));

        // agrega botones al panel
        panelBotones.add(botonLibros);
        panelBotones.add(botonEstudiantes);
        panelBotones.add(botonPrestamos);
        panelBotones.add(botonReportes);
        panelBotones.add(botonSalir);

        // agrega el panel de botones al panel de navegación
        panelNavegacion.add(panelBotones, BorderLayout.SOUTH);

        // panel de la aplicación = navegación + contenido
        JPanel panelApp = new JPanel(new BorderLayout(10, 10));
        panelApp.add(panelNavegacion, BorderLayout.NORTH);

        // agrega al panel de contenido los diferentes paneles según flujo
        panelContenido.add(crearPanelInicio(), "INICIO");
        panelContenido.add(libroPanel, "LIBROS");
        panelContenido.add(estudiantePanel, "ESTUDIANTES");
        panelContenido.add(prestamoPanel, "PRESTAMOS");
        panelContenido.add(reportePanel, "REPORTES");

        panelApp.add(panelContenido, BorderLayout.CENTER);

        // agrega login y app al panel raíz
        panelRaiz.add(loginPanel, "LOGIN");
        panelRaiz.add(panelApp, "APP");
        add(panelRaiz);

        // eventos botón
        botonLibros.addActionListener(e -> {
            libroPanel.actualizar();
            mostrarPanel("LIBROS");
        });
        botonEstudiantes.addActionListener(e -> {
            estudiantePanel.actualizar();
            mostrarPanel("ESTUDIANTES");
        });

        // recarga combos para que aparezcan libros y estudiantes nuevos
        botonPrestamos.addActionListener(e -> {
            prestamoPanel.actualizar();
            mostrarPanel("PRESTAMOS");
        });
        botonReportes.addActionListener(e -> {
            reportePanel.actualizar();
            mostrarPanel("REPORTES");
        });
        botonSalir.addActionListener(e -> {
            loginPanel.limpiar();
            mostrarPanel("INICIO");
            mostrarRaiz("LOGIN");
        });

        mostrarPanel("INICIO");
        mostrarRaiz("LOGIN");
    }


    // login correcto: habilita las opciones según rol + entra a la aplicación
    // bibliotecario = total permisos; estudiante = solo préstamos, devoluciones y consultas
    private void ingresar(Usuario usuario) {
        boolean bibliotecario = usuario.getRol() == Usuario.rolesTipo.bibliotecario;

        botonLibros.setEnabled(bibliotecario);
        botonEstudiantes.setEnabled(bibliotecario);

        mensajeInicio.setText("Bienvenido/a, " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        mostrarPanel("INICIO");
        mostrarRaiz("APP");
    }


    // cambio panel raíz (login a app)
    private void mostrarRaiz(String nombrePanel) {
        CardLayout layout = (CardLayout) panelRaiz.getLayout();
        layout.show(panelRaiz, nombrePanel);
    }


    // cambio panel de contenido
    private void mostrarPanel(String nombrePanel) {
        CardLayout layout = (CardLayout) panelContenido.getLayout();
        layout.show(panelContenido, nombrePanel);
    }


    // panel de inicio
    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new BorderLayout());
        mensajeInicio.setFont(new Font("Arial", Font.PLAIN, 20));

        panel.add(mensajeInicio, BorderLayout.CENTER);

        return panel;
    }
}