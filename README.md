# Proyecto-POE: Pokémon TCG Pocket (Genetic Apex)

Mini proyecto 1 de POE, Universidad del Valle.

## Integrantes

| Nombre | Código |
|--------|--------|
| Karen Elizabeth Berg Garcia | 202459506 |

## Descripción del juego

Simulación por consola, para dos jugadores en el mismo computador, del juego de cartas
Pokémon TCG Pocket con las cartas de la expansión Genetic Apex (10 Pokémon y 5 cartas de entrenador).

Cada jugador arma su campo con un Pokémon activo y hasta 2 en el banco, y se turnan para
adjuntar energías, jugar cartas de entrenador, retirar Pokémon y atacar. Gana el primero que:

- acumule **3 puntos de premio** (1 por Pokémon derrotado, 2 si era Pokémon-ex), o
- deje al rival **sin Pokémon** en juego, o
- consiga que el rival **no pueda robar** carta al inicio de su turno (mazo vacío).

## Requisitos

- Java 21 (JDK)
- No se usan librerías externas, solo Java SE estándar.

## Cómo compilar y ejecutar

Desde la carpeta raíz del proyecto.

**Linux / macOS**

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp bin Main
```

**Windows (PowerShell)**

```powershell
chcp 65001
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -Dfile.encoding=UTF-8 -cp bin Main
```

**Desde VS Code**: abrir la carpeta y ejecutar `Main.java` (Run).

### Si ves caracteres raros (tildes, ñ o emojis)

El programa fuerza la salida en UTF-8, pero la consola también tiene que estar en UTF-8:

- En Windows ejecuta `chcp 65001` antes de correr el juego y usa Windows Terminal si puedes
  (la consola clásica de CMD no dibuja bien los emojis).
- Compila siempre con `-encoding UTF-8`.

## Cómo se juega

1. Se escriben los nombres de los entrenadores (ENTER deja el nombre por defecto).
2. Cada uno elige uno de los 4 mazos predefinidos.
3. Se lanza una moneda para ver quién empieza. Cada jugador recibe 5 cartas (con mulligan si no
   trae ningún Pokémon), elige su Pokémon activo y puede poner hasta 2 en el banco.
4. En cada turno se roba una carta, se recibe una energía en la zona de energía y se elige entre:

```
1. Adjuntar energía de zona
2. Jugar carta de entrenador
3. Colocar Pokémon en banco
4. Retirar Pokémon activo
5. Atacar
6. Pasar turno
```

Atacar termina el turno. El primer jugador no puede atacar en su primer turno.

## Reglas implementadas

- **Mazos**: 20 cartas exactas, máximo 2 copias de cada una, mínimo 6 Pokémon. Se validan al
  crear el mazo (`Mazo.validar`), así que el juego funciona con cualquier mazo válido.
- **Energía**: la zona genera 1 energía por turno, de un tipo al azar entre los tipos de los
  Pokémon del mazo. Si no se adjunta, sigue disponible en el turno siguiente (no se acumulan).
  Un costo incoloro (☆) se paga con cualquier energía.
- **Debilidades** (tabla del enunciado, +20 de daño): Fuego vs Agua, Agua vs Fuego,
  Eléctrico vs Agua, Psíquico vs Incoloro.
- **Soportes**: máximo 1 por turno. Los objetos son ilimitados.
- **Retirada**: se pagan energías descartadas (las eliges tú). Turbo Velocidad baja el costo en 1
  durante ese turno (mínimo 0).
- **Poké Ball**: eliges el Pokémon que buscas y se baraja el mazo.
- **Investigación del Profesor**: descarta la mano y roba 2 (si el mazo tiene menos, roba las que haya).
- **Misty**: eliges un Pokémon de Agua en juego y cuántas energías 💧 adjuntas (1 a 3).

### Decisiones donde el enunciado no especifica

- Costo de retirada (no venía en la tabla): Snorlax 3, los Pokémon-ex 2 y todos los demás 1.
- Solo se puede retirar una vez por turno.
- Los Pokémon derrotados y las cartas jugadas van a la pila de descarte.

## Diseño orientado a objetos

```
src/
├── Main.java                   Punto de entrada
├── cartas/
│   ├── Carta                   (abstracta) base de todas las cartas
│   ├── Pokemon                 (abstracta) vida, energías, ataques, daño y debilidad
│   │   ├── PokemonBasico       1 punto de premio
│   │   └── PokemonEx           2 puntos de premio
│   ├── Entrenador              (abstracta) implementa Activable
│   │   ├── Soporte             (abstracta) 1 por turno
│   │   │   ├── InvestigacionProfesor
│   │   │   └── Misty
│   │   └── Objeto              (abstracta) sin límite
│   │       ├── Pocion
│   │       ├── PokeBall
│   │       └── TurboVelocidad
│   ├── Activable               (interfaz) efectos que se activan en el turno
│   ├── Ataque, Tipo            ataque y tipo de energía/Pokémon
│   └── CatalogoCartas          fábrica de las 15 cartas
├── juego/
│   ├── Partida                 turnos, combate y condiciones de victoria
│   ├── Jugador, Campo, Mazo    estado de cada jugador
│   ├── MazosPredefinidos       4 mazos listos para jugar
│   └── Interaccion             (interfaz) lo que la lógica le pide al usuario
└── consola/
    └── ConsolaUI               implementa Interaccion con menús por consola
```

- **Encapsulamiento**: todos los atributos son privados y se accede por métodos con sentido
  (`recibirDano`, `curar`, `adjuntarEnergia`...). Las listas se devuelven como no modificables.
- **Herencia**: la jerarquía de cartas reutiliza lógica en `Carta`, `Pokemon` y `Entrenador`.
- **Polimorfismo**: `getPuntosPremio()` (básico vs ex), `esSoporte()` y `activar()`
  (cada entrenador hace algo distinto, pero `Partida` los trata a todos igual como `Entrenador`).
- **Clases abstractas**: `Carta`, `Pokemon`, `Entrenador`, `Soporte` y `Objeto` no se pueden instanciar.
- **Interfaces**: `Activable` (cartas con efecto) e `Interaccion` (separa la lógica de la consola).
