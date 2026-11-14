package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import cartas.Ataque;
import cartas.Pokemon;
import cartas.Tipo;

/**
 * Dibuja un Pokemon en el campo como una "tarjeta": nombre, vida,
 * energias adjuntas y ataques (en verde los que ya puede usar).
 */
public class PanelPokemon extends JPanel {

    public static final int ANCHO = 215;
    public static final int ALTO = 205;

    public PanelPokemon(Pokemon pokemon, String etiqueta) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorDeTipo(pokemon.getTipo()), 3),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        setToolTipText(pokemon.getDescripcion());

        add(linea(etiqueta, 10, Font.BOLD, Color.GRAY));
        add(linea(pokemon.getNombre(), 17, Font.BOLD, null));
        add(linea(pokemon.getEtapa().getNombre() + " - " + pokemon.getTipo().getNombre()
                + " - Premio " + pokemon.getPuntosPremio(), 11, Font.PLAIN, null));
        add(Box.createVerticalStrut(5));
        add(crearBarraVida(pokemon));
        add(Box.createVerticalStrut(5));
        add(crearEnergias(pokemon));
        add(Box.createVerticalStrut(5));
        for (Ataque ataque : pokemon.getAtaques()) {
            add(crearAtaque(pokemon, ataque));
        }
        add(Box.createVerticalGlue());
        add(linea("Retirada: " + pokemon.getCostoRetirada(), 11, Font.PLAIN, Color.DARK_GRAY));
    }

    /** Tarjeta vacia para los espacios libres del campo. */
    public static JPanel crearHueco(String texto) {
        JPanel hueco = new JPanel();
        hueco.setLayout(new BoxLayout(hueco, BoxLayout.Y_AXIS));
        hueco.setPreferredSize(new Dimension(ANCHO, ALTO));
        hueco.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 4));
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setForeground(Color.GRAY);
        etiqueta.setAlignmentX(Component.CENTER_ALIGNMENT);
        hueco.add(Box.createVerticalGlue());
        hueco.add(etiqueta);
        hueco.add(Box.createVerticalGlue());
        return hueco;
    }

    // ---------- Colores por tipo (los usa tambien el tablero) ----------

    public static Color colorDeTipo(Tipo tipo) {
        switch (tipo) {
            case ELECTRICO: return new Color(234, 179, 8);
            case FUEGO:     return new Color(239, 68, 68);
            case AGUA:      return new Color(59, 130, 246);
            case PSIQUICO:  return new Color(168, 85, 247);
            default:        return new Color(156, 163, 175);
        }
    }

    // ---------- Partes de la tarjeta ----------

    private JLabel linea(String texto, int tamano, int estilo, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", estilo, tamano));
        if (color != null) {
            etiqueta.setForeground(color);
        }
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private JProgressBar crearBarraVida(Pokemon pokemon) {
        JProgressBar barra = new JProgressBar(0, pokemon.getPsMaximos());
        barra.setValue(pokemon.getPsActuales());
        barra.setStringPainted(true);
        barra.setString(pokemon.getPsActuales() + "/" + pokemon.getPsMaximos() + " PS");

        double proporcion = (double) pokemon.getPsActuales() / pokemon.getPsMaximos();
        if (proporcion > 0.5) {
            barra.setForeground(new Color(34, 197, 94));
        } else if (proporcion > 0.25) {
            barra.setForeground(new Color(245, 158, 11));
        } else {
            barra.setForeground(new Color(220, 38, 38));
        }
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        return barra;
    }

    private JPanel crearEnergias(Pokemon pokemon) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        List<Tipo> energias = new ArrayList<>(pokemon.getEnergias());
        if (energias.isEmpty()) {
            JLabel sin = new JLabel("Sin energia");
            sin.setFont(new Font("SansSerif", Font.ITALIC, 11));
            sin.setForeground(Color.GRAY);
            panel.add(sin);
            return panel;
        }
        Collections.sort(energias);
        for (Tipo tipo : energias) {
            JLabel ficha = new JLabel(tipo.getSimbolo(), SwingConstants.CENTER);
            ficha.setOpaque(true);
            ficha.setBackground(colorDeTipo(tipo));
            ficha.setForeground(Color.WHITE);
            ficha.setFont(new Font("SansSerif", Font.BOLD, 11));
            ficha.setPreferredSize(new Dimension(20, 20));
            panel.add(ficha);
        }
        return panel;
    }

    private JLabel crearAtaque(Pokemon pokemon, Ataque ataque) {
        String texto = ataque.getNombre() + "  " + ataque.getCantidadCosto()
                + ataque.getTipoCosto().getSimbolo() + "  >  " + ataque.getDanoBase();
        boolean usable = pokemon.puedeUsar(ataque);
        return linea(texto, 12, usable ? Font.BOLD : Font.PLAIN,
                usable ? new Color(21, 128, 61) : Color.GRAY);
    }
}
