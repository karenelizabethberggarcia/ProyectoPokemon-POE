package cartas;

public enum Etapa {
    BASICO("Basico"),
    Fase_1("Fase 1"),
    Fase_2("Fase 2");

    private final String nombre;

    Etapa(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }

    @Override 
    public String toString(){
        return nombre;
    }
}
