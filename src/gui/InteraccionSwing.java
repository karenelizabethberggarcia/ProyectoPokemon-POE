package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import cartas.Carta;
import cartas.Pokemon;
import cartas.Tipo;
import juego.Interaccion;
import juego.Jugador;
import juego.Partida;

/**
 * Puente entre la logica del juego (Partida) y la ventana.
 *
 * Partida corre en su propio hilo y llama a estos metodos. Los que piden
 * algo al jugador (elegirOpcion, esperarContinuar, mostrarGanador) abren una
 * ventana en el hilo grafico y hacen esperar al hilo de la partida hasta que
 * el jugador responda. Asi la ventana nunca se congela.
 */
public class InteraccionSwing implements Interaccion {

    private static final String LINEA = "============================================================";
    private static final int ANCHO_BARRA = 10;

    private final VentanaPrincipal ventana;
    private final JTextArea areaRegistro;

    public InteraccionSwing(VentanaPrincipal ventana, JTextArea areaRegistro) {
        this.ventana = ventana;
        this.areaRegistro = areaRegistro;
    }

    // ======================================================
    //  Metodos de la interfaz Interaccion
    // ======================================================

    @Override
    public int elegirOpcion(String titulo, List<String> opciones) {
        return enHiloGrafico(() -> mostrarDialogoOpciones(titulo, opciones));
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        agregarAlRegistro(mensaje);
    }

    @Override
    public void esperarContinuar(String mensaje) {
        agregarAlRegistro("");
        agregarAlRegistro(mensaje);
        enHiloGrafico(() -> {
            JOptionPane.showMessageDialog(ventana, mensaje, "Continuar", JOptionPane.INFORMATION_MESSAGE);
            return null;
        });
    }

    @Override
    public void mostrarTablero(int turno, Jugador actual, Jugador rival) {
        StringBuilder t = new StringBuilder();
        t.append("\n").append(LINEA).append("\n");
        t.append("TURNO ").append(turno).append(" - ").append(actual.getNombre()).append("\n");
        t.append(LINEA).append("\n");

        textoCampo(t, "RIVAL (" + rival.getNombre() + ")", rival);
        t.append("Puntos de premio rival: ").append(rival.getPuntosPremio())
                .append("/").append(Partida.PUNTOS_PARA_GANAR).append("\n\n");

        textoCampo(t, "TU CAMPO (" + actual.getNombre() + ")", actual);
        t.append("Puntos de premio propios: ").append(actual.getPuntosPremio())
                .append("/").append(Partida.PUNTOS_PARA_GANAR).append("\n\n");

        Tipo zona = actual.getEnergiaZona();
        if (zona == null) {
            t.append("Zona de energia: vacia (ya usaste la de este turno)\n");
        } else {
            t.append("Zona de energia: ").append(zona.getSimbolo()).append(" (disponible)\n");
        }

        List<Carta> mano = actual.getMano();
        t.append("Tu mano (").append(mano.size()).append(" cartas):\n");
        for (int i = 0; i < mano.size(); i++) {
            t.append(i + 1).append(". ").append(mano.get(i).getNombre()).append("\n");
        }
        agregarAlRegistro(t.toString());
    }

    @Override
    public void mostrarGanador(Jugador ganador, String motivo) {
        agregarAlRegistro("\n" + LINEA + "\n FIN DEL DUELO\n"
                + ganador.getNombre() + " se convierte en el nuevo Maestro Pokemon!\n"
                + motivo + "\n" + LINEA);
        enHiloGrafico(() -> {
            JOptionPane.showMessageDialog(ventana,
                    ganador.getNombre() + " se convierte en el nuevo Maestro Pokemon!\n" + motivo,
                    "Fin del duelo", JOptionPane.INFORMATION_MESSAGE);
            ventana.mostrarInicio();
            return null;
        });
    }

    // ======================================================
    //  Ventanas auxiliares (siempre se crean en el hilo grafico)
    // ======================================================

    /** Dialogo con un boton por opcion. No se puede cerrar sin elegir una. */
    private int mostrarDialogoOpciones(String titulo, List<String> opciones) {
        JDialog dialogo = new JDialog(ventana, "Elige una opcion", true);
        dialogo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        int[] elegido = {-1};

        JPanel botones = new JPanel(new GridLayout(0, 1, 0, 6));
        botones.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        for (int i = 0; i < opciones.size(); i++) {
            final int posicion = i;
            JButton boton = new JButton(opciones.get(i));
            boton.addActionListener(e -> {
                elegido[0] = posicion;
                dialogo.dispose();
            });
            botones.add(boton);
        }

        JLabel etiqueta = new JLabel("<html>" + titulo + "</html>");
        etiqueta.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JScrollPane scroll = new JScrollPane(botones);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(520, Math.min(40 * opciones.size() + 30, 420)));

        dialogo.add(etiqueta, BorderLayout.NORTH);
        dialogo.add(scroll, BorderLayout.CENTER);
        dialogo.pack();
        dialogo.setLocationRelativeTo(ventana);
        dialogo.setVisible(true); // espera aqui hasta que se pulse un boton
        return elegido[0];
    }

    // ======================================================
    //  Utilidades
    // ======================================================

    /**
     * Ejecuta algo en el hilo grafico y espera su resultado. Es lo que hace
     * esperar al hilo de la partida mientras el jugador decide.
     */
    private <T> T enHiloGrafico(Supplier<T> tarea) {
        if (SwingUtilities.isEventDispatchThread()) {
            return tarea.get();
        }
        Object[] resultado = new Object[1];
        try {
            SwingUtilities.invokeAndWait(() -> resultado[0] = tarea.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La partida fue interrumpida.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Error en la ventana.", e.getCause());
        }
        @SuppressWarnings("unchecked")
        T valor = (T) resultado[0];
        return valor;
    }

    private void agregarAlRegistro(String texto) {
        SwingUtilities.invokeLater(() -> {
            areaRegistro.append(texto + "\n");
            areaRegistro.setCaretPosition(areaRegistro.getDocument().getLength());
        });
    }

    private void textoCampo(StringBuilder t, String titulo, Jugador jugador) {
        Pokemon activo = jugador.getCampo().getActivo();
        if (activo == null) {
            t.append(titulo).append("  (sin Pokemon activo)\n");
        } else {
            t.append(titulo).append("  PS: ").append(barraDeVida(activo)).append(" ")
                    .append(activo.getPsActuales()).append("/").append(activo.getPsMaximos()).append("\n");
            t.append("[Activo] ").append(activo.getNombre())
                    .append("   Energia adjunta: ").append(energiasComoTexto(activo)).append("\n");
        }

        List<Pokemon> banco = jugador.getCampo().getBanco();
        if (banco.isEmpty()) {
            t.append("[Banco] (vacio)\n");
        } else {
            List<String> textos = new ArrayList<>();
            for (Pokemon p : banco) {
                textos.add(p.getNombre() + " (" + p.getPsActuales() + " PS)");
            }
            t.append("[Banco] ").append(String.join("   ", textos)).append("\n");
        }
    }

    private String barraDeVida(Pokemon pokemon) {
        int llenos = (int) Math.ceil((double) pokemon.getPsActuales() * ANCHO_BARRA / pokemon.getPsMaximos());
        llenos = Math.max(0, Math.min(ANCHO_BARRA, llenos));
        return "#".repeat(llenos) + ".".repeat(ANCHO_BARRA - llenos);
    }

    private String energiasComoTexto(Pokemon pokemon) {
        List<Tipo> energias = new ArrayList<>(pokemon.getEnergias());
        if (energias.isEmpty()) {
            return "ninguna";
        }
        java.util.Collections.sort(energias);
        StringBuilder texto = new StringBuilder();
        for (Tipo tipo : energias) {
            texto.append(tipo.getSimbolo());
        }
        return texto.toString();
    }
}
