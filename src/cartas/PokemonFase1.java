package cartas;

import java.util.List;

public class PokemonFase1 extends PokemonEvolucion {

    public PokemonFase1(String nombre, Tipo tipo, int ps, int costoRetirada,
                        List<Ataque> ataques, String evolucionaDe){
        super(nombre, tipo, ps, costoRetirada, ataques, evolucionaDe);
    }

    @Override 
    public Etapa getEtapa(){
        return Etapa.Fase_1;
    }


    
}
