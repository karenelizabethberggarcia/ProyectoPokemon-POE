package cartas;

/**
 * Ataque de un Pokemon. Es inmutable: una vez creado no cambia.
 */
public class Ataque {

    private final String nombre;
    private final Tipo tipoCosto;
    private final int cantidadCosto;
    private final int danoBase;

    public Ataque(String nombre, Tipo tipoCosto, int cantidadCosto, int danoBase) {
        this.nombre = nombre;
        this.tipoCosto = tipoCosto;
        this.cantidadCosto = cantidadCosto;
        this.danoBase = danoBase;
    }

    public String getNombre() {
        return nombre;
    }

    public Tipo getTipoCosto() {
        return tipoCosto;
    }

    public int getCantidadCosto() {
        return cantidadCosto;
    }

    public int getDanoBase() {
        return danoBase;
    }

    @Override
    public String toString() {
        return nombre + " (" + cantidadCosto + tipoCosto.getSimbolo() + "): " + danoBase + " dano";
    }
}
