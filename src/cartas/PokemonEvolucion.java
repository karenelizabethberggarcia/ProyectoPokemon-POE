package cartas;

import java.util.List;

public abstract class PokemonEvolucion extends Pokemon {

    private final String evolucionaDe;

    protected PokemonEvolucion(String nombre, Tipo tipo, int ps, int costoRetirada,
                                List<Ataque> ataques, String evolucionaDe) {
        super(nombre, tipo, ps, costoRetirada, ataques);
        this.evolucionaDe = evolucionaDe;
    }

    public String evoluvionaDe(){
        return evolucionaDe;
    }

    @Override 
    public int getPuntosPremio(){
        return 1;
    }

    @Override 
    public String getDescripcion(){
        return super.getDescripcion() + " (" +getEtapa() + ", evoluciona de: " + evolucionaDe;
    }
    
}
