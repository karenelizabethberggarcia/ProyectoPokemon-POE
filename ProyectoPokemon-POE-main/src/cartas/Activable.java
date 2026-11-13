package cartas;

import juego.Interaccion;
import juego.Jugador;

/**
 * Cartas con un efecto que se activa durante el turno (las de entrenador).
 */
public interface Activable {

    /** Indica si ahora mismo el efecto tendria algun objetivo valido. */
    boolean puedeActivarse(Jugador jugador);

    /** Aplica el efecto de la carta. Se llama despues de sacarla de la mano. */
    void activar(Jugador jugador, Interaccion interaccion);
}
