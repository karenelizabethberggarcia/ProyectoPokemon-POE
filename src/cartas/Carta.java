package cartas;

/**
 * Clase base de todas las cartas del juego. Es abstracta porque no existe
 * una "carta genérica": siempre es un Pokémon o un entrenador.
 */
public abstract class Carta {

    private final String nombre;

    protected Carta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    /** Texto corto que describe la carta, cada subclase lo arma a su manera. */
    public abstract String getDescripcion();

    @Override
    public String toString() {
        return nombre;
    }
}
