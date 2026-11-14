package gui;

import juego.Jugador;
import juego.MazosPredefinidos;
import juego.Partida;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class VentanaPrincipal extends JFrame {

    // Contantes para evitar errores
    public static final String PANEL_INICIO = "INICIO";
    public static final String PANEL_TRANSICION = "TRANSICION";
    public static final String PANEL_TABLERO = "TABLERO";
    public static final String PANEL_FIN = "FIN";

    // Contenedores y Layouts
    private CardLayout cardLayout;
    private JPanel contenedor;

    // Estado y componentes de la ventana
    private JTextArea areaRegistro;
    private Partida partidaActual;
    private InteraccionSwing interaccionSwing;

    // Paneles de cada pantalla
    private PanelInicio panelInicio;
    private PanelTransicion panelTransicion;
    private PanelTablero panelTablero;
    private PanelFin panelFin;


    public VentanaPrincipal() {
        // ventana
        setTitle("Pokemon TCG Pocket");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // incializar componentes
        areaRegistro = new JTextArea();
        areaRegistro.setEditable(false);
        interaccionSwing = new InteraccionSwing(this, areaRegistro);
    
        // layout y contenedor
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
    
        //instanciamos y registramos cada pantalla
        panelInicio = new PanelInicio(this);
        panelTransicion = new PanelTransicion(this);
        panelTablero = new PanelTablero(this, areaRegistro);
        panelFin = new PanelFin(this);

        contenedor.add(panelInicio, PANEL_INICIO);
        contenedor.add(panelTransicion, PANEL_TRANSICION);
        contenedor.add(panelTablero, PANEL_TABLERO);
        contenedor.add(panelFin, PANEL_FIN);


        add(contenedor);
        mostrarInicio(); 
    }
    
    public void mostrarInicio() {
        cardLayout.show(contenedor, PANEL_INICIO);
    }

    public void iniciarPartida(String nombre1, String nombre2, int indiceMazo1, int indiceMazo2) {
        areaRegistro.setText(""); // Limpiar el área de registro al iniciar una nueva partida

        Jugador jugador1 = new Jugador(nombre1, MazosPredefinidos.crear(indiceMazo1));
        Jugador jugador2 = new Jugador(nombre2, MazosPredefinidos.crear(indiceMazo2));
        this.partidaActual = new Partida(jugador1, jugador2, interaccionSwing);

        mostrarTablero();

        // La partida corre en su propio hilo: asi, cuando espera una respuesta
        // del jugador, la ventana sigue respondiendo y no se congela.
        Thread hilo = new Thread(() -> {
            try {
                partidaActual.jugar();
            } catch (RuntimeException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "La partida terminó por un error: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    mostrarInicio();
                });
            }
        }, "hilo-partida");
        hilo.setDaemon(true); // no impide cerrar el programa
        hilo.start();
    }

    public void mostrarTransicion() {
        //panelTransicion.actualizarTexto(partidaActual.getJugadorActual().getNombre());
        cardLayout.show(contenedor, PANEL_TRANSICION);
    }

    public void mostrarTablero() {
        // panelTablero.actualizarVista(partidaActual);
        cardLayout.show(contenedor, PANEL_TABLERO);
    }

    public void mostrarFin() {
        // panelFin.mostrarResultados(partidaActual);
        cardLayout.show(contenedor, PANEL_FIN);
    }

    // --- Getters para que los paneles accedan al estado sin romper la arquitectura ---

    public Partida getPartidaActual() {
        return partidaActual;
    }

    public JTextArea getAreaRegistro() {
        return areaRegistro;
    }

    public InteraccionSwing getInteraccionSwing() {
        return interaccionSwing;
    }

    // --- Punto de entrada seguro (EDT) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}   

