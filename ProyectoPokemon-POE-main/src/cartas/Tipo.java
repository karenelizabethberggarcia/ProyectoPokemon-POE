package cartas;

/**
 * Tipos de Pokemon y de energia. Se usa el mismo enum para las dos cosas
 * porque en este juego cada Pokemon es de un tipo y esa misma energia lo alimenta.
 * INCOLORO como energia requerida significa "cualquier energia".
 */
public enum Tipo {
    ELECTRICO("Electrico", "E"),
    FUEGO("Fuego", "F"),
    AGUA("Agua", "A"),
    PSIQUICO("Psiquico", "P"),
    INCOLORO("Incoloro", "*");

    private final String nombre;
    private final String simbolo;

    Tipo(String nombre, String simbolo) {
        this.nombre = nombre;
        this.simbolo = simbolo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSimbolo() {
        return simbolo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
