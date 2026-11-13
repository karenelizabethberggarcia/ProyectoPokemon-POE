package cartas;

/** Entrenador de tipo objeto: se pueden jugar todos los que quieras. */
public abstract class Objeto extends Entrenador {

    protected Objeto(String nombre, String descripcion) {
        super(nombre, descripcion);
    }

    @Override
    public boolean esSoporte() {
        return false;
    }
}
