package cartas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import juego.Interaccion;
import juego.Jugador;

public class PokeBall extends Objeto {

    public PokeBall() {
        super("Poke Ball", "Busca en tu mazo 1 Pokemon basico y ponlo en tu mano.");
    }

    @Override
    public boolean puedeActivarse(Jugador jugador) {
        return !jugador.getMazo().getPokemonesBasicos().isEmpty();
    }

    @Override
    public void activar(Jugador jugador, Interaccion interaccion) {
        // Si hay copias repetidas en el mazo solo mostramos una de cada nombre
        List<Pokemon> candidatos = new ArrayList<>();
        Set<String> nombresVistos = new HashSet<>();
        for (Pokemon p : jugador.getMazo().getPokemonesBasicos()) {
            if (nombresVistos.add(p.getNombre())) {
                candidatos.add(p);
            }
        }

        List<String> opciones = new ArrayList<>();
        for (Pokemon p : candidatos) {
            opciones.add(p.getResumen());
        }

        int eleccion = interaccion.elegirOpcion("Poke Ball: ?que Pokemon buscas en tu mazo?", opciones);
        Pokemon elegido = candidatos.get(eleccion);

        jugador.getMazo().extraer(elegido);
        jugador.agregarAMano(elegido);
        jugador.getMazo().barajar();
        interaccion.mostrarMensaje(jugador.getNombre() + " pone a " + elegido.getNombre()
                + " en su mano y baraja el mazo.");
    }
}
