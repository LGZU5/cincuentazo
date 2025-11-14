package com.example.cincuentazo.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class HandModelTest {

    private HandModel hand;
    private CardModel card1;
    private CardModel card2;
    private CardModel card3;

    @BeforeEach
    void setUp() {
        hand = new HandModel();
        card1 = new CardModel("A", "corazones");
        card2 = new CardModel("K", "diamantes");
        card3 = new CardModel("Q", "treboles");
    }


    @Test
    void testConstructor_CreatesEmptyHand() {
        assertEquals(0, hand.size());
        assertTrue(hand.getCards().isEmpty());
    }


    @Test
    void testAdd_MultipleCards() {
        hand.add(card1);
        hand.add(card2);
        hand.add(card3);

        assertEquals(3, hand.size());
        assertTrue(hand.contains(card1));
        assertTrue(hand.contains(card2));
        assertTrue(hand.contains(card3));
    }


    @Test
    void testRemoveCard_ExistingAndNonExisting() {
        hand.add(card1);

        // elimina existente
        CardModel removed = hand.removeCard(card1);
        assertEquals(card1, removed);
        assertEquals(0, hand.size());
        assertFalse(hand.contains(card1));

        // intenta eliminar una que nunca estuvo
        removed = hand.removeCard(card2);
        assertNull(removed);
        assertEquals(0, hand.size());
    }


    @Test
    void testRemoveAt_ValidIndex() {
        hand.add(card1);
        hand.add(card2);
        hand.add(card3);

        CardModel removed = hand.removeAt(1);
        assertEquals(card2, removed);
        assertEquals(2, hand.size());
        assertFalse(hand.contains(card2));
        assertTrue(hand.contains(card1));
        assertTrue(hand.contains(card3));
    }

    @Test
    void testRemoveAt_InvalidIndexReturnsNull() {
        hand.add(card1);

        assertNull(hand.removeAt(-1));
        assertNull(hand.removeAt(5));
        assertEquals(1, hand.size());
    }


    @Test
    void testGetCards_DefensiveCopy() {
        hand.add(card1);
        hand.add(card2);

        List<CardModel> copy = hand.getCards();
        assertEquals(2, copy.size());
        copy.clear(); // modifico la copia

        // la mano original no cambia
        assertEquals(2, hand.size());
        assertTrue(hand.contains(card1));
        assertTrue(hand.contains(card2));
    }


    @Test
    void testGet_ValidAndInvalidIndex() {
        hand.add(card1);
        hand.add(card2);

        assertEquals(card1, hand.get(0));
        assertEquals(card2, hand.get(1));

        assertNull(hand.get(-1));
        assertNull(hand.get(5));
    }


    @Test
    void testClear_RemovesAllCards() {
        hand.add(card1);
        hand.add(card2);
        hand.add(card3);

        hand.clear();

        assertEquals(0, hand.size());
        assertFalse(hand.contains(card1));
        assertFalse(hand.contains(card2));
        assertFalse(hand.contains(card3));
    }

    @Test
    void testToString_NotEmptyIncludesSize() {
        hand.add(card1);
        hand.add(card2);

        String result = hand.toString();
        // depende de tu implementación, ajusta si usas otro formato
        assertTrue(result.contains("2"));
        assertTrue(result.toLowerCase().contains("hand"));
    }
}
