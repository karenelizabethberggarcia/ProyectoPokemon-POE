package juego;

import java.util.ArrayList;
import java.util.List;

import cartas.Carta;
import cartas.CatalogoCartas;

/**
 * Mazos listos para jugar. La logica del juego no depende de estos mazos,
 * funciona con cualquier mazo que pase la validacion de Mazo.
 */
public class MazosPredefinidos {

    private static final String[] NOMBRES = {
        "Mazo Mewtwo",
        "Mazo Pikachu",
        "Mazo Charizard",
        "Mazo Articuno"
    };

    private static final String[] DESCRIPCIONES = {
        "Mewtwo ex, Zapdos ex y Pikachu->Raichu, con energia psiquica, electrica y de fuego",
        "Pikachu->Raichu y Zapdos ex, con energia electrica y de agua",
        "Moltres ex y Charmander->Charmeleon->Charizard, con energia de fuego y de agua",
        "Articuno ex, Squirtle->Wartortle->Blastoise y Magikarp->Gyarados, solo energia de agua"
    };

    public static int cantidad() {
        return NOMBRES.length;
    }

    public static String getNombre(int indice) {
        return NOMBRES[indice];
    }

    public static String getDescripcion(int indice) {
        return DESCRIPCIONES[indice];
    }

    /** Crea un mazo nuevo (con cartas nuevas) cada vez que se llama. */
    public static Mazo crear(int indice) {
        List<Carta> cartas = new ArrayList<>();

        if (indice == 0) {
            agregar(cartas, "Mewtwo ex", 2);
            agregar(cartas, "Zapdos ex", 2);
            agregar(cartas, "Pikachu", 2);
            agregar(cartas, "Raichu", 2);
            agregar(cartas, "Charmander", 2);
            agregar(cartas, "Jigglypuff", 1);
            agregar(cartas, "Wigglytuff", 1);
            agregarEntrenadoresBasicos(cartas);
        } else if (indice == 1) {
            agregar(cartas, "Pikachu", 2);
            agregar(cartas, "Raichu", 2);
            agregar(cartas, "Zapdos ex", 2);
            agregar(cartas, "Squirtle", 2);
            agregar(cartas, "Magikarp", 2);
            agregarEntrenadoresBasicos(cartas);
            agregar(cartas, "Misty", 2);
        } else if (indice == 2) {
            agregar(cartas, "Moltres ex", 2);
            agregar(cartas, "Charmander", 2);
            agregar(cartas, "Charmeleon", 2);
            agregar(cartas, "Charizard", 2);
            agregar(cartas, "Squirtle", 2);
            agregar(cartas, "Jigglypuff", 2);
            agregarEntrenadoresBasicos(cartas);
        } else if (indice == 3) {
            agregar(cartas, "Articuno ex", 2);
            agregar(cartas, "Squirtle", 2);
            agregar(cartas, "Wartortle", 2);
            agregar(cartas, "Blastoise", 1);
            agregar(cartas, "Magikarp", 2);
            agregar(cartas, "Gyarados", 1);
            agregar(cartas, "Jigglypuff", 2);
            agregarEntrenadoresBasicos(cartas);
        } else {
            throw new IllegalArgumentException("No existe el mazo " + indice);
        }

        return new Mazo(NOMBRES[indice], cartas);
    }

    /** Las 4 cartas de entrenador que usan todos los mazos (2 copias de cada una). */
    private static void agregarEntrenadoresBasicos(List<Carta> cartas) {
        agregar(cartas, "Investigacion del Profesor", 2);
        agregar(cartas, "Pocion", 2);
        agregar(cartas, "Poke Ball", 2);
        agregar(cartas, "Turbo Velocidad", 2);
    }

    private static void agregar(List<Carta> cartas, String nombre, int copias) {
        for (int i = 0; i < copias; i++) {
            cartas.add(CatalogoCartas.crear(nombre));
        }
    }
}
