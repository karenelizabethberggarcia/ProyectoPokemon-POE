package cartas;

/**
 * Carta de entrenador. Cada carta concreta define su efecto en activar().
 */
public abstract class Entrenador extends Carta implements Activable {

    private final String descripcion;

    protected Entrenador(String nombre, String descripcion) {
        super(nombre);
        this.descripcion = descripcion;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    /** Los soportes se limitan a uno por turno, los objetos no. */
    public abstract boolean esSoporte();
}
