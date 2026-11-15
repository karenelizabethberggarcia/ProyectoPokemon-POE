package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cartas.Pokemon;
import juego.Campo;
import juego.Jugador;
import juego.Partida;

/**
 * Campo de un jugador: encabezado con su nombre y puntos, el Pokemon activo
 * y los espacios del banco.
 */
public class PanelCampo extends JPanel {

    private final JLabel encabezado = new JLabel(" ");
    private final JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));

    public PanelCampo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        encabezado.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(encabezado, BorderLayout.NORTH);
        add(fila, BorderLayout.CENTER);
    }

    /** Redibuja el campo con el estado actual del jugador. */
    public void actualizar(String titulo, Jugador jugador) {
        encabezado.setText(titulo + " - " + jugador.getNombre()
                + "   |   Puntos de premio: " + jugador.getPuntosPremio()
                + "/" + Partida.PUNTOS_PARA_GANAR
                + "   |   Cartas en el mazo: " + jugador.getMazo().cantidad());

        fila.removeAll();
        Campo campo = jugador.getCampo();

        Pokemon activo = campo.getActivo();
        fila.add(activo == null
                ? PanelPokemon.crearHueco("Sin Pokemon activo")
                : new PanelPokemon(activo, "ACTIVO"));

        List<Pokemon> banco = campo.getBanco();
        for (int i = 0; i < Campo.MAX_BANCO; i++) {
            fila.add(i < banco.size()
                    ? new PanelPokemon(banco.get(i), "BANCO " + (i + 1))
                    : PanelPokemon.crearHueco("Banco vacio"));
        }
        revalidate();
        repaint();
    }

    /** Deja el campo en blanco (al empezar una partida nueva). */
    public void limpiar() {
        encabezado.setText(" ");
        fila.removeAll();
        revalidate();
        repaint();
    }
}
