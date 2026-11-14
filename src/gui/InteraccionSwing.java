package gui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import juego.Interaccion;
import juego.Jugador;


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

    private final VentanaPrincipal ventana;
    private final JTextArea areaRegistro;

    /** Aqui deja el tablero la opcion que pulso el jugador. */
    private final BlockingQueue<Integer> respuesta = new ArrayBlockingQueue<>(1);

    public InteraccionSwing(VentanaPrincipal ventana, JTextArea areaRegistro) {
        this.ventana = ventana;
        this.areaRegistro = areaRegistro;
    }

    // ======================================================
    //  Metodos de la interfaz Interaccion
    // ======================================================

    @Override
    public int elegirOpcion(String titulo, List<String> opciones) {
        respuesta.clear();
        List<String> copia = new ArrayList<>(opciones);
        // Los botones se crean en el hilo grafico...
        SwingUtilities.invokeLater(() -> ventana.getPanelTablero()
                .mostrarOpciones(titulo, copia, indice -> respuesta.offer(indice)));
        // ...y el hilo de la partida espera aqui hasta que el jugador pulse uno.
        try {
            return respuesta.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La partida fue interrumpida.", e);
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        agregarAlRegistro(mensaje);
    }

    @Override
    public void esperarContinuar(String mensaje) {
        agregarAlRegistro("");
        agregarAlRegistro(mensaje);
        elegirOpcion(mensaje, List.of("Continuar"));
    }

    @Override
    public void mostrarTablero(int turno, Jugador actual, Jugador rival) {
        agregarAlRegistro("--- Turno " + turno + ": " + actual.getNombre() + " ---");
        // Se espera a que termine de dibujarse antes de seguir con la partida
        enHiloGrafico(() -> {
            ventana.getPanelTablero().actualizarVista(turno, actual, rival);
            return null;
        });
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
}
