package juego;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cartas.Carta;
import cartas.Entrenador;
import cartas.Pokemon;
import cartas.Tipo;

/**
 * Un jugador: su mazo, su mano, su campo y lo que lleva de turno.
 */
public class Jugador {

    private final String nombre;
    private final Mazo mazo;
    private final Campo campo = new Campo();
    private final List<Carta> mano = new ArrayList<>();
    private final List<Carta> descarte = new ArrayList<>();

    private int puntosPremio = 0;
    private Tipo energiaZona;

    // Estado que se reinicia en cada turno
    private boolean soporteJugado;
    private boolean haRetirado;
    private int reduccionRetirada;

    public Jugador(String nombre, Mazo mazo) {
        this.nombre = nombre;
        this.mazo = mazo;
    }

    // ---------- Datos ----------

    public String getNombre() {
        return nombre;
    }

    public Mazo getMazo() {
        return mazo;
    }

    public Campo getCampo() {
        return campo;
    }

    public int getPuntosPremio() {
        return puntosPremio;
    }

    public void sumarPuntosPremio(int puntos) {
        puntosPremio += puntos;
    }

    // ---------- Mano y mazo ----------

    public List<Carta> getMano() {
        return Collections.unmodifiableList(mano);
    }

    public void agregarAMano(Carta carta) {
        mano.add(carta);
    }

    public boolean quitarDeMano(Carta carta) {
        return mano.remove(carta);
    }

    /** Roba una carta. Devuelve null si el mazo esta vacio. */
    public Carta robarCarta() {
        Carta carta = mazo.robar();
        if (carta != null) {
            mano.add(carta);
        }
        return carta;
    }

    /** Roba hasta n cartas y devuelve cuantas pudo robar realmente. */
    public int robarCartas(int n) {
        int robadas = 0;
        while (robadas < n && robarCarta() != null) {
            robadas++;
        }
        return robadas;
    }

    public void descartarMano() {
        descarte.addAll(mano);
        mano.clear();
    }

    /** Para el mulligan: la mano vuelve al mazo. */
    public void devolverManoAlMazo() {
        mazo.agregarTodas(mano);
        mano.clear();
    }

    public void descartar(Carta carta) {
        descarte.add(carta);
    }

    public List<Pokemon> getPokemonEnMano() {
        List<Pokemon> resultado = new ArrayList<>();
        for (Carta c : mano) {
            if (c instanceof Pokemon) {
                resultado.add((Pokemon) c);
            }
        }
        return resultado;
    }

    public List<Entrenador> getEntrenadoresEnMano() {
        List<Entrenador> resultado = new ArrayList<>();
        for (Carta c : mano) {
            if (c instanceof Entrenador) {
                resultado.add((Entrenador) c);
            }
        }
        return resultado;
    }

    // ---------- Zona de energia ----------

    public Tipo getEnergiaZona() {
        return energiaZona;
    }

    /** Si la zona esta vacia genera una energia de alguno de los tipos del mazo. */
    public void generarEnergiaZona(Random random) {
        if (energiaZona == null) {
            List<Tipo> tipos = mazo.getTiposEnergia();
            energiaZona = tipos.get(random.nextInt(tipos.size()));
        }
    }

    /** Toma la energia de la zona (queda vacia hasta el proximo turno). */
    public Tipo usarEnergiaZona() {
        Tipo energia = energiaZona;
        energiaZona = null;
        return energia;
    }

    // ---------- Estado del turno ----------

    public void iniciarTurno() {
        soporteJugado = false;
        haRetirado = false;
        reduccionRetirada = 0;
    }

    public void terminarTurno() {
        reduccionRetirada = 0;
    }

    public boolean haJugadoSoporte() {
        return soporteJugado;
    }

    public void registrarSoporte() {
        soporteJugado = true;
    }

    public boolean haRetirado() {
        return haRetirado;
    }

    public void registrarRetirada() {
        haRetirado = true;
    }

    public void reducirCostoRetirada(int cantidad) {
        reduccionRetirada += cantidad;
    }

    /** Costo de retirada del activo teniendo en cuenta lo jugado este turno (nunca baja de 0). */
    public int getCostoRetiradaActual() {
        Pokemon activo = campo.getActivo();
        if (activo == null) {
            return 0;
        }
        return Math.max(0, activo.getCostoRetirada() - reduccionRetirada);
    }
}
