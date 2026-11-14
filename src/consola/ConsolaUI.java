package consola;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import cartas.Carta;
import cartas.Pokemon;
import cartas.Tipo;
import juego.Interaccion;
import juego.Jugador;
import juego.MazosPredefinidos;
import juego.Partida;

/**
 * Interfaz por consola: menus numerados y dibujo del estado del campo.
 */
public class ConsolaUI implements Interaccion {

    private static final String LINEA = "============================================================";
    private static final int ANCHO_BARRA = 10;

    private final Scanner entrada;
    private final PrintStream salida;

    public ConsolaUI(Scanner entrada, PrintStream salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    // ======================================================
    //  Menu inicial
    // ======================================================

    public void mostrarBienvenida() {
        salida.println(LINEA);
        salida.println("  POKEMON TCG POCKET ");
        salida.println(LINEA);
        salida.println("Gana el primero que consiga " + Partida.PUNTOS_PARA_GANAR + " puntos de premio.");
    }

    public String pedirNombre(String nombrePorDefecto) {
        salida.print("Nombre del entrenador (ENTER para \"" + nombrePorDefecto + "\"): ");
        salida.flush();
        String texto = entrada.nextLine().trim();
        return texto.isEmpty() ? nombrePorDefecto : texto;
    }

    /** Devuelve el indice del mazo predefinido elegido. */
    public int elegirMazo(String nombreJugador) {
        List<String> opciones = new ArrayList<>();
        for (int i = 0; i < MazosPredefinidos.cantidad(); i++) {
            opciones.add(MazosPredefinidos.getNombre(i) + " - " + MazosPredefinidos.getDescripcion(i));
        }
        return elegirOpcion(nombreJugador + ", elige tu mazo:", opciones);
    }

    // ======================================================
    //  Interaccion
    // ======================================================

    @Override
    public int elegirOpcion(String titulo, List<String> opciones) {
        while (true) {
            salida.println(LINEA);
            salida.println(titulo);
            for (int i = 0; i < opciones.size(); i++) {
                salida.println((i + 1) + ". " + opciones.get(i));
            }
            salida.println(LINEA);
            salida.print("Opcion: ");
            salida.flush();

            String texto = entrada.nextLine().trim();
            try {
                int numero = Integer.parseInt(texto);
                if (numero >= 1 && numero <= opciones.size()) {
                    return numero - 1;
                }
            } catch (NumberFormatException e) {
                // cae al mensaje de abajo
            }
            salida.println("Opcion no valida, escribe un numero entre 1 y " + opciones.size() + ".");
        }
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        salida.println(mensaje);
    }

    @Override
    public void esperarContinuar(String mensaje) {
        salida.println();
        salida.print(mensaje + " Presiona ENTER para continuar...");
        salida.flush();
        entrada.nextLine();
    }

    @Override
    public void mostrarTablero(int turno, Jugador actual, Jugador rival) {
        salida.println();
        salida.println(LINEA);
        salida.println("TURNO " + turno + " - " + actual.getNombre());
        salida.println(LINEA);

        imprimirCampo("RIVAL (" + rival.getNombre() + ")", rival);
        salida.println("Puntos de premio rival: " + rival.getPuntosPremio() + "/" + Partida.PUNTOS_PARA_GANAR);
        salida.println();

        imprimirCampo("TU CAMPO (" + actual.getNombre() + ")", actual);
        salida.println("Puntos de premio propios: " + actual.getPuntosPremio() + "/" + Partida.PUNTOS_PARA_GANAR);
        salida.println();

        Tipo zona = actual.getEnergiaZona();
        if (zona == null) {
            salida.println("Zona de energia: vacia (ya usaste la de este turno)");
        } else {
            salida.println("Zona de energia: " + zona.getSimbolo() + " (disponible)");
        }

        List<Carta> mano = actual.getMano();
        salida.println("Tu mano (" + mano.size() + " cartas):");
        for (int i = 0; i < mano.size(); i++) {
            salida.println((i + 1) + ". " + mano.get(i).getNombre());
        }
    }

    @Override
    public void mostrarGanador(Jugador ganador, String motivo) {
        salida.println();
        salida.println(LINEA);
        salida.println(" !FIN DEL DUELO! ");
        salida.println("!" + ganador.getNombre() + " se convierte en el nuevo Maestro Pokemon!");
        salida.println(motivo);
        salida.println(LINEA);
    }

    // ======================================================
    //  Dibujo del campo
    // ======================================================

    private void imprimirCampo(String titulo, Jugador jugador) {
        Pokemon activo = jugador.getCampo().getActivo();
        if (activo == null) {
            salida.println(titulo + "  (sin Pokemon activo)");
        } else {
            salida.println(titulo + "  PS: " + barraDeVida(activo) + " "
                    + activo.getPsActuales() + "/" + activo.getPsMaximos());
            salida.println("[Activo] " + activo.getNombre() + "   Energia adjunta: " + energiasComoTexto(activo));
        }

        List<Pokemon> banco = jugador.getCampo().getBanco();
        if (banco.isEmpty()) {
            salida.println("[Banco] (vacio)");
        } else {
            List<String> textos = new ArrayList<>();
            for (Pokemon p : banco) {
                textos.add(p.getNombre() + " (" + p.getPsActuales() + " PS)");
            }
            salida.println("[Banco] " + String.join("   ", textos));
        }
    }

    private String barraDeVida(Pokemon pokemon) {
        int llenos = (int) Math.ceil((double) pokemon.getPsActuales() * ANCHO_BARRA / pokemon.getPsMaximos());
        return repetir("#", llenos) + repetir(".", ANCHO_BARRA - llenos);
    }

    /** Repite un texto varias veces (para las lineas y la barra de vida). */
    private static String repetir(String texto, int veces) {
        String resultado = "";
        for (int i = 0; i < veces; i++) {
            resultado += texto;
        }
        return resultado;
    }

    private String energiasComoTexto(Pokemon pokemon) {
        List<Tipo> energias = new ArrayList<>(pokemon.getEnergias());
        if (energias.isEmpty()) {
            return "ninguna";
        }
        Collections.sort(energias);
        StringBuilder texto = new StringBuilder();
        for (Tipo t : energias) {
            texto.append(t.getSimbolo());
        }
        return texto.toString();
    }
}
