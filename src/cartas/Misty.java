package cartas;

import java.util.ArrayList;
import java.util.List;

import juego.Interaccion;
import juego.Jugador;

public class Misty extends Soporte {

    private static final int MAX_ENERGIAS = 3;

    public Misty() {
        super("Misty", "Adjunta hasta 3 energía 💧 a uno de tus Pokémon de Agua.");
    }

    @Override
    public boolean puedeActivarse(Jugador jugador) {
        return !pokemonDeAgua(jugador).isEmpty();
    }

    @Override
    public void activar(Jugador jugador, Interaccion interaccion) {
        List<Pokemon> aguas = pokemonDeAgua(jugador);
        List<String> opciones = new ArrayList<>();
        for (Pokemon p : aguas) {
            opciones.add(p.getResumen());
        }
        int eleccion = interaccion.elegirOpcion("Misty: ¿a qué Pokémon de Agua le adjuntas energía?", opciones);
        Pokemon destino = aguas.get(eleccion);

        List<String> cantidades = new ArrayList<>();
        for (int i = 1; i <= MAX_ENERGIAS; i++) {
            cantidades.add(i + " energía(s) " + Tipo.AGUA.getSimbolo());
        }
        int cantidad = interaccion.elegirOpcion("¿Cuántas energías adjuntas? (hasta 3)", cantidades) + 1;

        for (int i = 0; i < cantidad; i++) {
            destino.adjuntarEnergia(Tipo.AGUA);
        }
        interaccion.mostrarMensaje("Misty adjunta " + cantidad + " energía(s) " + Tipo.AGUA.getSimbolo()
                + " a " + destino.getNombre() + ".");
    }

    private List<Pokemon> pokemonDeAgua(Jugador jugador) {
        List<Pokemon> resultado = new ArrayList<>();
        for (Pokemon p : jugador.getCampo().getTodos()) {
            if (p.getTipo() == Tipo.AGUA) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
