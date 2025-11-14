package com.example.cincuentazo.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PlayerModelTest {

    private PlayerModel humanPlayer;
    private PlayerModel aiPlayer;

    @BeforeEach
    void setUp() {
        humanPlayer = new PlayerModel("Juan", true);
        aiPlayer = new PlayerModel("CPU", false);
    }


    @Test
    void testConstructor_HumanPlayer() {
        assertEquals("Juan", humanPlayer.getName());
        assertTrue(humanPlayer.isHuman());
        assertNotNull(humanPlayer.getHand());
        assertFalse(humanPlayer.isEliminated());
        assertEquals(0, humanPlayer.getHand().size());
    }

    @Test
    void testConstructor_AIPlayer() {
        assertEquals("CPU", aiPlayer.getName());
        assertFalse(aiPlayer.isHuman());
        assertNotNull(aiPlayer.getHand());
        assertFalse(aiPlayer.isEliminated());
    }


    @Test
    void testGetHand_NotNullAndSameInstance() {
        assertNotNull(humanPlayer.getHand());
        assertSame(humanPlayer.getHand(), humanPlayer.getHand());
    }

    @Test
    void testGetHand_CanAddCards() {
        CardModel card = new CardModel("A", "corazones");
        humanPlayer.getHand().add(card);

        assertEquals(1, humanPlayer.getHand().size());
        assertTrue(humanPlayer.getHand().contains(card));
    }

    @Test
    void testGetHand_IndependentBetweenPlayers() {
        humanPlayer.getHand().add(new CardModel("7", "treboles"));

        assertEquals(1, humanPlayer.getHand().size());
        assertEquals(0, aiPlayer.getHand().size());
    }


    @Test
    void testIsEliminated_InitiallyFalse() {
        assertFalse(humanPlayer.isEliminated());
        assertFalse(aiPlayer.isEliminated());
    }

    @Test
    void testSetEliminated_TrueAndFalse() {
        humanPlayer.setEliminated(true);
        assertTrue(humanPlayer.isEliminated());

        humanPlayer.setEliminated(false);
        assertFalse(humanPlayer.isEliminated());
    }

    @Test
    void testSetEliminated_DoesNotAffectOtherPlayers() {
        humanPlayer.setEliminated(true);

        assertTrue(humanPlayer.isEliminated());
        assertFalse(aiPlayer.isEliminated());
    }


    @Test
    void testIntegration_AddCardsAndEliminate_PlayerKeepsHand() {
        PlayerModel player = new PlayerModel("Test", true);

        player.getHand().add(new CardModel("A", "picas"));
        player.getHand().add(new CardModel("K", "diamantes"));

        assertEquals(2, player.getHand().size());

        player.setEliminated(true);
        assertTrue(player.isEliminated());

        assertEquals(2, player.getHand().size());
    }
}
