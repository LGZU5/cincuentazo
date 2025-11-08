package com.cincuentazo.model;

/**
 * Representa una carta simple para el juego Cincuentazo.
 */
public class Card {

    public enum Rank { TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), J("J"), Q("Q"), K("K"), A("A");
        private final String symbol;
        Rank(String symbol) { this.symbol = symbol; }
        public String symbol() { return symbol; }
    }

    public enum Suit { SPADES, HEARTS, DIAMONDS, CLUBS }

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }

    /**
     * Valor base de la carta. El As devuelve 1 como valor base (la elección 10/1 se hace al jugar).
     */
    public int baseValue() {
        switch (rank) {
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 0;
            case TEN: return 10;
            case J:
            case Q:
            case K: return -10;
            case A: return 1;
            default: return 0;
        }
    }

    public boolean isAce() { return rank == Rank.A; }

    @Override
    public String toString() {
        return rank.symbol() + " of " + suit.name();
    }
}