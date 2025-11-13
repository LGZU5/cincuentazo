package com.example.cincuentazo.models;

import java.util.*;

/**
 * Motor de juego que gestiona el estado de una partida de "Cincuentazo".
 *
 * Responsabilidades:
 *
 *   Crear y manejar el mazo y la pila de descartes.
 *   Repartir cartas y mantener las manos de los jugadores.
 *   Aplicar jugadas, avanzar turnos y determinar ganador.
 *   Proveer una estrategia simple para CPUs.
 */
public class GameEngine {

    /** Tamaño inicial de la mano de cada jugador (número de cartas repartidas). */
    private static final int HAND_SIZE = 4;

    /** Suma máxima permitida en la mesa (regla del juego). */
    public static final int MAX_SUM = 50;

    /** Mazo principal del que se roban cartas. */
    private DeckModel deck;

    /** Pila de descartes (stack). La carta superior es la más recientemente descartada. */
    private final Deque<CardModel> discard;

    /** Lista de jugadores en la partida. El índice 0 es el jugador humano. */
    private final List<PlayerModel> players;

    /** Índice del jugador cuyo turno es actualmente. */
    private int currentPlayerIndex;

    /** Carta que actualmente está visible en la mesa (última jugada). */
    private CardModel lastPlayed;

    /** Suma actual de valores en la mesa. */
    private int tableSum;

    /**
     * Constructor del motor de juego.
     * Inicializa las estructuras de datos para jugadores y descartes.
     */
    public GameEngine() {
        this.players = new ArrayList<>();
        this.discard = new ArrayDeque<>();
        this.currentPlayerIndex = 0;
        this.tableSum = 0;
        this.lastPlayed = null;
    }

    /**
     * Inicializa una partida nueva con un jugador humano (índice 0) y el resto CPUs.
     *
     * Flujo:
     *
     *   normaliza el número de jugadores entre 2 y 4,
     *   crea los PlayerModel correspondientes,
     *   crea el mazo (DeckModel),
     *   reparte HAND_SIZE cartas a cada jugador,
     *   coloca una carta inicial en la mesa y calcula la suma inicial.
     *
     * @param numPlayers número total de jugadores deseado (se ajusta a 2..4)
     */
    public void startGame(int numPlayers) {
        int n = Math.max(2, Math.min(numPlayers, 4));
        players.clear();

        // El primer jugador siempre es el humano
        players.add(new PlayerModel("Tú", true));
        for (int i = 1; i < n; i++) {
            players.add(new PlayerModel("CPU" + i, false));
        }

        deck = new DeckModel();
        discard.clear();
        lastPlayed = null;
        tableSum = 0;
        currentPlayerIndex = 0;

        // Reparto inicial de cartas (HAND_SIZE rondas)
        for (int r = 0; r < HAND_SIZE; r++) {
            for (PlayerModel p : players) {
                CardModel c = deck.draw();
                if (c != null) p.getHand().add(c);
            }
        }

        // Colocar carta inicial en mesa
        lastPlayed = deck.draw();
        if (lastPlayed != null) {
            tableSum = lastPlayed.valueWhenPlayed(0);
        } else {
            tableSum = 0;
        }
    }

    /**
     * Devuelve el jugador cuyo turno es actualmente.
     *
     * @return PlayerModel del jugador actual
     */
    public PlayerModel currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Devuelve la lista de jugadores (inmutable).
     *
     * @return lista inmutable de jugadores
     */
    public List<PlayerModel> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Devuelve la última carta jugada y visible en la mesa.
     *
     * @return carta jugada (puede ser null)
     */
    public CardModel getLastPlayed() {
        return lastPlayed;
    }

    /**
     * Devuelve la suma actual de la mesa.
     *
     * @return suma de la mesa
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Indica si existe un ganador en la partida.
     * Hay ganador cuando queda exactamente un jugador no eliminado.
     *
     * @return true si hay un único jugador activo, false en caso contrario
     */
    public boolean hasWinner() {
        return players.stream().filter(p -> !p.isEliminated()).count() == 1;
    }

    /**
     * Obtiene el jugador ganador si existe.
     *
     * @return PlayerModel ganador o null si no hay ganador todavía
     */
    public PlayerModel getWinner() {
        if (!hasWinner()) return null;
        return players.stream().filter(p -> !p.isEliminated()).findFirst().orElse(null);
    }

    /**
     * Comprueba si una carta es jugable dadas las reglas actuales de la mesa.
     *
     * @param card carta a comprobar
     * @return true si la carta puede jugarse sin exceder MAX_SUM
     */
    public boolean isPlayable(CardModel card) {
        return card != null && card.isPlayable(tableSum);
    }

    /**
     * Aplica la jugada del jugador actual con la carta indicada.
     *
     * Operaciones realizadas en orden si la jugada es válida:
     *
     *   valida la jugada (no excede MAX_SUM y pertenece a la mano),
     *   mueve la carta previamente visible a descartes,<
     *   coloca la nueva carta como visible en la mesa,
     *   actualiza la suma de la mesa,
     *   remueve la carta de la mano del jugador,
     *   reabastece el mazo desde descartes si es necesario,
     *   el jugador roba una carta para reponer la mano (si hay mazo).
     *
     * @param card carta a jugar (no debe ser null)
     * @return ApplyResult con información del resultado (éxito, mensaje y cartas)
     * @throws NullPointerException si card es null
     */
    public ApplyResult applyResult(CardModel card) {
        PlayerModel player = currentPlayer();
        Objects.requireNonNull(card, "card");

        // Validar que la carta sea jugable
        if (!isPlayable(card)) {
            return ApplyResult.invalid("La carta excede el máximo de " + MAX_SUM);
        }

        // Validar que la carta pertenezca al jugador actual
        if (!player.getHand().getCards().contains(card)) {
            return ApplyResult.invalid("La carta no pertenece a la mano del jugador actual");
        }

        // Mover carta previa a descartes
        if (lastPlayed != null) {
            discard.push(lastPlayed);
        }

        // Aplicar la jugada
        lastPlayed = card;
        tableSum += card.valueWhenPlayed(tableSum);
        player.getHand().removeCard(card);

        // Reabastecer mazo si es necesario
        refillIfNeeded();

        // Robar carta para reponer mano del jugador
        CardModel drawn = drawFor(player);

        return ApplyResult.ok(tableSum, lastPlayed, drawn);
    }

    /**
     * Roba una carta del mazo para el jugador indicado y la añade a su mano.
     *
     * @param player jugador que roba
     * @return carta robada o null si no había cartas disponibles
     */
    public CardModel drawFor(PlayerModel player) {
        if (player == null) return null;
        CardModel c = deck.draw();
        if (c != null) player.getHand().add(c);
        return c;
    }

    /**
     * Avanza el turno al siguiente jugador no eliminado.
     * Si existe un ganador (solo uno activo) no cambia el turno.
     */
    public void nextTurn() {
        if (hasWinner()) return;
        int n = players.size();
        for (int i = 1; i <= n; i++) {
            int idx = (currentPlayerIndex + i) % n;
            if (!players.get(idx).isEliminated()) {
                currentPlayerIndex = idx;
                return;
            }
        }
    }

    /**
     * Reabastece el mazo desde la pila de descartes (sin incluir la última carta visible).
     * Las cartas descartadas se barajan antes de incorporarlas al mazo.
     */
    public void refillIfNeeded() {
        if (!deckIsEmpty()) return;
        if (discard.isEmpty()) return;

        List<CardModel> pool = new ArrayList<>(discard);
        discard.clear();
        Collections.shuffle(pool);

        deckAddAllToBottom(pool);
    }

    /**
     * Comprueba si el mazo está vacío.
     * Implementación: intenta sacar una carta y devolverla (sin perderla).
     *
     * @return true si el mazo está vacío
     */
    private boolean deckIsEmpty() {
        CardModel peek = deck.draw();
        if (peek == null) return true;

        // Devolver al fondo del mazo la carta extraída para no modificar el estado
        deckAddAllToBottom(Collections.singletonList(peek));
        return false;
    }

    /**
     * Añade una lista de cartas al final (fondo) del mazo en el orden recibido.
     *
     * @param cards lista de cartas a añadir
     */
    private void deckAddAllToBottom(List<CardModel> cards) {
        if (cards == null || cards.isEmpty()) return;
        for (CardModel c : cards) {
            deck.addToBottom(c);
        }
    }

    /**
     * Elimina a un jugador que no tiene cartas jugables.
     *
     * Si el jugador no tiene ninguna carta jugable:
     *
     *   se vacía su mano y las cartas se envían al final del mazo,
     *   se marca al jugador como eliminado.
     *
     * @param player jugador a comprobar/eliminar
     * @return true si el jugador fue eliminado, false si no fue eliminado
     */
    public boolean eliminateIfStuck(PlayerModel player) {
        if (player == null || player.isEliminated()) return false;
        boolean hasPlayable = player.getHand().getCards()
                .stream().anyMatch(c -> c.isPlayable(tableSum));
        if (hasPlayable) return false;

        // Enviar sus cartas al final del mazo
        List<CardModel> toReturn = new ArrayList<>(player.getHand().getCards());
        player.getHand().getCards().clear();
        deckAddAllToBottom(toReturn);

        player.setEliminated(true);
        return true;
    }

    /**
     * Estrategia sencilla para CPU:
     * elige la carta jugable que deje la suma final más baja posible (greedy defensivo).
     * En caso de empate, prefiere la carta con menor delta (más negativa/neutral).
     *
     * @param cpu jugador CPU que debe elegir una carta
     * @return carta elegida o null si no hay cartas jugables
     */
    public CardModel cpuChooseCard(PlayerModel cpu) {
        if (cpu == null || cpu.isEliminated()) return null;

        CardModel best = null;
        int bestFinal = Integer.MAX_VALUE;
        int bestDelta = Integer.MAX_VALUE;

        for (CardModel c : cpu.getHand().getCards()) {
            if (!c.isPlayable(tableSum)) continue;
            int delta = c.valueWhenPlayed(tableSum);
            int finalSum = tableSum + delta;
            if (finalSum > MAX_SUM) continue; // por seguridad

            if (finalSum < bestFinal || (finalSum == bestFinal && delta < bestDelta)) {
                bestFinal = finalSum;
                bestDelta = delta;
                best = c;
            }
        }
        return best;
    }

    /**
     * Clase inmutable que representa el resultado de intentar aplicar una jugada.
     *
     * Proporciona información sobre si la jugada fue exitosa, un mensaje
     * (en caso de error), la nueva suma de la mesa, la carta jugada y la carta robada.
     */
    public static final class ApplyResult {
        private final boolean ok;
        private final String message;
        private final int newSum;
        private final CardModel lastPlayed;
        private final CardModel drawn;

        private ApplyResult(boolean ok, String message, int newSum, CardModel lastPlayed, CardModel drawn) {
            this.ok = ok;
            this.message = message;
            this.newSum = newSum;
            this.lastPlayed = lastPlayed;
            this.drawn = drawn;
        }

        /**
         * Crea un ApplyResult indicando éxito.
         *
         * @param sum nueva suma de la mesa
         * @param last carta que quedó como última jugada
         * @param drawn carta robada después de la jugada (puede ser null)
         * @return ApplyResult válido
         */
        public static ApplyResult ok(int sum, CardModel last, CardModel drawn) {
            return new ApplyResult(true, null, sum, last, drawn);
        }

        /**
         * Crea un ApplyResult indicando jugada inválida con mensaje.
         *
         * @param msg motivo de la invalidación
         * @return ApplyResult que representa el fallo
         */
        public static ApplyResult invalid(String msg) {
            return new ApplyResult(false, msg, -1, null, null);
        }

        /**
         * Indica si la jugada fue exitosa.
         *
         * @return true si la jugada fue aplicada correctamente
         */
        public boolean ok() {
            return ok;
        }

        /**
         * Mensaje asociado al resultado (en caso de error).
         *
         * @return mensaje o null si no aplica
         */
        public String message() {
            return message;
        }
    }
}