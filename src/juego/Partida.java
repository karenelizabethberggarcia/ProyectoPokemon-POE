package juego;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cartas.Ataque;
import cartas.Carta;
import cartas.Entrenador;
import cartas.Pokemon;
import cartas.PokemonEvolucion;
import cartas.Tipo;
import consola.ConsolaUI;

/**
 * Controla un duelo completo entre dos jugadores: preparacion, turnos,
 * combate y condiciones de victoria.
 */
public class Partida {

    public static final int PUNTOS_PARA_GANAR = 3;
    public static final int CARTAS_INICIALES = 5;

    private static final String[] OPCIONES_TURNO = {
        "Adjuntar energia de zona",
        "Jugar carta de entrenador",
        "Colocar Pokemon en banco",
        "Evolucionar Pokemon",
        "Retirar Pokemon activo",
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

    public Partida(Jugador jugador12, Jugador jugador22, ConsolaUI consola) {
        this(jugador12, jugador22, (Interaccion) consola);
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
        interaccion.mostrarMensaje("Se lanza una moneda... !" + actual.getNombre() + " empieza el duelo!");

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
    //  Preparacion
    // ======================================================

    /** Baraja, reparte 5 cartas y repite (mulligan) mientras no haya ningun Pokemon en la mano. */
    private void repartirManoInicial(Jugador jugador) {
        jugador.getMazo().barajar();
        jugador.robarCartas(CARTAS_INICIALES);

        while (jugador.getPokemonEnMano().isEmpty()) {
            interaccion.mostrarMensaje(jugador.getNombre()
                    + " no tiene Pokemon basicos en su mano inicial: mulligan.");
            jugador.devolverManoAlMazo();
            jugador.getMazo().barajar();
            jugador.robarCartas(CARTAS_INICIALES);
        }
    }

    /** El jugador elige su Pokemon activo y, si quiere, hasta 2 en el banco. */
    private void prepararCampo(Jugador jugador) {
        interaccion.esperarContinuar("Preparacion del campo de " + jugador.getNombre() + ".");
        interaccion.mostrarMensaje("Tu mano: " + nombresDeCartas(jugador.getMano()));

        List<Pokemon> disponibles = jugador.getPokemonEnMano();
        int eleccion = interaccion.elegirOpcion(jugador.getNombre() + ", elige tu Pokemon activo:",
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
            opciones.add("No colocar mas Pokemon");
            int opcion = interaccion.elegirOpcion("?Quieres poner un Pokemon en el banco?", opciones);
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
            int opcion = interaccion.elegirOpcion("¿Que deseas hacer?", menuTurno);
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
                    evolucionar(actual);
                    break; 
                case 4:
                    retirarActivo(actual);
                    break;
                case 5:
                    turnoTerminado = atacar(actual, rival);
                    break;
                case 6:
                    turnoTerminado = true;
                    break;
                default:
                    interaccion.mostrarMensaje("Opcion no valida.");
            }
        }
        actual.terminarTurno();
    }

    private void adjuntarEnergia(Jugador jugador) {
        if (jugador.getEnergiaZona() == null) {
            interaccion.mostrarMensaje("Ya usaste la energia de la zona en este turno.");
            return;
        }

        List<Pokemon> enJuego = jugador.getCampo().getTodos();
        List<String> opciones = new ArrayList<>();
        for (Pokemon p : enJuego) {
            String lugar = (p == jugador.getCampo().getActivo()) ? "[Activo] " : "[Banco] ";
            opciones.add(lugar + p.getResumen());
        }
        opciones.add("Cancelar");

        String titulo = "?A que Pokemon le adjuntas la energia " + jugador.getEnergiaZona().getSimbolo() + "?";
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

        int eleccion = interaccion.elegirOpcion("?Que carta de entrenador juegas?", opciones);
        if (eleccion == entrenadores.size()) {
            return;
        }
        Entrenador carta = entrenadores.get(eleccion);

        if (carta.esSoporte() && jugador.haJugadoSoporte()) {
            interaccion.mostrarMensaje("Solo puedes jugar un soporte por turno.");
            return;
        }
        if (!carta.puedeActivarse(jugador)) {
            interaccion.mostrarMensaje("No puedes jugar " + carta.getNombre() + " ahora: no tendria ningun efecto.");
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
            interaccion.mostrarMensaje("Tu banco esta lleno (maximo " + Campo.MAX_BANCO + " Pokemon).");
            return;
        }
        List<Pokemon> enMano = jugador.getPokemonEnMano();
        if (enMano.isEmpty()) {
            interaccion.mostrarMensaje("No tienes Pokemon en la mano.");
            return;
        }

        List<String> opciones = resumenes(enMano);
        opciones.add("Cancelar");
        int eleccion = interaccion.elegirOpcion("?Que Pokemon colocas en el banco?", opciones);
        if (eleccion == enMano.size()) {
            return;
        }

        Pokemon elegido = enMano.get(eleccion);
        jugador.quitarDeMano(elegido);
        jugador.getCampo().agregarABanco(elegido);
        elegido.registrarColocacion(numeroTurno);
        interaccion.mostrarMensaje(elegido.getNombre() + " pasa al banco.");
    }

    public String errorNoEvolucion(Jugador jugador, PokemonEvolucion evolucion, Pokemon objetivo){
        if(jugador.getTurnosJugados() <= 1){
            return "No puedes evolucionar en tu primer turno";
        }
        if(!evolucion.puedeEvolucionarDe(objetivo)){
            return evolucion.getNombre() + " solo se coloca sobre " + evolucion.evoluvionaDe()
                    + " (" + evolucion.getEtapaAnterior() + ")";
        }
        if(objetivo.getTurnoColocacion() == numeroTurno){
            if(objetivo.esBasico()){
                return objetivo.getNombre() + " entro en juego este turno";
            }
            return objetivo.getNombre() + " ya evoluciono este turno";
        }
        return  null;
    }

    private void evolucionar(Jugador jugador){
        List<PokemonEvolucion> evoluciones = jugador.getEvoluvionesMano();
        if(evoluciones.isEmpty()){
            interaccion.mostrarMensaje("No tienes cartas de evolucion en mano");
            return;
        }
        if(jugador.getTurnosJugados() <= 1){
            interaccion.mostrarMensaje("No puedes evolucionar en tu primer turno");
            return;
        }
        List<String> opcionesCarta = new ArrayList<>();
        for(PokemonEvolucion e : evoluciones){
            opcionesCarta.add(e.getResumen() + " (" + e.getEtapa() + " evoluciona de " + e.evoluvionaDe() + ")");
        }
        opcionesCarta.add("Cancelar");
        int eleccionCarta = interaccion.elegirOpcion("¿Que carta de evolucion juegas?", opcionesCarta);
        if(eleccionCarta == evoluciones.size()){
            return;
        }
        PokemonEvolucion evolucion = evoluciones.get(eleccionCarta);
        List<Pokemon> enJuego = jugador.getCampo().getTodos();
        List<String> opcionesObjetivo = new ArrayList<>();
        for(Pokemon p : enJuego){
            String lugar = (p ==jugador.getCampo().getActivo() ? "[Activo]" : "[Banco]");
            String motivo = errorNoEvolucion(jugador, evolucion, p);
            opcionesObjetivo.add(lugar + p.getResumen() + (motivo == null ? "" : " [No disponible]"));   
        }
        opcionesObjetivo.add("Cancelar");
        int eleccionObjetivo = interaccion.elegirOpcion("¿Sobre que pokemon colocas a "
        + evolucion.getNombre() + "?", opcionesObjetivo);
        if(eleccionObjetivo == enJuego.size()){
            return;
        }
        Pokemon objetivo = enJuego.get(eleccionObjetivo);
        String motivo = errorNoEvolucion(jugador, evolucion, objetivo);
        if(motivo != null){
            interaccion.mostrarMensaje(motivo);
            return;
        }
        jugador.quitarDeMano(evolucion);
        evolucion.evolucionarSobre(objetivo);
        jugador.getCampo().reemplazar(objetivo, evolucion);
        evolucion.guardarColocacion(numeroTurno);
        interaccion.mostrarMensaje(objetivo.getNombre() + " evoluciona a " + evolucion.getNombre() + "!"
                    + " (" + evolucion.getPsActuales() + "/" + evolucion.getPsMaximos() + "Ps, "
                    + evolucion.cantidadEnergias() + " energias adjuntas)");
    }

    private void retirarActivo(Jugador jugador) {
        Campo campo = jugador.getCampo();
        Pokemon activo = campo.getActivo();

        if (jugador.haRetirado()) {
            interaccion.mostrarMensaje("Ya retiraste un Pokemon en este turno.");
            return;
        }
        if (campo.getBanco().isEmpty()) {
            interaccion.mostrarMensaje("No tienes Pokemon en el banco para reemplazar al activo.");
            return;
        }
        int costo = jugador.getCostoRetiradaActual();
        if (activo.cantidadEnergias() < costo) {
            interaccion.mostrarMensaje(activo.getNombre() + " necesita " + costo
                    + " energia(s) para retirarse y solo tiene " + activo.cantidadEnergias() + ".");
            return;
        }

        List<Pokemon> banco = campo.getBanco();
        List<String> opciones = resumenes(banco);
        opciones.add("Cancelar");
        String titulo = "Retirar a " + activo.getNombre() + " cuesta " + costo + " energia(s). ?Quien pasa al frente?";
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

    /** El jugador elige que energias descartar para pagar un costo. */
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
                        "?Que energia descartas? (" + (i + 1) + " de " + cantidad + ")", opciones);
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
            String estado = pokemonAtacante.puedeUsar(a) ? "" : "  [energia insuficiente]";
            opciones.add(a + estado);
        }
        opciones.add("Cancelar");

        int eleccion = interaccion.elegirOpcion("¿Con que ataque?", opciones);
        if (eleccion == ataques.size()) {
            return false;
        }
        Ataque ataque = ataques.get(eleccion);
        if (!pokemonAtacante.puedeUsar(ataque)) {
            interaccion.mostrarMensaje("No tienes las energias necesarias para " + ataque.getNombre() + ".");
            return false;
        }

        Pokemon objetivo = defensor.getCampo().getActivo();
        int dano = pokemonAtacante.calcularDano(ataque, objetivo);
        interaccion.mostrarMensaje(pokemonAtacante.getNombre() + " usa " + ataque.getNombre() + " contra "
                + objetivo.getNombre() + ".");
        if (objetivo.esDebilA(pokemonAtacante.getTipo())) {
            interaccion.mostrarMensaje("!Es debil a " + pokemonAtacante.getTipo() + "! +"
                    + Pokemon.BONO_DEBILIDAD + " de dano.");
        }
        objetivo.recibirDano(dano);
        interaccion.mostrarMensaje(objetivo.getNombre() + " recibe " + dano + " de dano ("
                + objetivo.getPsActuales() + "/" + objetivo.getPsMaximos() + " PS).");

        if (objetivo.estaDerrotado()) {
            resolverDerrota(atacante, defensor);
        }
        return true;
    }

    /** Se encarga de todo lo que pasa cuando el Pokemon activo del defensor llega a 0 PS. */
    private void resolverDerrota(Jugador atacante, Jugador defensor) {
        Pokemon derrotado = defensor.getCampo().quitarActivo();
        defensor.descartar(derrotado);
        for(Pokemon previo : derrotado.getEtapasAnteriores()){
            defensor.descartar(previo);
        }

        int puntos = derrotado.getPuntosPremio();
        atacante.sumarPuntosPremio(puntos);
        interaccion.mostrarMensaje("!" + derrotado.getNombre() + " fue derrotado! " + atacante.getNombre()
                + " gana " + puntos + " punto(s) de premio (" + atacante.getPuntosPremio() + "/"
                + PUNTOS_PARA_GANAR + ").");

        if (atacante.getPuntosPremio() >= PUNTOS_PARA_GANAR) {
            terminarPartida(atacante, atacante.getNombre() + " reunio " + PUNTOS_PARA_GANAR
                    + " puntos de premio.");
            return;
        }
        if (defensor.getCampo().getBanco().isEmpty()) {
            terminarPartida(atacante, defensor.getNombre() + " se quedo sin Pokemon en juego.");
            return;
        }

        List<Pokemon> banco = defensor.getCampo().getBanco();
        int eleccion = interaccion.elegirOpcion(
                defensor.getNombre() + ", elige el nuevo Pokemon activo:", resumenes(banco));
        Pokemon nuevo = banco.get(eleccion);
        defensor.getCampo().promover(nuevo);
        interaccion.mostrarMensaje(nuevo.getNombre() + " es el nuevo Pokemon activo de " + defensor.getNombre() + ".");
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
