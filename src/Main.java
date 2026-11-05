import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import consola.ConsolaUI;
import juego.Jugador;
import juego.MazosPredefinidos;
import juego.Partida;

public class Main {

    public static void main(String[] args) {
        // Forzamos UTF-8 para que las tildes y los emojis se vean bien en la consola
        PrintStream salida = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        java.util.Scanner entrada = new java.util.Scanner(System.in, StandardCharsets.UTF_8);
        ConsolaUI consola = new ConsolaUI(entrada, salida);

        try {
            consola.mostrarBienvenida();

            String nombre1 = consola.pedirNombre("Entrenador 1");
            String nombre2 = consola.pedirNombre("Entrenador 2");
            int mazo1 = consola.elegirMazo(nombre1);
            int mazo2 = consola.elegirMazo(nombre2);

            Jugador jugador1 = new Jugador(nombre1, MazosPredefinidos.crear(mazo1));
            Jugador jugador2 = new Jugador(nombre2, MazosPredefinidos.crear(mazo2));

            new Partida(jugador1, jugador2, consola).jugar();
        } catch (NoSuchElementException e) {
            salida.println();
            salida.println("Se cerro la entrada de datos, el duelo se cancela.");
        }
    }
}
