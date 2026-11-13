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

    public abstract Etapa getEtapaAnterior();

    public boolean puedeEvolucionarDe(Pokemon pokemon){
        return pokemon.getNombre().equals(evolucionaDe) && pokemon.getEtapa() == getEtapaAnterior();
    }

    public void evolucionarSobre(Pokemon anterior){
        if(!puedeEvolucionarDe(anterior)){
            throw new IllegalArgumentException(getNombre() + " no puede evolucionar de: " + anterior.getNombre());
        }
        int dano = anterior.getPsMaximos() - anterior.getPsActuales();
        guardarEtapasAnteriores(anterior);
        for(Tipo energia : anterior.getEnergias()){
            adjuntarEnergia(energia);
        }
        recibirDano(dano);
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
