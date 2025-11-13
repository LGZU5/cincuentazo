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
 * Controlador principal del juego.
 * Gestiona la interacción entre la vista (FXML) y el modelo (GameEngine, PlayerModel, CardModel).
 * Permite seleccionar y jugar cartas, actualizar las manos y el tablero,
 * y manejar los turnos de los jugadores CPU mediante un hilo separado.
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

    /** Carta seleccionada por el jugador humano (si existe). */
    private CardModel selectedCard = null;
    /** Vista (ImageView) de la carta seleccionada (para efectos visuales). */
    private ImageView selectedCardView = null;

    /** Hilo único que procesa los turnos de las CPUs. */
    private CpuTurnsThread cpuThread;

    @FXML
    public void initialize() {
        hideAllPlayers();
    }

    /**
     * Configura el número de jugadores e inicia la partida.
     * Arranca el hilo de CPUs (si no está corriendo).
     */
    public void setNumberOfPlayers(int playersCount) {
        numberOfPlayers = Math.max(2, Math.min(playersCount, 4));

        // Inicializar GameEngine y comenzar la partida
        gameEngine = new GameEngine();
        gameEngine.startGame(numberOfPlayers);

        // Obtener nombres de los jugadores desde el motor de juego
        List<String> names = new ArrayList<>();
        for (PlayerModel p : gameEngine.getPlayers()) {
            names.add(p.getName());
        }

        showPlayers(names);
        refreshAllHands();
        updateBoard();

        // Arrancar hilo de CPUs si no existe
        if (cpuThread == null || !cpuThread.isAlive()) {
            cpuThread = new CpuTurnsThread(
                    gameEngine,
                    () -> { refreshAllHands(); updateBoard(); },
                    this::checkWinner,
                    this::checkIfHumanCanPlay
            );
            cpuThread.start();
        }
    }

    private void showPlayer(javafx.scene.layout.Pane pane, Label label, String name) {
        if (pane != null) {
            pane.setVisible(true);
            pane.setManaged(true);
        }
        if (label != null) label.setText(name);
    }

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

    private void refreshAllHands() {
        if (gameEngine == null) return;
        List<PlayerModel> players = gameEngine.getPlayers();

        paintHand(playerBottomHand, players.get(0), true, HUMAN_WIDTH, HUMAN_HEIGHT);

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

    private ImageView createCardImageView(CardModel card, boolean faceUp, double width, double height) {
        String path = faceUp ? imagePathForCard(card) : BACK_IMAGE;
        Image img = loadImage(path, width, height);
        if (img == null) return null;

        ImageView cardImage = new ImageView(img);
        cardImage.setFitWidth(width);
        cardImage.setFitHeight(height);
        cardImage.setPreserveRatio(true);
        cardImage.setUserData(card);

        // Listener solo si la carta está boca arriba (mano del humano)
        if (faceUp) {
            cardImage.setOnMouseClicked(e -> {
                // Solo permitir selección si es turno humano y la carta está en su mano
                if (gameEngine != null && gameEngine.currentPlayer() != null && gameEngine.currentPlayer().isHuman()) {
                    selectCard(card, cardImage);
                }
            });
        }
        return cardImage;
    }

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

    private Image loadImage(String resourcePath, double width, double height) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            return new Image(is, width, height, true, true);
        } catch (Exception e) {
            System.err.println("error cargando imagen " + resourcePath + " -> " + e.getMessage());
            return null;
        }
    }

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
     * Maneja el evento de jugar la carta seleccionada por el jugador humano.
     * La lógica mutante sobre gameEngine se realiza dentro de synchronized(gameEngine)
     * para evitar condiciones de carrera con el hilo de CPUs.
     */
    @FXML
    private void onPlayCard() {
        if (selectedCard == null) {
            AlertModel.warning("Selecciona una carta", "No has seleccionado una carta");
            return;
        }

        if (gameEngine == null) return;

        synchronized (gameEngine) {
            // Validaciones: turno humano y carta en su mano
            if (!gameEngine.currentPlayer().isHuman()) {
                AlertModel.warning("No es tu turno", "Espera a tu turno.");
                return;
            }
            if (!gameEngine.currentPlayer().getHand().contains(selectedCard)) {
                AlertModel.warning("Carta inválida", "La carta no está en tu mano.");
                return;
            }

            // Intentar aplicar jugada
            GameEngine.ApplyResult result = gameEngine.applyResult(selectedCard);
            if (!result.ok()) {
                AlertModel.warning("No se puede jugar la carta", result.message());
                return;
            }

            // Reset selección visual
            selectedCard = null;
            if (selectedCardView != null) {
                selectedCardView.getStyleClass().remove("selected-card");
            }
            selectedCardView = null;

            // Actualizar UI en FX Thread (ya estamos en FX thread)
            refreshAllHands();
            updateBoard();

            // Avanzar turno: lo hará el engine; el CpuTurnsThread detectará si debe actuar
            gameEngine.nextTurn();
        }
        // Si el siguiente es humano, comprobamos su estado; si es CPU, el hilo CPU actuará.
        if (gameEngine.currentPlayer().isHuman()) {
            checkIfHumanCanPlay();
        }
    }

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
                AlertModel.warning("¡Juego terminado!", winner.getName() + " ha ganado!");
                if (cpuThread != null) cpuThread.requestStop();
            }
        }
    }

    /**
     * Comprueba si hay un ganador y muestra un mensaje con el resultado.
     */
    private void checkWinner() {
        if (gameEngine != null && gameEngine.hasWinner()) {
            PlayerModel winner = gameEngine.getWinner();
            AlertModel.warning("¡Juego terminado!", winner.getName() + " ha ganado!");
            if (cpuThread != null) cpuThread.requestStop();
        }
    }

    @FXML
    private void Fold(javafx.event.ActionEvent e) {
        boolean ok = AlertModel.confirm("Confirmacion", "¿Deseas abandonar la partida?");
        if (ok) {
            try {
                // detener CPU thread si corre
                if (cpuThread != null) {
                    cpuThread.requestStop();
                    cpuThread = null;
                }

                StartView startView = StartView.getInstance();
                startView.show();

                Stage current = (Stage) ((Node) e.getSource()).getScene().getWindow();
                current.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}