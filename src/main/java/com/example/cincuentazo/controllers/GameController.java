package com.example.cincuentazo.controllers;

import com.example.cincuentazo.models.*;
import com.example.cincuentazo.views.StartView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The main controller for the card game.
 * <p>
 * Manages the interaction between the view (FXML) and the model (GameEngine, PlayerModel, CardModel).
 * Handles card selection, playing moves, updating player hands and the board state,
 * and managing CPU player turns via a dedicated thread.
 * </p>
 */
public class GameController {

    private static final String CARDS_FOLDER = "/com/example/cincuentazo/assets/images/cards/";
    private static final String BACK_IMAGE = CARDS_FOLDER + "back.png";
    private static final double HUMAN_WIDTH = 100;
    private static final double HUMAN_HEIGHT = 140;
    private static final double NPC_WIDTH = 80;
    private static final double NPC_HEIGHT = 112;

    @FXML private StackPane playerTopPane, playerBottomPane, playerLeftPane, playerRightPane;
    @FXML private HBox playerTopHand, playerBottomHand;
    @FXML private TilePane playerLeftHand, playerRightHand;
    @FXML private Label playerTopName, playerLeftName, playerRightName, playerBottomName, counterLabel;
    @FXML private ImageView deckImage, lastPlayedImage;

    private GameEngine gameEngine;
    private int numberOfPlayers = 2;

    /** The card currently selected by the human player. */
    private CardModel selectedCard = null;
    /** The ImageView of the selected card (for visual effects). */
    private ImageView selectedCardView = null;

    /** The single thread responsible for processing CPU turns. */
    private CpuTurnsThread cpuThread;

    /**
     * Initializes the controller. This method is called after all FXML
     * fields have been injected.
     */
    @FXML
    public void initialize() {
        hideAllPlayers();
    }


    /**
     * Sets the number of players and starts a new game.
     * Initiates and starts the CPU turns thread if it is not already running.
     *
     * @param playersCount The desired number of players (2 to 4).
     */
    public void setNumberOfPlayers(int playersCount) {
        try {
            numberOfPlayers = Math.max(2, Math.min(playersCount, 4));

            // Initialize GameEngine and start the game
            gameEngine = new GameEngine();
            gameEngine.startGame(numberOfPlayers);

            // Retrieve player names from the game engine
            List<String> names = new ArrayList<>();
            for (PlayerModel p : gameEngine.getPlayers()) {
                names.add(p.getName());
            }

            showPlayers(names);
            refreshAllHands();
            updateBoard();

            // Start CPU thread if it doesn't exist or isn't running
            if (cpuThread == null || !cpuThread.isAlive()) {
                cpuThread = new CpuTurnsThread(
                        gameEngine,
                        () -> {
                            refreshAllHands();
                            updateBoard();
                        },
                        this::checkWinner,
                        this::checkIfHumanCanPlay
                );
                cpuThread.start();
            }
        } catch (Exception e){
            AlertModel.warning("Error al iniciar el juego", "No se pudo inicializar el juego" );
        }
    }

    /**
     * Shows a player pane and sets their name.
     *
     * @param pane The StackPane for the player's area.
     * @param label The Label displaying the player's name.
     * @param name The player's name.
     */
    private void showPlayer(javafx.scene.layout.Pane pane, Label label, String name) {
        if (pane != null) {
            pane.setVisible(true);
            pane.setManaged(true);
        }
        if (label != null) label.setText(name);
    }

    /**
     * Configures the visibility and names of player UI components based on
     * the number of players.
     *
     * @param names A list of player names, starting with the human player.
     */
    private void showPlayers(List<String> names) {
        hideAllPlayers();

        if (playerBottomPane != null) {
            playerBottomPane.setVisible(true);
            playerBottomPane.setManaged(true);
        }
        if (playerBottomName != null && !names.isEmpty()) playerBottomName.setText(names.get(0));

        switch (names.size()) {
            case 2:
                showPlayer(playerTopPane, playerTopName, names.get(1));
                break;
            case 3:
                showPlayer(playerLeftPane, playerLeftName, names.get(1));
                showPlayer(playerRightPane, playerRightName, names.get(2));
                break;
            case 4:
                showPlayer(playerLeftPane, playerLeftName, names.get(1));
                showPlayer(playerTopPane, playerTopName, names.get(2));
                showPlayer(playerRightPane, playerRightName, names.get(3));
                break;
            default:
                // no-op
        }
    }

    /**
     * Hides all player panes and clears their name labels.
     */
    private void hideAllPlayers() {
        if (playerTopPane != null)    { playerTopPane.setVisible(false);    playerTopPane.setManaged(false); }
        if (playerLeftPane != null)   { playerLeftPane.setVisible(false);   playerLeftPane.setManaged(false); }
        if (playerRightPane != null)  { playerRightPane.setVisible(false);  playerRightPane.setManaged(false); }
        if (playerBottomPane != null) { playerBottomPane.setVisible(false); playerBottomPane.setManaged(false); }

        if (playerTopName != null)    playerTopName.setText("");
        if (playerLeftName != null)   playerLeftName.setText("");
        if (playerRightName != null)  playerRightName.setText("");
        if (playerBottomName != null) playerBottomName.setText("");
    }

    /**
     * Refreshes the display of all players' hands based on the current game state.
     */
    private void refreshAllHands() {
        if (gameEngine == null) return;
        List<PlayerModel> players = gameEngine.getPlayers();

        // Human player (always index 0)
        paintHand(playerBottomHand, players.get(0), true, HUMAN_WIDTH, HUMAN_HEIGHT);

        // CPU players
        if (numberOfPlayers == 2) {
            paintHand(playerTopHand, players.get(1), false, NPC_WIDTH, NPC_HEIGHT);
        } else if (numberOfPlayers == 3) {
            paintHand(playerLeftHand,  players.get(1), false, NPC_WIDTH, NPC_HEIGHT);
            paintHand(playerRightHand, players.get(2), false, NPC_WIDTH, NPC_HEIGHT);
        } else if (numberOfPlayers == 4) {
            paintHand(playerLeftHand,  players.get(1), false, NPC_WIDTH, NPC_HEIGHT);
            paintHand(playerTopHand,   players.get(2), false, NPC_WIDTH, NPC_HEIGHT);
            paintHand(playerRightHand, players.get(3), false, NPC_WIDTH, NPC_HEIGHT);
        }
    }

    /**
     * Draws a player's hand in the specified container.
     *
     * @param container The layout pane to display the cards in.
     * @param player The PlayerModel whose hand to display.
     * @param faceUp True if cards should show their face (human player), false otherwise.
     * @param width The desired width for each card image.
     * @param height The desired height for each card image.
     */
    private void paintHand(javafx.scene.layout.Pane container,
                           PlayerModel player,
                           boolean faceUp,
                           double width, double height) {
        if (container == null || player == null) return;

        container.getChildren().clear();
        for (CardModel card : player.getHand().getCards()) {
            ImageView cardImage = createCardImageView(card, faceUp, width, height);
            if (cardImage != null) container.getChildren().add(cardImage);
        }
    }

    /**
     * Updates the game board elements: the deck, the last played card, and the counter label.
     */
    private void updateBoard() {
        Image deckImg = loadImage(BACK_IMAGE, HUMAN_WIDTH, HUMAN_HEIGHT);
        if (deckImage != null && deckImg != null) {
            deckImage.setImage(deckImg);
        }

        CardModel lastPlayed = gameEngine.getLastPlayed();
        if (lastPlayedImage != null && lastPlayed != null) {
            Image face = loadImage(imagePathForCard(lastPlayed), HUMAN_WIDTH, HUMAN_HEIGHT);
            if (face != null) lastPlayedImage.setImage(face);
        }

        if (counterLabel != null) {
            counterLabel.setText(String.valueOf(gameEngine.getTableSum()));
        }
    }

    /**
     * Creates an ImageView for a given card model.
     * Includes an event handler for card selection if the card is face up.
     *
     * @param card The CardModel to display.
     * @param faceUp Whether the card should be shown face up or as a back image.
     * @param width The image width.
     * @param height The image height.
     * @return An ImageView instance of the card, or null if image loading fails.
     */
    private ImageView createCardImageView(CardModel card, boolean faceUp, double width, double height) {
        String path = faceUp ? imagePathForCard(card) : BACK_IMAGE;
        Image img = loadImage(path, width, height);
        if (img == null) return null;

        ImageView cardImage = new ImageView(img);
        cardImage.setFitWidth(width);
        cardImage.setFitHeight(height);
        cardImage.setPreserveRatio(true);
        cardImage.setUserData(card);

        // Listener only if the card is face up (human player's hand)
        if (faceUp) {
            cardImage.setOnMouseClicked(e -> {
                // Only allow selection if it's the human's turn
                if (gameEngine != null && gameEngine.currentPlayer() != null && gameEngine.currentPlayer().isHuman()) {
                    selectCard(card, cardImage);
                }
            });
        }
        return cardImage;
    }

    /**
     * Generates the resource path for a card's face image.
     *
     * @param card The CardModel.
     * @return The resource path string.
     */
    private String imagePathForCard(CardModel card) {
        if (card == null || card.rank == null || card.suit == null) return BACK_IMAGE;

        String rankCard = card.rank;
        String suitCard;
        switch (card.suit) {
            case "picas":     suitCard = "P"; break;
            case "corazones": suitCard = "C"; break;
            case "diamantes": suitCard = "D"; break;
            case "treboles":  suitCard = "T"; break;
            default:          suitCard = card.suit; break;
        }
        return CARDS_FOLDER + rankCard + suitCard + ".png";
    }

    /**
     * Loads an image resource from the application's classpath.
     *
     * @param resourcePath The path to the image resource.
     * @param width The desired width.
     * @param height The desired height.
     * @return The loaded Image object, or null on failure.
     */
    private Image loadImage(String resourcePath, double width, double height) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            return new Image(is, width, height, true, true);
        } catch (Exception e) {
            AlertModel.warning("Error al cargar la imagen","Ha ocurrido un error al cargar la imagen" + resourcePath);
            return null;
        }
    }

    /**
     * Handles the visual selection/deselection of a card by the human player.
     *
     * @param card The CardModel that was clicked.
     * @param view The ImageView of the card that was clicked.
     */
    private void selectCard(CardModel card, ImageView view) {
        if (selectedCardView != null) {
            selectedCardView.getStyleClass().remove("selected-card");
        }

        selectedCard = card;
        selectedCardView = view;

        if (selectedCardView != null) {
            selectedCardView.getStyleClass().add("selected-card");
        }
    }

    /**
     * Handles the FXML action for playing the currently selected card by the human player.
     * <p>
     * Critical game logic changes are wrapped in a {@code synchronized(gameEngine)} block
     * to prevent race conditions with the CPU turns thread.
     * </p>
     */
    @FXML
    private void onPlayCard() {
        if (selectedCard == null) {
            AlertModel.warning("Selecciona una carta", "No has seleccionado una carta");
            return;
        }

        if (gameEngine == null) return;

        synchronized (gameEngine) {
            // Validations: human turn and card in hand
            if (!gameEngine.currentPlayer().isHuman()) {
                AlertModel.warning("No es tu turno", "Espera a tu turno.");
                return;
            }
            if (!gameEngine.currentPlayer().getHand().contains(selectedCard)) {
                AlertModel.warning("Carta inválida", "La carta no está en tu mano.");
                return;
            }

            // Attempt to apply the move
            GameEngine.ApplyResult result = gameEngine.applyResult(selectedCard);
            if (!result.ok()) {
                AlertModel.warning("No se puede jugar la carta", result.message());
                return;
            }

            // Reset visual selection
            selectedCard = null;
            if (selectedCardView != null) {
                selectedCardView.getStyleClass().remove("selected-card");
            }
            selectedCardView = null;

            //Update UI on FX Thread
            refreshAllHands();
            updateBoard();

            // Advance turn
            gameEngine.nextTurn();
        }
        // Check if the next player (if human) is stuck. CPU thread will handle CPU players.
        if (gameEngine.currentPlayer().isHuman()) {
            checkIfHumanCanPlay();
        }
    }

    /**
     * Checks if the current human player has any playable cards.
     * If not, the player is eliminated and game win conditions are checked.
     */
    private void checkIfHumanCanPlay() {
        PlayerModel human = gameEngine.currentPlayer();

        if (human == null || !human.isHuman()) return;

        boolean hasPlayableCard = human.getHand().getCards()
                .stream()
                .anyMatch(card -> gameEngine.isPlayable(card));

        if (!hasPlayableCard) {
            synchronized (gameEngine) {
                gameEngine.eliminateIfStuck(human);
            }
            AlertModel.warning("¡Has perdido!", "No tienes cartas jugables.");

            if (gameEngine.hasWinner()) {
                PlayerModel winner = gameEngine.getWinner();
                AlertModel.warning("¡Juego terminado!", winner.getName() + " haz ganado!");
                if (cpuThread != null) cpuThread.requestStop();
            }
        }
    }

    /**
     * Checks if a winner exists and displays the game over message.
     */
    private void checkWinner() {
        if (gameEngine != null && gameEngine.hasWinner()) {
            PlayerModel winner = gameEngine.getWinner();
            AlertModel.warning("¡Juego terminado!", winner.getName() + " haz ganado!");
            if (cpuThread != null) cpuThread.requestStop();
        }
    }

    /**
     * Handles the action for the human player to fold (quit) the current game.
     * It prompts for confirmation, stops the CPU thread, and returns to the start view.
     *
     * @param e The ActionEvent from the UI.
     */
    @FXML
    private void Fold(javafx.event.ActionEvent e) {
        boolean ok = AlertModel.confirm("Confirmacion", "¿Deseas abandonar la partida?");
        if (ok) {
            try {
                // Stop CPU thread
                if (cpuThread != null) {
                    cpuThread.requestStop();
                    cpuThread = null;
                }

                StartView startView = StartView.getInstance();
                startView.show();

                Stage current = (Stage) ((Node) e.getSource()).getScene().getWindow();
                current.close();

            } catch (Exception ex) {
                AlertModel.warning("Error al salir", "No se pudo cerrar correctamente la ventana.");
            }
        }
    }
}