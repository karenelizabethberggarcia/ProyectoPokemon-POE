package cartas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parte comun de todos los Pokemon: vida, energias adjuntas, ataques y dano.
 * Lo unico que cambia entre un Pokemon normal y uno ex son los puntos de premio.
 */
public abstract class Pokemon extends Carta {

    public static final int BONO_DEBILIDAD = 20;

    private final Tipo tipo;
    private final int psMaximos;
    private int psActuales;
    private final int costoRetirada;
    private final List<Ataque> ataques;
    private final List<Tipo> energias = new ArrayList<>();

    protected Pokemon(String nombre, Tipo tipo, int psMaximos, int costoRetirada, List<Ataque> ataques) {
        super(nombre);
        this.tipo = tipo;
        this.psMaximos = psMaximos;
        this.psActuales = psMaximos;
        this.costoRetirada = costoRetirada;
        this.ataques = new ArrayList<>(ataques);
    }

    /** Puntos de premio que recibe el rival cuando este Pokemon es derrotado. */
    public abstract int getPuntosPremio();

    public abstract Etapa getEtapa();

    public boolean esBasico(){
        return getEtapa() == Etapa.BASICO;
    }

    // ---------- Datos basicos ----------

    public Tipo getTipo() {
        return tipo;
    }

    public int getPsMaximos() {
        return psMaximos;
    }

    public int getPsActuales() {
        return psActuales;
    }

    public int getCostoRetirada() {
        return costoRetirada;
    }

    public List<Ataque> getAtaques() {
        return Collections.unmodifiableList(ataques);
    }

    public List<Tipo> getEnergias() {
        return Collections.unmodifiableList(energias);
    }

    /** Ejemplo: "Pikachu (40/60 PS)" */
    public String getResumen() {
        return getNombre() + " (" + psActuales + "/" + psMaximos + " PS)";
    }

    @Override
    public String getDescripcion() {
        return "Pokemon de tipo " + tipo + " con " + psMaximos + " PS";
    }

    // ---------- Vida ----------

    public void recibirDano(int dano) {
        psActuales = Math.max(0, psActuales - dano);
    }

    public void curar(int cantidad) {
        psActuales = Math.min(psMaximos, psActuales + cantidad);
    }

    public boolean estaDerrotado() {
        return psActuales <= 0;
    }

    public boolean estaHerido() {
        return psActuales < psMaximos;
    }

    // ---------- Energias ----------

    public void adjuntarEnergia(Tipo energia) {
        energias.add(energia);
    }

    /** Quita una energia de ese tipo. Devuelve false si no tenia ninguna. */
    public boolean descartarEnergia(Tipo energia) {
        return energias.remove(energia);
    }

    public int cantidadEnergias() {
        return energias.size();
    }

    public int contarEnergias(Tipo energia) {
        int total = 0;
        for (Tipo e : energias) {
            if (e == energia) {
                total++;
            }
        }
        return total;
    }

    // ---------- Combate ----------

    /**
     * Un ataque se puede usar si hay suficientes energias del tipo pedido.
     * Si el costo es incoloro sirve cualquier energia.
     */
    public boolean puedeUsar(Ataque ataque) {
        if (ataque.getTipoCosto() == Tipo.INCOLORO) {
            return energias.size() >= ataque.getCantidadCosto();
        }
        return contarEnergias(ataque.getTipoCosto()) >= ataque.getCantidadCosto();
    }

    /** Tabla de debilidades simplificada del enunciado. */
    public boolean esDebilA(Tipo atacante) {
        if (tipo == Tipo.AGUA) {
            return atacante == Tipo.FUEGO || atacante == Tipo.ELECTRICO;
        }
        if (tipo == Tipo.FUEGO) {
            return atacante == Tipo.AGUA;
        }
        if (tipo == Tipo.INCOLORO) {
            return atacante == Tipo.PSIQUICO;
        }
        return false;
    }

    /** Dano base del ataque mas 20 si el defensor es debil al tipo de este Pokemon. */
    public int calcularDano(Ataque ataque, Pokemon defensor) {
        int dano = ataque.getDanoBase();
        if (defensor.esDebilA(tipo)) {
            dano += BONO_DEBILIDAD;
        }
        return dano;
    }
}
