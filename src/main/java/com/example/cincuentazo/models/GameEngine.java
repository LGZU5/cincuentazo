package com.example.cincuentazo.models;

import java.util.*;

/**
 * The game engine responsible for managing the state of a "Cincuentazo" game.
 *
 * <p>Responsibilities include:
 * <ul>
 * <li>Creating and managing the deck and discard pile.</li>
 * <li>Dealing cards and maintaining player hands.</li>
 * <li>Applying moves, advancing turns, and determining the winner.</li>
 * <li>Providing a simple AI strategy for CPU players.</li>
 * </ul>
 * </p>
 */
public class GameEngine {

    /** Initial hand size for each player (number of cards dealt) */
    private static final int HAND_SIZE = 4;

    /** Maximum allowed sum on the table (game rule) */
    public static final int MAX_SUM = 50;

    /** The main deck from which cards are drawn */
    private DeckModel deck;

    /** The discard pile (stack). The top card is the most recently discarded one */
    private final Deque<CardModel> discard;

    /** List of players in the game. Index 0 is the human player */
    private final List<PlayerModel> players;

    /** Index of the player whose turn it currently is */
    private int currentPlayerIndex;

    /** The card currently visible on the table (last card played) */
    private CardModel lastPlayed;

    /** The current sum of values on the table */
    private int tableSum;

    /**
     * Constructor for the game engine
     * Initializes the data structures for players and discards
     */
    public GameEngine() {
        this.players = new ArrayList<>();
        this.discard = new ArrayDeque<>();
        this.currentPlayerIndex = 0;
        this.tableSum = 0;
        this.lastPlayed = null;
    }

    /**
     * Initializes a new game with one human player (index 0) and the rest CPUs.
     *
     * <ul>
     * <li>Normalizes the number of players (between 2 and 4)</li>
     * <li>Creates the corresponding PlayerModels</li>
     * <li>Creates the DeckModel</li>
     * <li>Deals HAND_SIZE cards to each player</li>
     * <li>Places an initial card on the table and calculates the starting sum</li>
     * </ul>
     * </p>
     *
     * @param numPlayers The total desired number of players (will be clamped to 2-4)
     */
    public void startGame(int numPlayers) {
        int n = Math.max(2, Math.min(numPlayers, 4));
        players.clear();

        // The first player is always the human
        players.add(new PlayerModel("Tú", true));
        for (int i = 1; i < n; i++) {
            players.add(new PlayerModel("CPU" + i, false));
        }

        deck = new DeckModel();
        discard.clear();
        lastPlayed = null;
        tableSum = 0;
        currentPlayerIndex = 0;

        // Initial deal (HAND_SIZE rounds)
        for (int r = 0; r < HAND_SIZE; r++) {
            for (PlayerModel p : players) {
                CardModel c = deck.draw();
                if (c != null) p.getHand().add(c);
            }
        }

        // Place the starting card on the table
        lastPlayed = deck.draw();
        if (lastPlayed != null) {
            tableSum = lastPlayed.valueWhenPlayed(0);
        } else {
            tableSum = 0;
        }
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return PlayerModel of the current player.
     */
    public PlayerModel currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Returns the list of players (unmodifiable).
     *
     * @return Unmodifiable list of players.
     */
    public List<PlayerModel> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Returns the last card played and visible on the table.
     *
     * @return The last played card (can be null).
     */
    public CardModel getLastPlayed() {
        return lastPlayed;
    }

    /**
     * Returns the current sum of the table.
     *
     * @return The table sum.
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Indicates if there is a winner in the game.
     * A winner exists when exactly one non-eliminated player remains.
     *
     * @return true if there is a single active player, false otherwise.
     */
    public boolean hasWinner() {
        return players.stream().filter(p -> !p.isEliminated()).count() == 1;
    }

    /**
     * Gets the winning player, if one exists.
     *
     * @return The winning PlayerModel, or null if there is no winner yet.
     */
    public PlayerModel getWinner() {
        if (!hasWinner()) return null;
        return players.stream().filter(p -> !p.isEliminated()).findFirst().orElse(null);
    }

    /**
     * Checks if a card is playable given the current table rules
     *
     * @param card The card to check
     * @return true if the card can be played without exceeding MAX_SUM
     */
    public boolean isPlayable(CardModel card) {
        return card != null && card.isPlayable(tableSum);
    }

    /**
     * Applies the current player's move using the specified card
     *
     * <p>Operations performed in order if the move is valid:
     * <ol>
     * <li>Validates the move (does not exceed MAX_SUM and belongs to the hand)</li>
     * <li>Moves the previously visible card to the discard pile</li>
     * <li>Places the new card as visible on the table</li>
     * <li>Updates the table sum</li>
     * <li>Removes the card from the player's hand</li>
     * <li>Refills the deck from discards if necessary</li>
     * <li>The player draws a card to replenish their hand (if the deck has cards)</li>
     * </ol>
     * </p>
     *
     * @param card The card to play (must not be null).
     * @return ApplyResult with information about the outcome (success, message, cards)
     * @throws NullPointerException if card is null
     */
    public ApplyResult applyResult(CardModel card) {
        PlayerModel player = currentPlayer();
        Objects.requireNonNull(card, "card");

        // Validate that the card is playable
        if (!isPlayable(card)) {
            return ApplyResult.invalid("La carta excede el máximo de " + MAX_SUM);
        }

        // Validate that the card belongs to the current player
        if (!player.getHand().getCards().contains(card)) {
            return ApplyResult.invalid("La carta no pertenece a la mano del jugador actual");
        }

        // Move previous card to discard
        if (lastPlayed != null) {
            discard.push(lastPlayed);
        }

        // Apply the move
        lastPlayed = card;
        tableSum += card.valueWhenPlayed(tableSum);
        player.getHand().removeCard(card);

        // Refill deck if necessary
        refillIfNeeded();

        // Draw a card to replenish the player's hand
        CardModel drawn = drawFor(player);

        return ApplyResult.ok(tableSum, lastPlayed, drawn);
    }

    /**
     * Draws a card from the deck for the specified player and adds it to their hand.
     *
     * @param player The player who is drawing.
     * @return The card drawn, or null if no cards were available.
     */
    public CardModel drawFor(PlayerModel player) {
        if (player == null) return null;
        CardModel c = deck.draw();
        if (c != null) player.getHand().add(c);
        return c;
    }

    /**
     * Advances the turn to the next non-eliminated player.
     * If a winner exists (only one active), the turn does not change.
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
     * Refills the deck from the discard pile (excluding the last visible card) if the deck is empty.
     * Discarded cards are shuffled before being added to the deck.
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
     * Checks if the deck is empty.
     * Implementation: Attempts to draw a card and return it (without losing it).
     *
     * @return true if the deck is empty.
     */
    private boolean deckIsEmpty() {
        CardModel peek = deck.draw();
        if (peek == null) return true;

        // Return the drawn card to the bottom of the deck to avoid modifying the state
        deckAddAllToBottom(Collections.singletonList(peek));
        return false;
    }

    /**
     * Adds a list of cards to the bottom of the deck in the order received.
     *
     * @param cards The list of cards to add.
     */
    private void deckAddAllToBottom(List<CardModel> cards) {
        if (cards == null || cards.isEmpty()) return;
        for (CardModel c : cards) {
            deck.addToBottom(c);
        }
    }

    /**
     * Eliminates a player who has no playable cards.
     *
     * <p>If the player has no playable cards:
     * <ul>
     * <li>Their hand is emptied, and the cards are sent to the bottom of the deck.</li>
     * <li>The player is marked as eliminated.</li>
     * </ul>
     * </p>
     *
     * @param player The player to check/eliminate.
     * @return true if the player was eliminated, false otherwise.
     */
    public boolean eliminateIfStuck(PlayerModel player) {
        if (player == null || player.isEliminated()) return false;
        boolean hasPlayable = player.getHand().getCards()
                .stream().anyMatch(c -> c.isPlayable(tableSum));
        if (hasPlayable) return false;

        // Send their cards to the bottom of the deck
        List<CardModel> toReturn = new ArrayList<>(player.getHand().getCards());
        player.getHand().getCards().clear();
        deckAddAllToBottom(toReturn);

        player.setEliminated(true);
        return true;
    }

    /**
     * Simple CPU strategy:
     * Chooses the playable card that results in the lowest possible final sum (defensive greedy).
     * In case of a tie, it prefers the card with the smallest delta (most negative/neutral).
     *
     * @param cpu The CPU player choosing a card.
     * @return The chosen card, or null if no cards are playable.
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
            if (finalSum > MAX_SUM) continue; // Safety check

            if (finalSum < bestFinal || (finalSum == bestFinal && delta < bestDelta)) {
                bestFinal = finalSum;
                bestDelta = delta;
                best = c;
            }
        }
        return best;
    }

    /**
     * An immutable class that represents the result of attempting to apply a move.
     *
     * <p>Provides information about whether the move was successful, a message
     * (in case of error), the new table sum, the card played, and the card drawn.
     * </p>
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
         * Creates an ApplyResult indicating success.
         *
         * @param sum      The new table sum.
         * @param last     The card that became the last played card.
         * @param drawn    The card drawn after the move (can be null).
         * @return A valid ApplyResult.
         */
        public static ApplyResult ok(int sum, CardModel last, CardModel drawn) {
            return new ApplyResult(true, null, sum, last, drawn);
        }

        /**
         * Creates an ApplyResult indicating an invalid move.
         *
         _        * @param msg The reason for the invalidation.
         * @return An ApplyResult representing the failure.
         */
        public static ApplyResult invalid(String msg) {
            return new ApplyResult(false, msg, -1, null, null);
        }

        /**
         * Indicates if the move was successful.
         *
         * @return true if the move was applied correctly.
         */
        public boolean ok() {
            return ok;
        }

        /**
         * Message associated with the result (in case of error).
         *
         * @return The message, or null if not applicable.
         */
        public String message() {
            return message;
        }
    }
}