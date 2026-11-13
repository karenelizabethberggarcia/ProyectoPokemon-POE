package cartas;

import java.util.ArrayList;
import java.util.List;

/**
 * Crea las cartas de Genetic Apex que pide el enunciado.
 * Cada llamada crea una carta nueva, porque dos copias de Pikachu
 * tienen que tener su propia vida y sus propias energias.
 */
public class CatalogoCartas {

    private static final String[] NOMBRES = {
        "Pikachu", "Charmander", "Squirtle", "Jigglypuff", "Magikarp", "Snorlax",
        "Mewtwo ex", "Zapdos ex", "Moltres ex", "Articuno ex",
        "Investigacion del Profesor", "Pocion", "Poke Ball", "Turbo Velocidad", "Misty"
    };

    /** Crea una carta nueva a partir de su nombre. */
    public static Carta crear(String nombre) {
        if (nombre.equals("Pikachu")) {
            return pikachu();
        } else if (nombre.equals("Charmander")) {
            return charmander();
        } else if (nombre.equals("Squirtle")) {
            return squirtle();
        } else if (nombre.equals("Jigglypuff")) {
            return jigglypuff();
        } else if (nombre.equals("Magikarp")) {
            return magikarp();
        } else if (nombre.equals("Snorlax")) {
            return snorlax();
        } else if (nombre.equals("Mewtwo ex")) {
            return mewtwoEx();
        } else if (nombre.equals("Zapdos ex")) {
            return zapdosEx();
        } else if (nombre.equals("Moltres ex")) {
            return moltresEx();
        } else if (nombre.equals("Articuno ex")) {
            return articunoEx();
        } else if (nombre.equals("Investigacion del Profesor")) {
            return new InvestigacionProfesor();
        } else if (nombre.equals("Pocion")) {
            return new Pocion();
        } else if (nombre.equals("Poke Ball")) {
            return new PokeBall();
        } else if (nombre.equals("Turbo Velocidad")) {
            return new TurboVelocidad();
        } else if (nombre.equals("Misty")) {
            return new Misty();
        }
        throw new IllegalArgumentException("No existe la carta: " + nombre);
    }

    public static List<String> nombres() {
        List<String> lista = new ArrayList<>();
        for (String nombre : NOMBRES) {
            lista.add(nombre);
        }
        return lista;
    }

    // ---------- Pokemon basicos ----------

    public static Pokemon pikachu() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Mordisco", Tipo.ELECTRICO, 1, 20));
        return new PokemonBasico("Pikachu", Tipo.ELECTRICO, 60, 1, ataques);
    }

    public static Pokemon charmander() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Ascuas", Tipo.FUEGO, 1, 30));
        return new PokemonBasico("Charmander", Tipo.FUEGO, 60, 1, ataques);
    }

    public static Pokemon squirtle() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Placaje", Tipo.AGUA, 1, 20));
        return new PokemonBasico("Squirtle", Tipo.AGUA, 60, 1, ataques);
    }

    public static Pokemon jigglypuff() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Golpe", Tipo.INCOLORO, 1, 20));
        return new PokemonBasico("Jigglypuff", Tipo.INCOLORO, 70, 1, ataques);
    }

    public static Pokemon magikarp() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Chapoteo", Tipo.AGUA, 1, 10));
        return new PokemonBasico("Magikarp", Tipo.AGUA, 30, 1, ataques);
    }

    public static Pokemon snorlax() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Golpe Cuerpo", Tipo.INCOLORO, 3, 100));
        return new PokemonBasico("Snorlax", Tipo.INCOLORO, 150, 3, ataques);
    }

    // ---------- Pokemon-ex ----------

    public static Pokemon mewtwoEx() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Ataque Psiquico", Tipo.PSIQUICO, 2, 50));
        ataques.add(new Ataque("Psicorrayo", Tipo.PSIQUICO, 3, 150));
        return new PokemonEx("Mewtwo ex", Tipo.PSIQUICO, 150, 2, ataques);
    }

    public static Pokemon zapdosEx() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Picotazo", Tipo.INCOLORO, 1, 30));
        ataques.add(new Ataque("Tormenta Electrica", Tipo.ELECTRICO, 3, 100));
        return new PokemonEx("Zapdos ex", Tipo.ELECTRICO, 130, 2, ataques);
    }

    public static Pokemon moltresEx() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Aleteo", Tipo.INCOLORO, 1, 30));
        ataques.add(new Ataque("Danza Infernal", Tipo.FUEGO, 3, 70));
        return new PokemonEx("Moltres ex", Tipo.FUEGO, 130, 2, ataques);
    }

    public static Pokemon articunoEx() {
        List<Ataque> ataques = new ArrayList<>();
        ataques.add(new Ataque("Viento Helado", Tipo.AGUA, 1, 30));
        ataques.add(new Ataque("Ventisca", Tipo.AGUA, 3, 100));
        return new PokemonEx("Articuno ex", Tipo.AGUA, 130, 2, ataques);
    }
}
