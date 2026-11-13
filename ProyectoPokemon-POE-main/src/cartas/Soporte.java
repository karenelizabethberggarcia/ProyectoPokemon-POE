package cartas;

/** Entrenador de tipo soporte: maximo uno por turno. */
public abstract class Soporte extends Entrenador {

    protected Soporte(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean esSoporte() {
        return true;
    }
}
