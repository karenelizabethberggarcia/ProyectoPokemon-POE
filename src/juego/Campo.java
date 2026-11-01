package juego;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cartas.Pokemon;

/**
 * Campo de batalla de un jugador: un Pokémon activo y hasta 2 en el banco.
 */
public class Campo {

    public static final int MAX_BANCO = 2;

    private Pokemon activo;
    private final List<Pokemon> banco = new ArrayList<>();

    public Pokemon getActivo() {
        return activo;
    }

    public List<Pokemon> getBanco() {
        return Collections.unmodifiableList(banco);
    }

    public void colocarActivo(Pokemon pokemon) {
        this.activo = pokemon;
    }

    public boolean bancoLleno() {
        return banco.size() >= MAX_BANCO;
    }

    /** Devuelve false si el banco ya estaba lleno. */
    public boolean agregarABanco(Pokemon pokemon) {
        if (bancoLleno()) {
            return false;
        }
        banco.add(pokemon);
        return true;
    }

    /** Saca al activo del campo (por ejemplo cuando lo derrotan) y lo devuelve. */
    public Pokemon quitarActivo() {
        Pokemon anterior = activo;
        activo = null;
        return anterior;
    }

    /** El Pokémon del banco pasa a ser el activo. El activo actual debe haberse quitado antes. */
    public void promover(Pokemon delBanco) {
        if (!banco.remove(delBanco)) {
            throw new IllegalArgumentException("Ese Pokémon no está en el banco.");
        }
        activo = delBanco;
    }

    /** Retirada: el activo va al banco y el elegido del banco toma su lugar. */
    public void intercambiar(Pokemon delBanco) {
        int posicion = banco.indexOf(delBanco);
        if (posicion < 0) {
            throw new IllegalArgumentException("Ese Pokémon no está en el banco.");
        }
        banco.set(posicion, activo);
        activo = delBanco;
    }

    /** Todos los Pokémon en juego, empezando por el activo. */
    public List<Pokemon> getTodos() {
        List<Pokemon> todos = new ArrayList<>();
        if (activo != null) {
            todos.add(activo);
        }
        todos.addAll(banco);
        return todos;
    }

    public boolean tienePokemon() {
        return activo != null || !banco.isEmpty();
    }
}
