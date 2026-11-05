package cartas;

import java.util.List;

/** Pokemon normal: da 1 punto de premio al ser derrotado. */
public class PokemonBasico extends Pokemon {

    public PokemonBasico(String nombre, Tipo tipo, int ps, int costoRetirada, List<Ataque> ataques) {
        super(nombre, tipo, ps, costoRetirada, ataques);
    }

    @Override
    public int getPuntosPremio() {
        return 1;
    }
}
