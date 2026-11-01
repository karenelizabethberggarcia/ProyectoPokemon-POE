package juego;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cartas.Ataque;
import cartas.Carta;
import cartas.Entrenador;
import cartas.Pokemon;
import cartas.Tipo;

/**
 * Controla un duelo completo entre dos jugadores: preparación, turnos,
 * combate y condiciones de victoria.
 */
public class Partida {

    public static final int PUNTOS_PARA_GANAR = 3;
    public static final int CARTAS_INICIALES = 5;

    private static final String[] OPCIONES_TURNO = {
        "Adjuntar energía de zona",
        "Jugar carta de entrenador",
        "Colocar Pokémon en banco",
        "Retirar Pokémon activo",
        "Atacar",
        "Pasar turno"
    };

    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Interaccion interaccion;
    private final Random random = new Random();
    private final List<String> menuTurno = new ArrayList<>();

    private int numeroTurno = 0;
    private Jugador ganador;
    private String motivoVictoria;

    public Partida(Jugador jugador1, Jugador jugador2, Interaccion interaccion) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.interaccion = interaccion;
        for (String opcion : OPCIONES_TURNO) {
            menuTurno.add(opcion);
        }
    }

    public Jugador getGanador() {
        return ganador;
    }

    // ======================================================
    //  Flujo general
    // ======================================================

    public void jugar() {
        repartirManoInicial(jugador1);
        repartirManoInicial(jugador2);

        Jugador actual = random.nextBoolean() ? jugador1 : jugador2;
        Jugador rival = (actual == jugador1) ? jugador2 : jugador1;
        interaccion.mostrarMensaje("Se lanza una moneda... ¡" + actual.getNombre() + " empieza el duelo!");

        prepararCampo(actual);
        prepararCampo(rival);

        while (ganador == null) {
            jugarTurno(actual, rival);
            Jugador temporal = actual;
            actual = rival;
            rival = temporal;
        }
        interaccion.mostrarGanador(ganador, motivoVictoria);
    }

    private void terminarPartida(Jugador vencedor, String motivo) {
        ganador = vencedor;
        motivoVictoria = motivo;
    }

    // ======================================================
    //  Preparación
    // ======================================================

    /** Baraja, reparte 5 cartas y repite (mulligan) mientras no haya ningún Pokémon en la mano. */
    private void repartirManoInicial(Jugador jugador) {
        jugador.getMazo().barajar();
        jugador.robarCartas(CARTAS_INICIALES);

        while (jugador.getPokemonEnMano().isEmpty()) {
            interaccion.mostrarMensaje(jugador.getNombre()
                    + " no tiene Pokémon básicos en su mano inicial: mulligan.");
            jugador.devolverManoAlMazo();
            jugador.getMazo().barajar();
            jugador.robarCartas(CARTAS_INICIALES);
        }
    }

    /** El jugador elige su Pokémon activo y, si quiere, hasta 2 en el banco. */
    private void prepararCampo(Jugador jugador) {
        interaccion.esperarContinuar("Preparación del campo de " + jugador.getNombre() + ".");
        interaccion.mostrarMensaje("Tu mano: " + nombresDeCartas(jugador.getMano()));

        List<Pokemon> disponibles = jugador.getPokemonEnMano();
        int eleccion = interaccion.elegirOpcion(jugador.getNombre() + ", elige tu Pokémon activo:",
                resumenes(disponibles));
        Pokemon activo = disponibles.get(eleccion);
        jugador.quitarDeMano(activo);
        jugador.getCampo().colocarActivo(activo);

        while (!jugador.getCampo().bancoLleno()) {
            List<Pokemon> restantes = jugador.getPokemonEnMano();
            if (restantes.isEmpty()) {
                break;
            }
            List<String> opciones = resumenes(restantes);
            opciones.add("No colocar más Pokémon");
            int opcion = interaccion.elegirOpcion("¿Quieres poner un Pokémon en el banco?", opciones);
            if (opcion == restantes.size()) {
                break;
            }
            Pokemon elegido = restantes.get(opcion);
            jugador.quitarDeMano(elegido);
            jugador.getCampo().agregarABanco(elegido);
        }
    }

    // ======================================================
    //  Turnos
    // ======================================================

    private void jugarTurno(Jugador actual, Jugador rival) {
        numeroTurno++;
        actual.iniciarTurno();
        interaccion.esperarContinuar("Comienza el turno de " + actual.getNombre() + ".");

        Carta robada = actual.robarCarta();
        if (robada == null) {
            terminarPartida(rival, actual.getNombre() + " no tiene cartas en el mazo para robar.");
            return;
        }
        actual.generarEnergiaZona(random);
        interaccion.mostrarMensaje("Robaste: " + robada.getNombre());

        boolean turnoTerminado = false;
        while (!turnoTerminado && ganador == null) {
            interaccion.mostrarTablero(numeroTurno, actual, rival);
            int opcion = interaccion.elegirOpcion("¿Qué deseas hacer?", menuTurno);
            switch (opcion) {
                case 0:
                    adjuntarEnergia(actual);
                    break;
                case 1:
                    jugarEntrenador(actual);
                    break;
                case 2:
                    colocarEnBanco(actual);
                    break;
                case 3:
                    retirarActivo(actual);
                    break;
                case 4:
                    turnoTerminado = atacar(actual, rival);
                    break;
                case 5:
                    turnoTerminado = true;
                    break;
                default:
                    interaccion.mostrarMensaje("Opción no válida.");
            }
        }
        actual.terminarTurno();
    }

    private void adjuntarEnergia(Jugador jugador) {
        if (jugador.getEnergiaZona() == null) {
            interaccion.mostrarMensaje("Ya usaste la energía de la zona en este turno.");
            return;
        }

        List<Pokemon> enJuego = jugador.getCampo().getTodos();
        List<String> opciones = new ArrayList<>();
        for (Pokemon p : enJuego) {
            String lugar = (p == jugador.getCampo().getActivo()) ? "[Activo] " : "[Banco] ";
            opciones.add(lugar + p.getResumen());
        }
        opciones.add("Cancelar");

        String titulo = "¿A qué Pokémon le adjuntas la energía " + jugador.getEnergiaZona().getSimbolo() + "?";
        int eleccion = interaccion.elegirOpcion(titulo, opciones);
        if (eleccion == enJuego.size()) {
            return;
        }

        Pokemon destino = enJuego.get(eleccion);
        Tipo energia = jugador.usarEnergiaZona();
        destino.adjuntarEnergia(energia);
        interaccion.mostrarMensaje("Adjuntas " + energia.getSimbolo() + " a " + destino.getNombre() + ".");
    }

    private void jugarEntrenador(Jugador jugador) {
        List<Entrenador> entrenadores = jugador.getEntrenadoresEnMano();
        if (entrenadores.isEmpty()) {
            interaccion.mostrarMensaje("No tienes cartas de entrenador en la mano.");
            return;
        }

        List<String> opciones = new ArrayList<>();
        for (Entrenador e : entrenadores) {
            String clase = e.esSoporte() ? "Soporte" : "Objeto";
            opciones.add(e.getNombre() + " [" + clase + "] - " + e.getDescripcion());
        }
        opciones.add("Cancelar");

        int eleccion = interaccion.elegirOpcion("¿Qué carta de entrenador juegas?", opciones);
        if (eleccion == entrenadores.size()) {
            return;
        }
        Entrenador carta = entrenadores.get(eleccion);

        if (carta.esSoporte() && jugador.haJugadoSoporte()) {
            interaccion.mostrarMensaje("Solo puedes jugar un soporte por turno.");
            return;
        }
        if (!carta.puedeActivarse(jugador)) {
            interaccion.mostrarMensaje("No puedes jugar " + carta.getNombre() + " ahora: no tendría ningún efecto.");
            return;
        }

        jugador.quitarDeMano(carta);
        jugador.descartar(carta);
        if (carta.esSoporte()) {
            jugador.registrarSoporte();
        }
        interaccion.mostrarMensaje(jugador.getNombre() + " juega " + carta.getNombre() + ".");
        carta.activar(jugador, interaccion);
    }

    private void colocarEnBanco(Jugador jugador) {
        if (jugador.getCampo().bancoLleno()) {
            interaccion.mostrarMensaje("Tu banco está lleno (máximo " + Campo.MAX_BANCO + " Pokémon).");
            return;
        }
        List<Pokemon> enMano = jugador.getPokemonEnMano();
        if (enMano.isEmpty()) {
            interaccion.mostrarMensaje("No tienes Pokémon en la mano.");
            return;
        }

        List<String> opciones = resumenes(enMano);
        opciones.add("Cancelar");
        int eleccion = interaccion.elegirOpcion("¿Qué Pokémon colocas en el banco?", opciones);
        if (eleccion == enMano.size()) {
            return;
        }

        Pokemon elegido = enMano.get(eleccion);
        jugador.quitarDeMano(elegido);
        jugador.getCampo().agregarABanco(elegido);
        interaccion.mostrarMensaje(elegido.getNombre() + " pasa al banco.");
    }

    private void retirarActivo(Jugador jugador) {
        Campo campo = jugador.getCampo();
        Pokemon activo = campo.getActivo();

        if (jugador.haRetirado()) {
            interaccion.mostrarMensaje("Ya retiraste un Pokémon en este turno.");
            return;
        }
        if (campo.getBanco().isEmpty()) {
            interaccion.mostrarMensaje("No tienes Pokémon en el banco para reemplazar al activo.");
            return;
        }
        int costo = jugador.getCostoRetiradaActual();
        if (activo.cantidadEnergias() < costo) {
            interaccion.mostrarMensaje(activo.getNombre() + " necesita " + costo
                    + " energía(s) para retirarse y solo tiene " + activo.cantidadEnergias() + ".");
            return;
        }

        List<Pokemon> banco = campo.getBanco();
        List<String> opciones = resumenes(banco);
        opciones.add("Cancelar");
        String titulo = "Retirar a " + activo.getNombre() + " cuesta " + costo + " energía(s). ¿Quién pasa al frente?";
        int eleccion = interaccion.elegirOpcion(titulo, opciones);
        if (eleccion == banco.size()) {
            return;
        }
        Pokemon reemplazo = banco.get(eleccion);

        pagarEnergias(activo, costo);
        campo.intercambiar(reemplazo);
        jugador.registrarRetirada();
        interaccion.mostrarMensaje(activo.getNombre() + " se retira al banco y " + reemplazo.getNombre()
                + " es el nuevo activo.");
    }

    /** El jugador elige qué energías descartar para pagar un costo. */
    private void pagarEnergias(Pokemon pokemon, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            List<Tipo> distintas = new ArrayList<>();
            for (Tipo e : pokemon.getEnergias()) {
                if (!distintas.contains(e)) {
                    distintas.add(e);
                }
            }
            Tipo aDescartar = distintas.get(0);
            if (distintas.size() > 1) {
                List<String> opciones = new ArrayList<>();
                for (Tipo t : distintas) {
                    opciones.add(t.getSimbolo() + " " + t.getNombre());
                }
                int eleccion = interaccion.elegirOpcion(
                        "¿Qué energía descartas? (" + (i + 1) + " de " + cantidad + ")", opciones);
                aDescartar = distintas.get(eleccion);
            }
            pokemon.descartarEnergia(aDescartar);
        }
    }

    // ======================================================
    //  Combate
    // ======================================================

    /** Devuelve true si el ataque se hizo (y por lo tanto el turno termina). */
    private boolean atacar(Jugador atacante, Jugador defensor) {
        if (numeroTurno == 1) {
            interaccion.mostrarMensaje("El primer jugador no puede atacar en su primer turno.");
            return false;
        }

        Pokemon pokemonAtacante = atacante.getCampo().getActivo();
        List<Ataque> ataques = pokemonAtacante.getAtaques();
        List<String> opciones = new ArrayList<>();
        for (Ataque a : ataques) {
            String estado = pokemonAtacante.puedeUsar(a) ? "" : "  [energía insuficiente]";
            opciones.add(a + estado);
        }
        opciones.add("Cancelar");

        int eleccion = interaccion.elegirOpcion("¿Con qué ataque?", opciones);
        if (eleccion == ataques.size()) {
            return false;
        }
        Ataque ataque = ataques.get(eleccion);
        if (!pokemonAtacante.puedeUsar(ataque)) {
            interaccion.mostrarMensaje("No tienes las energías necesarias para " + ataque.getNombre() + ".");
            return false;
        }

        Pokemon objetivo = defensor.getCampo().getActivo();
        int dano = pokemonAtacante.calcularDano(ataque, objetivo);
        interaccion.mostrarMensaje(pokemonAtacante.getNombre() + " usa " + ataque.getNombre() + " contra "
                + objetivo.getNombre() + ".");
        if (objetivo.esDebilA(pokemonAtacante.getTipo())) {
            interaccion.mostrarMensaje("¡Es débil a " + pokemonAtacante.getTipo() + "! +"
                    + Pokemon.BONO_DEBILIDAD + " de daño.");
        }
        objetivo.recibirDano(dano);
        interaccion.mostrarMensaje(objetivo.getNombre() + " recibe " + dano + " de daño ("
                + objetivo.getPsActuales() + "/" + objetivo.getPsMaximos() + " PS).");

        if (objetivo.estaDerrotado()) {
            resolverDerrota(atacante, defensor);
        }
        return true;
    }

    /** Se encarga de todo lo que pasa cuando el Pokémon activo del defensor llega a 0 PS. */
    private void resolverDerrota(Jugador atacante, Jugador defensor) {
        Pokemon derrotado = defensor.getCampo().quitarActivo();
        defensor.descartar(derrotado);

        int puntos = derrotado.getPuntosPremio();
        atacante.sumarPuntosPremio(puntos);
        interaccion.mostrarMensaje("¡" + derrotado.getNombre() + " fue derrotado! " + atacante.getNombre()
                + " gana " + puntos + " punto(s) de premio (" + atacante.getPuntosPremio() + "/"
                + PUNTOS_PARA_GANAR + ").");

        if (atacante.getPuntosPremio() >= PUNTOS_PARA_GANAR) {
            terminarPartida(atacante, atacante.getNombre() + " reunió " + PUNTOS_PARA_GANAR
                    + " puntos de premio.");
            return;
        }
        if (defensor.getCampo().getBanco().isEmpty()) {
            terminarPartida(atacante, defensor.getNombre() + " se quedó sin Pokémon en juego.");
            return;
        }

        List<Pokemon> banco = defensor.getCampo().getBanco();
        int eleccion = interaccion.elegirOpcion(
                defensor.getNombre() + ", elige el nuevo Pokémon activo:", resumenes(banco));
        Pokemon nuevo = banco.get(eleccion);
        defensor.getCampo().promover(nuevo);
        interaccion.mostrarMensaje(nuevo.getNombre() + " es el nuevo Pokémon activo de " + defensor.getNombre() + ".");
    }

    // ======================================================
    //  Ayudas
    // ======================================================

    private List<String> resumenes(List<Pokemon> pokemon) {
        List<String> textos = new ArrayList<>();
        for (Pokemon p : pokemon) {
            textos.add(p.getResumen());
        }
        return textos;
    }

    private String nombresDeCartas(List<Carta> cartas) {
        List<String> nombres = new ArrayList<>();
        for (Carta c : cartas) {
            nombres.add(c.getNombre());
        }
        return String.join(", ", nombres);
    }
}
