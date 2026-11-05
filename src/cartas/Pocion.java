package cartas;

import juego.Interaccion;
import juego.Jugador;

public class Pocion extends Objeto {

    private static final int CURACION = 20;

    public Pocion() {
        super("Pocion", "Cura 20 PS al Pokemon activo propio.");
    }

    @Override
    public boolean puedeActivarse(Jugador jugador) {
        Pokemon activo = jugador.getCampo().getActivo();
        return activo != null && activo.estaHerido();
    }

    @Override
    public void activar(Jugador jugador, Interaccion interaccion) {
        Pokemon activo = jugador.getCampo().getActivo();
        int antes = activo.getPsActuales();
        activo.curar(CURACION);
        int curados = activo.getPsActuales() - antes;
        interaccion.mostrarMensaje(activo.getNombre() + " recupera " + curados + " PS ("
                + activo.getPsActuales() + "/" + activo.getPsMaximos() + ").");
    }
}
