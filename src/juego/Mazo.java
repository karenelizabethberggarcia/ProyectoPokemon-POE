package juego;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cartas.Carta;
import cartas.Pokemon;
import cartas.Tipo;

/**
 * Mazo de un jugador. Al crearlo se valida que cumpla las reglas del enunciado
 * (20 cartas, maximo 2 copias de cada una y al menos 6 Pokemon basicos).
 * Despues, durante la partida, va perdiendo cartas al robar.
 */
public class Mazo {

    public static final int TAMANO = 20;
    public static final int MAX_COPIAS = 2;
    public static final int MIN_POKEMON = 6;

    private final String nombre;
    private final List<Carta> cartas;
    private final List<Tipo> tiposEnergia;

    public Mazo(String nombre, List<Carta> cartas) {
        List<String> errores = validar(cartas);
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Mazo invalido (" + nombre + "): " + String.join(" ", errores));
        }
        this.nombre = nombre;
        this.cartas = new ArrayList<>(cartas);
        this.tiposEnergia = calcularTiposEnergia(cartas);
    }

    /** Devuelve la lista de problemas del mazo; si esta vacia el mazo es valido. */
    public static List<String> validar(List<Carta> cartas) {
        List<String> errores = new ArrayList<>();

        if (cartas.size() != TAMANO) {
            errores.add("Debe tener exactamente " + TAMANO + " cartas y tiene " + cartas.size() + ".");
        }

        // Cuantas copias hay de cada carta (se cuenta por nombre)
        Map<String, Integer> copias = new HashMap<>();
        int pokemon = 0;
        for (Carta c : cartas) {
            String nombre = c.getNombre();
            if (copias.containsKey(nombre)) {
                copias.put(nombre, copias.get(nombre) + 1);
            } else {
                copias.put(nombre, 1);
            }
            if (c instanceof Pokemon && ((Pokemon) c).esBasico()) {
                pokemon++;
            }
        }
        for (String nombre : copias.keySet()) {
            if (copias.get(nombre) > MAX_COPIAS) {
                errores.add("Hay mas de " + MAX_COPIAS + " copias de " + nombre + ".");
            }
        }
        if (pokemon < MIN_POKEMON) {
            errores.add("Debe tener al menos " + MIN_POKEMON + " Pokemon basicos y tiene " + pokemon + ".");
        }
        return errores;
    }

    /**
     * La zona de energia genera los tipos que aparecen en los Pokemon del mazo.
     * Los incoloros no cuentan; si el mazo solo tuviera incoloros se usan todos los tipos.
     */
    private static List<Tipo> calcularTiposEnergia(List<Carta> cartas) {
        List<Tipo> tipos = new ArrayList<>();
        for (Carta c : cartas) {
            if (c instanceof Pokemon) {
                Tipo tipo = ((Pokemon) c).getTipo();
                if (tipo != Tipo.INCOLORO && !tipos.contains(tipo)) {
                    tipos.add(tipo);
                }
            }
        }
        if (tipos.isEmpty()) {
            tipos.add(Tipo.ELECTRICO);
            tipos.add(Tipo.FUEGO);
            tipos.add(Tipo.AGUA);
            tipos.add(Tipo.PSIQUICO);
        }
        return tipos;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Tipo> getTiposEnergia() {
        return Collections.unmodifiableList(tiposEnergia);
    }

    public void barajar() {
        Collections.shuffle(cartas);
    }

    /** Saca la carta de arriba. Devuelve null si el mazo esta vacio. */
    public Carta robar() {
        if (cartas.isEmpty()) {
            return null;
        }
        return cartas.remove(0);
    }

    public boolean estaVacio() {
        return cartas.isEmpty();
    }

    public int cantidad() {
        return cartas.size();
    }

    public void agregarTodas(List<Carta> nuevas) {
        cartas.addAll(nuevas);
    }

    /** Pokemon que quedan en el mazo (para la Poke Ball). */
    public List<Pokemon> getPokemonesBasicos() {
        List<Pokemon> resultado = new ArrayList<>();
        for (Carta c : cartas) {
            if (c instanceof Pokemon && ((Pokemon) c).esBasico()) {
                resultado.add((Pokemon) c);
            }
        }
        return resultado;
    }

    /** Saca una carta concreta del mazo. */
    public boolean extraer(Carta carta) {
        return cartas.remove(carta);
    }
}
