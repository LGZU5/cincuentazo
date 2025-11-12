package com.example.cincuentazo.controllers;

import com.example.cincuentazo.models.*;
import com.example.cincuentazo.views.InstructionsView;
import com.example.cincuentazo.views.StartView;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import static javax.security.auth.callback.ConfirmationCallback.OK;

public class GameController {

    @FXML private StackPane playerTopPane, playerBottomPane;
    @FXML private StackPane playerLeftPane, playerRightPane;

    @FXML private HBox playerTopHand, playerBottomHand;
    @FXML private TilePane playerLeftHand, playerRightHand;

    @FXML private Label playerTopName, playerLeftName, playerRightName, playerBottomName;

    @FXML private ImageView deckImage;
    @FXML private ImageView lastPlayedImage;
    @FXML private Label counterLabel;

    private GameEngine gameEngine;

    private int numberOfPlayers = 2;

    private final String CARDS_FOLDER = "/com/example/cincuentazo/assets/images/cards/";
    private final String BACK_IMAGE = CARDS_FOLDER + "back.png";
    private static final double HUMAN_WIDTH = 100, HUMAN_HEIGHT = 140, NPC_WIDTH = 80, NPC_HEIGHT  = 112;

    private CardModel selectedCard = null;
    private ImageView selectedCardView = null;

    @FXML
    public void initialize() {
        hideAllPlayers();
    }

    /**
     * Configura el número de jugadores e inicia una nueva partida.
     * Crea el GameEngine, inicializa el juego y actualiza la interfaz.
     *
     * @param playersCount Número de jugadores (entre 2 y 4)
     */
    public void setNumberOfPlayers(int playersCount) {
        numberOfPlayers = Math.max(2, Math.min(playersCount, 4));

        // Inicializar GameEngine
        gameEngine = new GameEngine();
        gameEngine.startGame(numberOfPlayers);

        // Obtener nombres de los jugadores desde GameEngine
        List<String> names = new ArrayList<>();
        for (PlayerModel p : gameEngine.getPlayers()) {
            names.add(p.getName());
        }

        showPlayers(names);
        refreshAllHands();
        updateBoard();
    }

    /**
     * Actualiza la visualización de las manos de todos los jugadores.
     * El jugador humano (índice 0) muestra sus cartas boca arriba,
     * mientras que las CPUs muestran el reverso de las cartas.
     */
    private void refreshAllHands() {
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


    /**
     * Dibuja las cartas de un jugador en el contenedor especificado.
     *
     * @param container Contenedor JavaFX donde se dibujarán las cartas
     * @param player Jugador cuyas cartas se mostrarán
     * @param faceUp true para mostrar las cartas boca arriba, false para el reverso
     * @param width Ancho de cada imagen de carta
     * @param height Alto de cada imagen de carta
     */
    private void paintHand(javafx.scene.layout.Pane container,
                           PlayerModel player,
                           boolean faceUp,
                           double width, double height) {
        if (container == null || player == null) return;
        container.getChildren().clear();
        for (CardModel card : player.getHand().getCards()) {
            ImageView cardImage = createCardImageView(card, faceUp, width, height);
            container.getChildren().add(cardImage);
        }
    }


    /**
     * Actualiza los elementos visuales de la mesa:
     * imagen del mazo, carta actualmente jugada y contador de suma.
     */
    private void updateBoard() {
        // Mazo (reverso)
        Image deckImg = loadImage(BACK_IMAGE, HUMAN_WIDTH, HUMAN_HEIGHT);
        if (deckImage != null && deckImg != null) {
            deckImage.setImage(deckImg);
        }

        // Carta visible en mesa
        CardModel lastPlayed = gameEngine.getLastPlayed();
        if (lastPlayedImage != null && lastPlayed != null) {
            Image face = loadImage(imagePathForCard(lastPlayed), HUMAN_WIDTH, HUMAN_HEIGHT);
            if (face != null) lastPlayedImage.setImage(face);
        }

        // Contador
        if (counterLabel != null) {
            counterLabel.setText(String.valueOf(gameEngine.getTableSum()));
        }
    }


    /**
     * Marca una carta como seleccionada por el jugador humano.
     * Aplica un efecto visual de sombra a la carta seleccionada.
     *
     * @param card Modelo de la carta seleccionada
     * @param view Vista de imagen de la carta seleccionada
     */
    private void selectCard(CardModel card, ImageView view) {
        if (selectedCardView != null) {
            selectedCardView.setStyle("");
        }

        selectedCard = card;
        selectedCardView = view;

        selectedCardView.setStyle("-fx-effect: dropshadow(gaussian, rgba(125,73,255,0.65), 20, 0.8, 0, 0);");
    }


    /**
     * Crea una vista de imagen para una carta con interacción de clic.
     *
     * @param card Modelo de la carta
     * @param faceUp true para mostrar el frente, false para el reverso
     * @param width Ancho de la imagen
     * @param height Alto de la imagen
     * @return ImageView configurada o null si falla la carga
     */
    private ImageView createCardImageView(CardModel card, boolean faceUp, double width, double height) {
        String path = faceUp ? imagePathForCard(card) : BACK_IMAGE;
        Image img = loadImage(path, width, height);
        if (img == null) return null; // sin imagen -> sin vista

        ImageView cardImage = new ImageView(img);
        cardImage.setFitWidth(width);
        cardImage.setFitHeight(height);
        cardImage.setPreserveRatio(true);
        cardImage.setUserData(card);
        cardImage.setOnMouseClicked(e -> {
            selectCard(card, cardImage);
        });
        return cardImage;
    }


    /**
     * Genera la ruta del archivo de imagen para una carta específica.
     *
     * @param card Carta para la cual se genera la ruta
     * @return Ruta del recurso de imagen
     */
    private String imagePathForCard(CardModel card) {
        if (card == null || card.rank == null || card.suit == null) return BACK_IMAGE;
        String rankCard = card.rank;
        String suitCard;
        switch (card.suit) {
            case "picas": suitCard = "P"; break;
            case "corazones": suitCard = "C"; break;
            case "diamantes": suitCard = "D"; break;
            case "treboles": suitCard = "T"; break;
            default: suitCard = card.suit; break;
        }
        return CARDS_FOLDER + rankCard + suitCard + ".png";
    }


    /**
    * Carga una imagen desde los recursos del proyecto.
    *
    * @param resourcePath Ruta del recurso de imagen
    * @param width Ancho deseado
    * @param height Alto deseado
    * @return Imagen cargada o null si falla
    */
    private Image loadImage(String resourcePath, double width, double height) {
        // Carga directa sin caché
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            return new Image(is, width, height, true, true);
        } catch (Exception e) {
            System.err.println("error cargando imagen " + resourcePath + " -> " + e.getMessage());
            return null;
        }
    }


    /**
     * Hace visible un panel de jugador y establece su nombre.
     *
     * @param pane Panel del jugador
     * @param label Etiqueta del nombre
     * @param name Nombre a mostrar
     */
    private void showPlayer(javafx.scene.layout.Pane pane, Label label, String name) {
        if (pane != null) {
            pane.setVisible(true);
            pane.setManaged(true);
        }
        if (label != null) label.setText(name);
    }


    /**
     * Muestra los paneles de jugadores según el número de participantes.
     * Distribuye a los jugadores en las posiciones: abajo (humano),
     * arriba, izquierda y derecha según corresponda.
     *
     * @param names Lista de nombres de jugadores
     */
    private void showPlayers(List<String> names) {
        hideAllPlayers();
        if (playerBottomPane != null) {
            playerBottomPane.setVisible(true);
            playerBottomPane.setManaged(true);
        }
        if (playerBottomName != null) playerBottomName.setText(names.get(0));

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
        }
    }


    /**
     * Oculta todos los paneles de jugadores y limpia sus nombres.
     */
    private void hideAllPlayers() {
        if (playerTopPane != null) { playerTopPane.setVisible(false); playerTopPane.setManaged(false); }
        if (playerLeftPane != null) { playerLeftPane.setVisible(false); playerLeftPane.setManaged(false); }
        if (playerRightPane != null) { playerRightPane.setVisible(false); playerRightPane.setManaged(false); }
        if (playerBottomPane != null) { playerBottomPane.setVisible(false); playerBottomPane.setManaged(false); }

        if (playerTopName != null) playerTopName.setText("");
        if (playerLeftName != null) playerLeftName.setText("");
        if (playerRightName != null) playerRightName.setText("");
        if (playerBottomName != null) playerBottomName.setText("");
    }


    /**
     * Maneja el evento de jugar una carta seleccionada por el jugador humano.
     * Valida la jugada, actualiza el estado del juego y procesa turnos de CPU.
     */
    @FXML
    private void onPlayCard() {
        if (selectedCard == null) {
            AlertModel.warning("Selecciona una carta", "No has seleccionado una carta");
            return;
        }

        // Usar GameEngine para aplicar la jugada
        GameEngine.ApplyResult result = gameEngine.applyResult(selectedCard);

        if (!result.ok()) {
            AlertModel.warning("No se puede jugar la carta", result.message());
            return;
        }

        // Reset selección
        selectedCard = null;
        if (selectedCardView != null) {
            selectedCardView.setStyle("");
        }
        selectedCardView = null;

        //Actualizar UI
        refreshAllHands();
        updateBoard();

        //Avanzar turno (si quieres que las CPUs jueguen automáticamente)
        gameEngine.nextTurn();

        //Verifica si el humano puede seguir jugando
        if (gameEngine.currentPlayer().isHuman()) {
            checkIfHumanCanPlay();
        } else {
            // DELAY ANTES de procesar CPUs
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> processCPUTurns());
            pause.play();
        }
    }


    /**
     * Verifica si el jugador humano tiene cartas jugables en su turno.
     * Si no tiene cartas válidas, lo elimina y verifica si hay un ganador.
     */
    private void checkIfHumanCanPlay() {
        PlayerModel human = gameEngine.currentPlayer();

        if (!human.isHuman()) return; // Por seguridad

        // Verificar si tiene cartas jugables
        boolean hasPlayableCard = human.getHand().getCards()
                .stream()
                .anyMatch(card -> gameEngine.isPlayable(card));

        if (!hasPlayableCard) {
            // El humano no puede jugar, pierde
            gameEngine.eliminateIfStuck(human);
            AlertModel.warning("¡Has perdido!", "No tienes cartas jugables.");

            // Verificar si hay ganador después de eliminar al humano
            if (gameEngine.hasWinner()) {
                PlayerModel winner = gameEngine.getWinner();
                AlertModel.warning("¡Juego terminado!", winner.getName() + " ha ganado!");
            }
        }
    }

    /**
     * Inicia el procesamiento de turnos de CPU si el jugador actual no es humano.
     */
    private void processCPUTurns() {
        if (!gameEngine.currentPlayer().isHuman() && !gameEngine.hasWinner()) {
            processSingleCPUTurn();
        }
    }


    /**
     * Procesa el turno de una CPU individual.
     * La CPU elige una carta según su estrategia, la juega si es posible,
     * o es eliminada si no tiene movimientos válidos.
     * Incluye delays entre turnos para mejor experiencia visual.
     */
    private void processSingleCPUTurn() {
        //Verifica si hay un ganador antes de pasar
        if (gameEngine.hasWinner()) {
            checkWinner();
            return;
        }

        PlayerModel cpu = gameEngine.currentPlayer();

        // CPU elige carta
        CardModel chosen = gameEngine.cpuChooseCard(cpu);

        if (chosen == null) {
            // CPU no tiene cartas jugables, eliminar
            gameEngine.eliminateIfStuck(cpu);
            gameEngine.nextTurn();

            // VERIFICAR GANADOR DESPUÉS DE ELIMINAR
            if (gameEngine.hasWinner()) {
                checkWinner();
                return;
            }

            // Verificar si el siguiente también es CPU
            if (!gameEngine.currentPlayer().isHuman()) {
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> processSingleCPUTurn());
                pause.play();
            } else {
                checkIfHumanCanPlay();
            }
            return;
        }

        // CPU juega
        GameEngine.ApplyResult result = gameEngine.applyResult(chosen);

        if (result.ok()) {
            refreshAllHands();
            updateBoard();
            gameEngine.nextTurn();

            // VERIFICAR GANADOR DESPUÉS DE JUGAR
            if (gameEngine.hasWinner()) {
                checkWinner();
                return;
            }

            // Si el siguiente jugador también es CPU
            if (!gameEngine.currentPlayer().isHuman()) {
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> processSingleCPUTurn());
                pause.play();
            } else {
                // ✅ Turno del humano, verificar si puede jugar
                checkIfHumanCanPlay();
            }
        } else {
            gameEngine.eliminateIfStuck(cpu);
            gameEngine.nextTurn();

            if (gameEngine.hasWinner()) {
                checkWinner();
                return;
            }

            if (!gameEngine.currentPlayer().isHuman()) {
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> processSingleCPUTurn());
                pause.play();
            } else {
                // ✅ Turno del humano, verificar si puede jugar
                checkIfHumanCanPlay();
            }
        }
    }

    /**
     * Verifica si hay un ganador y muestra un mensaje con el resultado.
     * NO ESTA FUNCIONANDO
     */
    private void checkWinner() {
        if (gameEngine.hasWinner()) {
            PlayerModel winner = gameEngine.getWinner();
            AlertModel.warning("¡Juego terminado!", winner.getName() + " ha ganado!");
        }
    }


    /**
     * Maneja el evento de abandonar la partida.
     * Solicita confirmación al usuario y regresa a la pantalla de inicio.
     *
     * @param e Evento de acción de JavaFX
     */
    @FXML
    private void Fold(javafx.event.ActionEvent e) {
        boolean ok = AlertModel.confirm("Confirmacion", "¿Deseas abandonar la partida?");
        if (ok){
            try {
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
