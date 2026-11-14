package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import juego.MazosPredefinidos;

/**
 * Pantalla inicial: cada entrenador escribe su nombre y elige su mazo.
 */
public class PanelInicio extends JPanel {

    private final VentanaPrincipal ventana;

    private final JTextField campoNombre1 = new JTextField("Entrenador 1");
    private final JTextField campoNombre2 = new JTextField("Entrenador 2");
    private final JComboBox<String> comboMazo1 = new JComboBox<>();
    private final JComboBox<String> comboMazo2 = new JComboBox<>();
    private final JLabel descripcionMazo1 = new JLabel(" ");
    private final JLabel descripcionMazo2 = new JLabel(" ");

    public PanelInicio(VentanaPrincipal ventana) {
        this.ventana = ventana;

        setLayout(new BorderLayout(0, 25));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        add(crearTitulo(), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
        add(crearBotonJugar(), BorderLayout.SOUTH);

        configurarMazo(comboMazo1, descripcionMazo1);
        configurarMazo(comboMazo2, descripcionMazo2);
        // para que no empiecen los dos con el mismo mazo
        if (MazosPredefinidos.cantidad() > 1) {
            comboMazo2.setSelectedIndex(1);
        }
    }

    // ---------- Construccion de la pantalla ----------

    private JPanel crearTitulo() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 5));

        JLabel titulo = new JLabel("POKEMON TCG POCKET", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 34));

        JLabel subtitulo = new JLabel(
                "Gana el primero que consiga " + juego.Partida.PUNTOS_PARA_GANAR + " puntos de premio.",
                SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));

        panel.add(titulo);
        panel.add(subtitulo);
        return panel;
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 0));
        panel.add(crearPanelJugador("Entrenador 1", campoNombre1, comboMazo1, descripcionMazo1));
        panel.add(crearPanelJugador("Entrenador 2", campoNombre2, comboMazo2, descripcionMazo2));

        // Al ponerlo arriba (NORTH) los campos no se estiran para llenar toda la ventana
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(panel, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel crearPanelJugador(String titulo, JTextField campoNombre,
            JComboBox<String> comboMazo, JLabel descripcion) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(titulo),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JPanel campos = new JPanel(new GridLayout(0, 1, 0, 8));
        campos.add(new JLabel("Nombre:"));
        campos.add(campoNombre);
        campos.add(new JLabel("Mazo:"));
        campos.add(comboMazo);
        panel.add(campos, BorderLayout.NORTH);

        // Espacio reservado para que la descripcion (2 lineas) no se corte
        descripcion.setVerticalAlignment(SwingConstants.TOP);
        descripcion.setPreferredSize(new Dimension(100, 45));
        panel.add(descripcion, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBotonJugar() {
        JButton boton = new JButton("Comenzar duelo");
        boton.setFont(new Font("SansSerif", Font.BOLD, 20));
        boton.addActionListener(e -> comenzar());

        JPanel panel = new JPanel();
        panel.add(boton);
        return panel;
    }

    // ---------- Comportamiento ----------

    /** Llena el combo con los mazos y muestra la descripcion del mazo elegido. */
    private void configurarMazo(JComboBox<String> combo, JLabel descripcion) {
        for (int i = 0; i < MazosPredefinidos.cantidad(); i++) {
            combo.addItem(MazosPredefinidos.getNombre(i));
        }
        combo.addActionListener(e -> mostrarDescripcion(combo, descripcion));
        mostrarDescripcion(combo, descripcion);
    }

    private void mostrarDescripcion(JComboBox<String> combo, JLabel descripcion) {
        int indice = combo.getSelectedIndex();
        if (indice >= 0) {
            // <html> permite que el texto largo se divida en varias lineas
            descripcion.setText("<html>" + MazosPredefinidos.getDescripcion(indice) + "</html>");
        }
    }

    private void comenzar() {
        String nombre1 = textoOPorDefecto(campoNombre1, "Entrenador 1");
        String nombre2 = textoOPorDefecto(campoNombre2, "Entrenador 2");
        ventana.iniciarPartida(nombre1, nombre2,
                comboMazo1.getSelectedIndex(), comboMazo2.getSelectedIndex());
    }

    private String textoOPorDefecto(JTextField campo, String porDefecto) {
        String texto = campo.getText().trim();
        return texto.isEmpty() ? porDefecto : texto;
    }
}
