package cartas;

import juego.Interaccion;
import juego.Jugador;

public class TurboVelocidad extends Objeto {

    private static final int REDUCCION = 1;

    public TurboVelocidad() {
        super("Turbo Velocidad", "Reduce en 1 el costo de retirada del Pokémon activo este turno.");
    }

    @Override
    public boolean puedeActivarse(Jugador jugador) {
        return jugador.getCampo().getActivo() != null;
    }

    @Override
    public void activar(Jugador jugador, Interaccion interaccion) {
        jugador.reducirCostoRetirada(REDUCCION);
        Pokemon activo = jugador.getCampo().getActivo();
        interaccion.mostrarMensaje("Este turno retirar a " + activo.getNombre() + " cuesta "
                + jugador.getCostoRetiradaActual() + " energía(s).");
    }
}
