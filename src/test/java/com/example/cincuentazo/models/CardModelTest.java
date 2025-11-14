package com.example.cincuentazo.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias básicas para {@link CardModel}.
 *
 * Se valida:
 * - Construcción de la carta.
 * - Valor base según las reglas del juego.
 * - Valor al ser jugada según la suma actual.
 * - Si una carta es jugable sin exceder 50.
 * - Representación en texto.
 */
class CardModelTest {

    private CardModel asCorazones;
    private CardModel cincoPicas;
    private CardModel nueveTreboles;
    private CardModel jotaDiamantes;

    @BeforeEach
    void setUp() {
        asCorazones   = new CardModel("A", "corazones");
        cincoPicas    = new CardModel("5", "picas");
        nueveTreboles = new CardModel("9", "treboles");
        jotaDiamantes = new CardModel("J", "diamantes");
    }


    @Test
    void testConstructor_GuardaRankYSuit() {
        assertEquals("A", asCorazones.rank);
        assertEquals("corazones", asCorazones.suit);
    }


    @Test
    void testBaseValue_TiposPrincipales() {
        // numérica
        assertEquals(5, cincoPicas.baseValue());
        // nueve
        assertEquals(0, nueveTreboles.baseValue());
        // figura
        assertEquals(-10, jotaDiamantes.baseValue());
        // As
        assertEquals(1, asCorazones.baseValue());
    }


    @Test
    void testValueWhenPlayed_As_CambiaSegunSuma() {
        // suma baja → vale 10
        assertEquals(10, asCorazones.valueWhenPlayed(0));
        assertEquals(10, asCorazones.valueWhenPlayed(30));

        // suma alta → vale 1
        assertEquals(1, asCorazones.valueWhenPlayed(45));
    }

    @Test
    void testValueWhenPlayed_NoAs_UsaBaseValue() {
        assertEquals(5, cincoPicas.baseValue());
        assertEquals(5, cincoPicas.valueWhenPlayed(0));
        assertEquals(5, cincoPicas.valueWhenPlayed(20));
    }


    @Test
    void testIsPlayable_LimiteCincuenta_ConCartaNumerica() {
        assertTrue(cincoPicas.isPlayable(45));   // 45 + 5 = 50
        assertFalse(cincoPicas.isPlayable(46));  // 46 + 5 = 51
    }

    @Test
    void testIsPlayable_As_UsaDiezOUno() {
        // Como 10
        assertTrue(asCorazones.isPlayable(40));  // 40 + 10 = 50

        // Como 1
        assertTrue(asCorazones.isPlayable(49));  // 49 + 1 = 50

        // No jugable
        assertFalse(asCorazones.isPlayable(50)); // 50 + 1 = 51
    }

    @Test
    void testIsPlayable_CasosEspecialesNueveYNegativa() {
        // nueve: valor 0
        assertTrue(nueveTreboles.isPlayable(0));
        assertTrue(nueveTreboles.isPlayable(50)); // 50 + 0 = 50

        // figura: valor negativo
        assertTrue(jotaDiamantes.isPlayable(0));
        assertTrue(jotaDiamantes.isPlayable(50)); // 50 + (-10) = 40
    }


    @Test
    void testToString_FormatoCorrecto() {
        assertEquals("A corazones", asCorazones.toString());
    }
}