package cartas;

/**
 * Tipos de Pokémon y de energía. Se usa el mismo enum para las dos cosas
 * porque en este juego cada Pokémon es de un tipo y esa misma energía lo alimenta.
 * INCOLORO como energía requerida significa "cualquier energía".
 */
public enum Tipo {
    ELECTRICO("Eléctrico", "⚡"),
    FUEGO("Fuego", "🔥"),
    AGUA("Agua", "💧"),
    PSIQUICO("Psíquico", "🔮"),
    INCOLORO("Incoloro", "☆");

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
