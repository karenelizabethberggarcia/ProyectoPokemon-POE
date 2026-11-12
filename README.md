# Proyecto-POE: Pokémon TCG Pocket (Genetic Apex)

Mini proyecto de POE, Universidad del Valle.

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

### Decisiones donde el enunciado no especifica

- Costo de retirada (no venía en la tabla): Snorlax 3, los Pokémon-ex 2 y todos los demás 1.
- Solo se puede retirar una vez por turno.
- Los Pokémon derrotados y las cartas jugadas van a la pila de descarte.

- **Encapsulamiento**: todos los atributos son privados y se accede por métodos con sentido
  (`recibirDano`, `curar`, `adjuntarEnergia`...). Las listas se devuelven como no modificables.
- **Herencia**: la jerarquía de cartas reutiliza lógica en `Carta`, `Pokemon` y `Entrenador`.
- **Polimorfismo**: `getPuntosPremio()` (básico vs ex), `esSoporte()` y `activar()`
  (cada entrenador hace algo distinto, pero `Partida` los trata a todos igual como `Entrenador`).
- **Clases abstractas**: `Carta`, `Pokemon`, `Entrenador`, `Soporte` y `Objeto` no se pueden instanciar.
- **Interfaces**: `Activable` (cartas con efecto) e `Interaccion` (separa la lógica de la consola).
