package cartas;

import java.util.List;

/** Pokemon-ex: da 2 puntos de premio al ser derrotado. */
public class PokemonEx extends Pokemon {

    public PokemonEx(String nombre, Tipo tipo, int ps, int costoRetirada, List<Ataque> ataques) {
        super(nombre, tipo, ps, costoRetirada, ataques);
    }

    @Override
    public int getPuntosPremio() {
        return 2;
    }

    @Override 
    public Etapa getEtapa(){
        return Etapa.BASICO;
    }
    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " (Pokemon-ex, vale 2 puntos de premio)";
    }
}
