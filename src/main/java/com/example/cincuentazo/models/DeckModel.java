package com.example.cincuentazo.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing a deck of cards
 */
public class DeckModel {

    private final ArrayList<CardModel> cards = new ArrayList<>();

    public DeckModel() {
        // Create the 52 cards using the CardModel values
        cards.clear();
        for (String suit : CardModel.SUITS) {
            for (String rank : CardModel.RANKS) {
                cards.add(new CardModel(rank, suit));
            }
        }
        shuffle();
    }

    /**
     * Shuffle the deck
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Number of remaining cards
     */
    public int size() {
        return cards.size();
    }

    /**
     * Indicates if the deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void addToBottom(CardModel card) {
        cards.add(0, card);
    }


    /**
     * Takes the top card out of the deck
     */
    public CardModel draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }
}
