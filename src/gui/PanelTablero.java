package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.IntConsumer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import cartas.Carta;
import cartas.Entrenador;
import cartas.Pokemon;
import cartas.Tipo;
import juego.Jugador;


/**
 * Pantalla del duelo.
 *
 *   arriba     : turno y energia de la zona
 *   centro     : campo del rival (arriba) y campo del jugador en turno (abajo)
 *   derecha    : opciones para elegir (botones) y registro de lo que ocurre
 *   abajo      : mano del jugador en turno
 */
public class PanelTablero extends JPanel {

    private final JLabel estado = new JLabel(" ");
    private final PanelCampo campoRival = new PanelCampo();
    private final PanelCampo campoPropio = new PanelCampo();
    private final JPanel panelMano = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
    private final JLabel tituloMano = new JLabel("Mano");
    private final JLabel cartaSeleccionada = new JLabel(" ");

    private final JLabel tituloOpciones = new JLabel(" ");
    private final JPanel panelBotones = new JPanel(new GridLayout(0, 1, 0, 6));

    public PanelTablero(VentanaPrincipal ventana, JTextArea areaRegistro) {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        estado.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(estado, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(2, 1, 0, 6));
        campos.add(campoRival);
        campos.add(campoPropio);
        add(campos, BorderLayout.CENTER);

        add(crearColumnaDerecha(areaRegistro), BorderLayout.EAST);
        add(crearZonaMano(), BorderLayout.SOUTH);
    }

    // ---------- Construccion ----------

    private JPanel crearColumnaDerecha(JTextArea areaRegistro) {
        JPanel columna = new JPanel(new BorderLayout(0, 8));
        columna.setPreferredSize(new Dimension(300, 100));

        // Opciones
        JPanel opciones = new JPanel(new BorderLayout(0, 6));
        opciones.setBorder(BorderFactory.createTitledBorder("Que quieres hacer"));
        tituloOpciones.setFont(new Font("SansSerif", Font.BOLD, 13));
        opciones.add(tituloOpciones, BorderLayout.NORTH);
        JScrollPane scrollBotones = new JScrollPane(panelBotones);
        scrollBotones.setBorder(null);
        scrollBotones.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        opciones.add(scrollBotones, BorderLayout.CENTER);
        opciones.setPreferredSize(new Dimension(300, 360));

        // Registro
        areaRegistro.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        areaRegistro.setLineWrap(true);
        areaRegistro.setWrapStyleWord(true);
        JScrollPane scrollRegistro = new JScrollPane(areaRegistro);
        scrollRegistro.setBorder(BorderFactory.createTitledBorder("Registro"));

        columna.add(opciones, BorderLayout.NORTH);
        columna.add(scrollRegistro, BorderLayout.CENTER);
        return columna;
    }

    private JPanel crearZonaMano() {
        JPanel zona = new JPanel(new BorderLayout());
        zona.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tituloMano.setFont(new Font("SansSerif", Font.BOLD, 13));
        zona.add(tituloMano, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelMano);
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(100, 92));
        zona.add(scroll, BorderLayout.CENTER);
        
        cartaSeleccionada.setFont(new Font("SansSerif", Font.ITALIC, 12));
        zona.add(cartaSeleccionada, BorderLayout.SOUTH);
        return zona;
    }

    // ---------- Lo que la partida le pide a la pantalla ----------

    // Redibuja todo con el estado actual del duelo.
    public void actualizarVista(int turno, Jugador actual, Jugador rival) {
        if(turno == 0){
            estado.setText("PREPARACION - " + actual.getNombre() + " arma su campo");
            campoRival.ocultar("Rival - " + rival.getNombre() + " (si campo no se muestra durante la preparacion)");
        }else{
            Tipo zona = actual.getEnergiaZona();
            String textoZona = (zona == null)
                    ? "Energia de zona: ya usada"
                    : "Energia de zona: " + zona.getSimbolo() + " (disponible)";
            estado.setText("TURNO " + turno + " - Juega " + actual.getNombre() + "     " + textoZona);
            campoRival.actualizar("RIVAL", rival);
        }
        campoPropio.actualizar("TU CAMPO", actual);

        tituloMano.setText("Mano de " + actual.getNombre() + " (" + actual.getMano().size() + " cartas)");
        cartaSeleccionada.setText(" ");
        panelMano.removeAll();
        ButtonGroup grupo = new ButtonGroup();
        for (Carta carta : actual.getMano()) {
            JToggleButton boton = crearCartaMano(carta);
            grupo.add(boton);
            panelMano.add(boton);
        }
        panelMano.revalidate();
        panelMano.repaint();
    }

    /**
     * Muestra un titulo y un boton por opcion. Cuando el jugador pulsa uno,
     * se quitan los botones y se avisa con la posicion elegida.
     */
    public void mostrarOpciones(String titulo, List<String> opciones, IntConsumer alElegir) {
        tituloOpciones.setText("<html>" + titulo + "</html>");
        panelBotones.removeAll();
        for (int i = 0; i < opciones.size(); i++) {
            final int posicion = i;
            JButton boton = new JButton("<html><div style='width:230px;text-align:center'>"
                    + opciones.get(i) + "</div></html>");
            boton.addActionListener(e -> {
                quitarOpciones();
                alElegir.accept(posicion);
            });
            panelBotones.add(boton);
        }
        panelBotones.revalidate();
        panelBotones.repaint();
    }

    /** Vacia el area de opciones (mientras la partida piensa o no espera nada). */
    public void quitarOpciones() {
        tituloOpciones.setText(" ");
        panelBotones.removeAll();
        panelBotones.revalidate();
        panelBotones.repaint();
    }

    /** Deja el tablero en blanco para una partida nueva. */
    public void reiniciar() {
        estado.setText(" ");
        campoRival.limpiar();
        campoPropio.limpiar();
        panelMano.removeAll();
        tituloMano.setText("Mano");
        cartaSeleccionada.setText(" ");
        quitarOpciones();
    }

    // ---------- Cartas de la mano ----------

    private javax.swing.border.Border bordeCarta(Color color, int grosor){
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color, grosor),
                                                BorderFactory.createEmptyBorder(2, 6, 2, 6));
    }

    private JToggleButton crearCartaMano(Carta carta) {
        String subtitulo;
        Color borde;
        if (carta instanceof Pokemon) {
            Pokemon p = (Pokemon) carta;
            subtitulo = p.getEtapa().getNombre() + " - " + p.getPsMaximos() + " PS";
            borde = PanelPokemon.colorDeTipo(p.getTipo());
        } else if (carta instanceof Entrenador) {
            subtitulo = ((Entrenador) carta).esSoporte() ? "Soporte" : "Objeto";
            borde = new Color(100, 116, 139);
        } else {
            subtitulo = "";
            borde = Color.GRAY;
        }
        JToggleButton boton = new JToggleButton("<html><b>" + carta.getNombre() + "</b><br>"
                + subtitulo + "</html>");
        boton.setPreferredSize(new Dimension(175, 58));
        boton.setHorizontalAlignment(JToggleButton.LEFT);
        boton.setFocusPainted(false);
        boton.setToolTipText(carta.getDescripcion());
        boton.setBorder(bordeCarta(borde, 3));
        boton.addItemListener(e -> {
            boolean elegida = boton.isSelected();
            boton.setBorder(bordeCarta(borde, elegida ? 6 : 3));
            cartaSeleccionada.setText(elegida
                    ? "Carta seleccionada: " + carta.getNombre() + " - " + carta.getDescripcion()
                    : " ");
        });
        boton.setAlignmentY(Component.TOP_ALIGNMENT);
        return boton;
    }
}
