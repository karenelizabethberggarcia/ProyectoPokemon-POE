package juego;

import java.util.List;

/**
 * Todo lo que la lógica del juego necesita pedirle o mostrarle al usuario.
 * Así Partida y las cartas no dependen de la consola: la consola es solo
 * una forma de implementar esto.
 */
public interface Interaccion {

    /** Muestra un menú numerado y devuelve la posición elegida (empezando en 0). */
    int elegirOpcion(String titulo, List<String> opciones);

    void mostrarMensaje(String mensaje);

    void mostrarTablero(int turno, Jugador actual, Jugador rival);

    void esperarContinuar(String mensaje);

    void mostrarGanador(Jugador ganador, String motivo);
}
