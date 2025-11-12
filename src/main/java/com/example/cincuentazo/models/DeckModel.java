package com.example.cincuentazo.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modelo que representa un mazo de cartas.
 */
public class DeckModel {

    private final ArrayList<CardModel> cards = new ArrayList<>();

    public DeckModel() {
        // Crear las 52 cartas usando los valores de CardModel
        cards.clear();
        for (String suit : CardModel.SUITS) {
            for (String rank : CardModel.RANKS) {
                cards.add(new CardModel(rank, suit));
            }
        }
        shuffle();
    }

    /**
     * Baraja el mazo.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Número de cartas restantes.
     */
    public int size() {
        return cards.size();
    }

    /**
     * Indica si el mazo está vacío.
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void addToBottom(CardModel card) {
        cards.add(0, card);
    }


    /**
     * Saca la carta superior del mazo.
     */
    public CardModel draw() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }
}
