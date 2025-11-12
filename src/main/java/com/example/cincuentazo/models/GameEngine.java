package com.example.cincuentazo.models;

import java.util.*;

public class GameEngine {
    private DeckModel deck;
    private final Deque<CardModel> discard;
    private final List<PlayerModel> players;
    private int currentPlayerIndex;
    private CardModel lastPlayed;
    private int tableSum;

    private static final int HAND_SIZE = 4;
    private static final int MAX_SUM = 50;

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
     * Inicializa una partida nueva con un jugador humano y el resto CPU.
     * Crea el mazo, reparte 4 cartas a cada jugador y coloca la primera
     * carta en la mesa para comenzar el juego.
     *
     * @param numPlayers Número total de jugadores (se ajusta entre 2 y 4)
     */
    public void startGame(int numPlayers) {
        int n = Math.max(2, Math.min(numPlayers, 4));
        players.clear();

        //El primer jugador siempre es el humano
        players.add(new PlayerModel("Tú", true));
        for (int i = 1; i < n; i++) {
            players.add(new PlayerModel("CPU" + i, false));
        }

        deck = new DeckModel();
        discard.clear();
        lastPlayed = null;
        tableSum = 0;
        currentPlayerIndex = 0;

        // Reparto inicial de cartas
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


    public PlayerModel currentPlayer() { return players.get(currentPlayerIndex); }
    public List<PlayerModel> getPlayers() { return Collections.unmodifiableList(players); }
    public CardModel getLastPlayed() { return lastPlayed; }
    public int getTableSum() { return tableSum; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    /**
     * Verifica si hay un ganador en el juego.
     * Existe un ganador cuando solo queda un jugador no eliminado.
     *
     * @return true si hay exactamente un jugador activo, false en caso contrario
     */

    public boolean hasWinner() {
        return players.stream().filter(p -> !p.isEliminated()).count() == 1;
    }

    /**
     * Verifica si hay un ganador en el juego.
     * Existe un ganador cuando solo queda un jugador no eliminado.
     *
     * @return true si hay exactamente un jugador activo, false en caso contrario
     */

    public PlayerModel getWinner() {
        if (!hasWinner()) return null;
        return players.stream().filter(p -> !p.isEliminated()).findFirst().orElse(null);
    }

    /**
     * Verifica si una carta puede ser jugada con la suma actual en la mesa.
     * Una carta es jugable si al sumarla no se excede MAX_SUM (50).
     *
     * @param card Carta a verificar
     * @return true si la carta puede ser jugada, false en caso contrario
     */

    public boolean isPlayable(CardModel card) {
        return card != null && card.isPlayable(tableSum);
    }


    /**
     * Aplica la jugada del jugador actual con la carta indicada.
     * Realiza las siguientes operaciones si la jugada es válida:
     *
     *   Mueve la carta visible anterior a la pila de descartes
     *   Coloca la nueva carta como visible en la mesa
     *   Actualiza la suma de la mesa
     *   Remueve la carta de la mano del jugador
     *   Reabastece el mazo desde descartes si es necesario
     *   El jugador roba una nueva carta para mantener 4 en mano
     *
     *
     * @param card Carta a jugar
     * @return ApplyResult con el resultado de la operación (éxito o error)
     * @throws NullPointerException si card es null
     */
    public ApplyResult applyResult(CardModel card) {
        PlayerModel player = currentPlayer();
        Objects.requireNonNull(card, "card");

        //Validar que la carta no exceda el máximo
        if (!isPlayable(card)) {
            return ApplyResult.invalid("La carta excede el máximo de " + MAX_SUM);
        }

        //Validar que la carta pertenezca al jugador
        if (!player.getHand().getCards().contains(card)) {
            return ApplyResult.invalid("La carta no pertenece a la mano del jugador actual");
        }

        //Mover carta anterior a descartes
        if (lastPlayed != null) {
            discard.push(lastPlayed);
        }

        //Aplicar la jugada
        lastPlayed = card;
        tableSum += card.valueWhenPlayed(tableSum);
        player.getHand().removeCard(card);

        //Refill el mazo si está vacío
        refillIfNeeded();

        CardModel drawn = drawFor(player);

        return ApplyResult.ok(tableSum, lastPlayed, drawn);
    }

    /**
     * Roba una carta del mazo para un jugador (si hay) y la agrega a su mano.
     * @param player Jugador que roba la carta
     * @return carta robada o null si no hay cartas disponibles.
     */
    public CardModel drawFor(PlayerModel player) {
        if (player == null) return null;
        CardModel c = deck.draw();
        if (c != null) player.getHand().add(c);
        return c;
    }

    /**
     * Avanza el turno al siguiente jugador no eliminado.
     * Si todos salvo uno están eliminados, no rota (habrá ganador).
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
     * Reabastece el mazo desde descartes (excepto la última jugada) si el mazo está vacío.
     * Las cartas descartadas se barajan antes de incorporarlas al mazo.
     * La última carta jugada (visible en mesa) NO se incluye en el reabastecimiento.
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
     * Elimina al jugador si no tiene ninguna carta jugable.
     * En tal caso, sus cartas se envían al final del mazo (disponibles para ser tomadas).
     * Devuelve true si fue eliminado.
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
     * Estrategia:
     * elige la carta jugable que deje la suma final más baja posible (greedy defensivo).
     * Si empata, prefiere la que tenga menor delta (más negativa/neutral).
     * Si no hay jugables, devuelve null.
     *
     * @param cpu Jugador CPU que debe elegir una carta
     * @return Carta elegida o null si no tiene cartas jugables
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
     * Verifica si el mazo está vacío.
     * Implementación temporal: intenta sacar una carta y devolverla.
     *
     * @return true si el mazo está vacío, false en caso contrario
     */
    private boolean deckIsEmpty() {
        CardModel peek = deck.draw();
        if (peek == null) return true;

        deckAddAllToBottom(Collections.singletonList(peek));
        return false;
    }

    /**
     * Agrega una lista de cartas al final del mazo.
     * Las cartas se agregan en el orden recibido.
     *
     * @param cards Lista de cartas a agregar
     */
    private void deckAddAllToBottom(List<CardModel> cards) {
        if (cards == null || cards.isEmpty()) return;

        for (CardModel c : cards) {
            deck.addToBottom(c);
        }
    }


    /**  Clase inmutable que encapsula el resultado de aplicar una jugada.
     * Contiene información sobre si la jugada fue exitosa, la nueva suma,
     * la carta jugada y la carta robada.*/
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
        public static ApplyResult ok(int sum, CardModel last, CardModel drawn) {
            return new ApplyResult(true, null, sum, last, drawn);
        }
        public static ApplyResult invalid(String msg) {
            return new ApplyResult(false, msg, -1, null, null);
        }
        public boolean ok() { return ok; }
        public String message() { return message; }
        public int newSum() { return newSum; }
        public CardModel lastPlayed() { return lastPlayed; }
        public CardModel drawn() { return drawn; }
    }
}


