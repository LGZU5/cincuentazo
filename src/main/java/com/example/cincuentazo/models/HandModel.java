package com.example.cincuentazo.models;

import java.util.ArrayList;
import java.util.List;

/**
 * HandModel - mano simple para un jugador.
 *
 * - Usa ArrayList internamente.
 * - Provee métodos sencillos: add, removeCard, removeAt, getCards (copia), size, clear, contains, get.
 */
public class HandModel {

    private final ArrayList<CardModel> cards = new ArrayList<CardModel>();

    public HandModel() {
        // mano inicialmente vacía
    }

    /**
     * Añade una carta a la mano. Si la carta es null no hace nada.
     */
    public void add(CardModel c) {
        if (c == null) return;
        cards.add(c);
    }

    /**
     * Elimina la carta indicada de la mano.
     * Devuelve la carta si se eliminó, o null si no estaba.
     */
    public CardModel removeCard(CardModel c) {
        if (c == null) return null;
        boolean removed = cards.remove(c);
        return removed ? c : null;
    }

    /**
     * Elimina y devuelve la carta en la posición index.
     * Si el índice es inválido devuelve null.
     */
    public CardModel removeAt(int index) {
        if (index < 0 || index >= cards.size()) return null;
        return cards.remove(index);
    }

    /**
     * Devuelve una copia de la lista de cartas (para lectura).
     * No expone la lista interna para evitar modificaciones externas directas.
     */
    public List<CardModel> getCards() {
        return new ArrayList<CardModel>(cards);
    }

    /**
     * Devuelve la carta en la posición index sin eliminarla.
     * Si el índice es inválido devuelve null.
     */
    public CardModel get(int index) {
        if (index < 0 || index >= cards.size()) return null;
        return cards.get(index);
    }

    /**
     * Indica si la mano contiene la carta dada.
     */
    public boolean contains(CardModel c) {
        if (c == null) return false;
        return cards.contains(c);
    }

    /**
     * Número de cartas en la mano.
     */
    public int size() {
        return cards.size();
    }

    /**
     * Vacía la mano.
     */
    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {
        return "HandModel(size=" + size() + ", cards=" + cards + ")";
    }
}
