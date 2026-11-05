package juego;

import java.util.List;

/**
 * Todo lo que la logica del juego necesita pedirle o mostrarle al usuario.
 * Asi Partida y las cartas no dependen de la consola: la consola es solo
 * una forma de implementar esto.
 */
public interface Interaccion {

    /** Muestra un menu numerado y devuelve la posicion elegida (empezando en 0). */
    int elegirOpcion(String titulo, List<String> opciones);

    void mostrarMensaje(String mensaje);

    void mostrarTablero(int turno, Jugador actual, Jugador rival);

    void esperarContinuar(String mensaje);

    void mostrarGanador(Jugador ganador, String motivo);
}
