package cartas;

import juego.Interaccion;
import juego.Jugador;

public class InvestigacionProfesor extends Soporte {

    private static final int CARTAS_A_ROBAR = 2;

    public InvestigacionProfesor() {
        super("Investigación del Profesor", "Descarta tu mano actual y roba 2 cartas del mazo.");
    }

    @Override
    public boolean puedeActivarse(Jugador jugador) {
        return true;
    }

    @Override
    public void activar(Jugador jugador, Interaccion interaccion) {
        int descartadas = jugador.getMano().size();
        jugador.descartarMano();
        int robadas = jugador.robarCartas(CARTAS_A_ROBAR);
        interaccion.mostrarMensaje(jugador.getNombre() + " descarta " + descartadas
                + " carta(s) de su mano y roba " + robadas + ".");
    }
}
