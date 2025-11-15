package com.example.cincuentazo.models;

import java.util.ArrayList;
import java.util.List;

/**
 * HandModel - Represents a simple hand of cards for a player.
 *
 * <p>
 * Internally uses an {@link java.util.ArrayList} to manage the cards.
 * Provides basic management methods for the hand: add, removeCard, removeAt,
 * getCards (returns a copy), size, clear, contains, and get.
 * </p>
 */
public class HandModel {

    private final ArrayList<CardModel> cards = new ArrayList<CardModel>();

    public HandModel() {
        // Initial hand empty
    }

    /**
     * Add a card to your hand. If the card is null, it does nothing
     */
    public void add(CardModel c) {
        if (c == null) return;
        cards.add(c);
    }

    /**
     * Remove the indicated card from the hand
     * Return the card if it was removed, or null if it wasn't in the hand
     */
    public CardModel removeCard(CardModel c) {
        if (c == null) return null;
        boolean removed = cards.remove(c);
        return removed ? c : null;
    }

    /**
     * Removes and returns the card at the index position
     * If the index is invalid, returns null
     */
    public CardModel removeAt(int index) {
        if (index < 0 || index >= cards.size()) return null;
        return cards.remove(index);
    }

    /**
     * Returns a copy of the letter list (for reading).
     * Does not expose the internal list to prevent direct external modifications.
     */
    public List<CardModel> getCards() {
        return new ArrayList<CardModel>(cards);
    }

    /**
     * Returns the card in the index position without deleting it
     * If the index is invalid, it returns null
     */
    public CardModel get(int index) {
        if (index < 0 || index >= cards.size()) return null;
        return cards.get(index);
    }

    /**
     * Indicates if the hand has the given card
     */
    public boolean contains(CardModel c) {
        if (c == null) return false;
        return cards.contains(c);
    }

    /**
     * Number of cards in the hand
     */
    public int size() {
        return cards.size();
    }

    /**
     * Clean the hand
     */
    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {
        return "HandModel(size=" + size() + ", cards=" + cards + ")";
    }
}
