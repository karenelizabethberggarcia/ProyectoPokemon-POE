package cartas;

import java.util.List;

public class PokemonFase2 extends PokemonEvolucion{
    
    public PokemonFase2(String nombre, Tipo tipo, int ps, int costoRetirada,
                        List<Ataque> ataques, String evolucionaDe){
        super(nombre, tipo, ps, costoRetirada, ataques, evolucionaDe);
    }

    @Override 
    public Etapa getEtapa(){
        return Etapa.Fase_2;
    }
}
