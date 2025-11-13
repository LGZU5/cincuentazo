package com.example.cincuentazo.controllers;

import com.example.cincuentazo.models.*;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador principal del juego.
 *
 * Este controlador maneja la vista de juego: muestra las manos de los jugadores,
 * procesa las jugadas (tanto del humano como de las CPUs), actualiza la mesa y
 * gestiona la navegación de regreso a la pantalla de inicio.
 *
 */
public class GameController {

    /** Carpeta base donde se encuentran las imágenes de las cartas en recursos. */
    private static final String CARDS_FOLDER = "/com/example/cincuentazo/assets/images/cards/";

    /** Ruta de la imagen del reverso de las cartas. */
    private static final String BACK_IMAGE = CARDS_FOLDER + "back.png";

    /** Ancho para las cartas del jugador humano. */
    private static final double HUMAN_WIDTH = 100;

    /** Alto para las cartas del jugador humano. */
    private static final double HUMAN_HEIGHT = 140;

    /** Ancho para las cartas de los NPCs. */
    private static final double NPC_WIDTH = 80;

    /** Alto para las cartas de los NPCs. */
    private static final double NPC_HEIGHT = 112;

    @FXML private StackPane playerTopPane, playerBottomPane, playerLeftPane, playerRightPane;
    @FXML private HBox playerTopHand, playerBottomHand;
    @FXML private TilePane playerLeftHand, playerRightHand;
    @FXML private Label playerTopName, playerLeftName, playerRightName, playerBottomName, counterLabel;
    @FXML private ImageView deckImage, lastPlayedImage;

    /** Motor de juego que contiene la lógica del juego. */
    private GameEngine gameEngine;

    /** Número actual de jugadores (2..4). */
    private int numberOfPlayers = 2;

    /** Carta seleccionada por el jugador humano (si existe). */
    private CardModel selectedCard = null;

    /** Vista (ImageView) de la carta seleccionada (para efectos visuales). */
    private ImageView selectedCardView = null;

    /**
     * Inicializador del controlador llamado por JavaFX al cargar el FXML.
     * Oculta todos los paneles de jugadores para evitar mostrar vistas no utilizadas.
     */
    @FXML
    public void initialize() {
        hideAllPlayers();
    }

    /**
     * Configura el número de jugadores e inicia una nueva partida.
     *
     * Este método:
     *
     *   valida y ajusta el número de jugadores a 2..4,
     *   crea e inicializa un GameEngine,
     *   obtiene los nombres de los jugadores,
     *   muestra los paneles correspondientes,
     *   refresca las manos y actualiza la mesa.
     *
     * @param playersCount número de jugadores deseado (se normaliza entre 2 y 4)
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
    }

    /**
     * Muestra un panel de jugador y pone el nombre correspondiente.
     *
     * @param pane  panel (StackPane u otro) correspondiente al jugador
     * @param label etiqueta donde se muestra el nombre
     * @param name  nombre a mostrar
     */
    private void showPlayer(javafx.scene.layout.Pane pane, Label label, String name) {
        if (pane != null) {
            pane.setVisible(true);
            pane.setManaged(true);
        }
        if (label != null) label.setText(name);
    }

    /**
     * Muestra los paneles de jugadores según la lista de nombres recibida.
     *
     * La distribución es:
     *
     *   Índice 0: jugador humano (abajo)
     *   Índices 1..3: NPCs en las posiciones izquierda/arriba/derecha según cantidad
     *
     * @param names lista de nombres (debe contener al menos 1 elemento)
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
            default:
                // no-op
        }
    }

    /**
     * Oculta todos los paneles de jugadores y limpia las etiquetas de nombre.
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
     * Actualiza la visualización de las manos de todos los jugadores.
     *
     * El jugador humano (índice 0) ve sus cartas boca arriba; las CPUs ven el reverso.
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
     * @param container contenedor JavaFX donde se dibujarán las cartas
     * @param player    jugador cuyas cartas se mostrarán
     * @param faceUp    true para mostrar las cartas boca arriba, false para el reverso
     * @param width     ancho de cada imagen de carta
     * @param height    alto de cada imagen de carta
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
     * Actualiza los elementos visuales de la mesa: imagen del mazo, carta jugada y contador.
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

        // Contador (suma de la mesa)
        if (counterLabel != null) {
            counterLabel.setText(String.valueOf(gameEngine.getTableSum()));
        }
    }

    /**
     * Crea una ImageView para una carta con el listener de clic que selecciona la carta.
     *
     * @param card   modelo de la carta
     * @param faceUp true para mostrar el frente, false para el reverso
     * @param width  ancho deseado
     * @param height alto deseado
     * @return ImageView configurada, o null si no se pudo cargar la imagen
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
        cardImage.setOnMouseClicked(e -> selectCard(card, cardImage));
        return cardImage;
    }

    /**
     * Genera la ruta del recurso de imagen para una carta específica.
     *
     * @param card carta para la cual se genera la ruta
     * @return ruta del recurso de imagen (o BACK_IMAGE si no se puede determinar)
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
     * Carga una imagen desde los recursos del proyecto.
     *
     * @param resourcePath ruta del recurso de imagen
     * @param width        ancho deseado
     * @param height       alto deseado
     * @return imagen cargada o null si falla la carga
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
     * Marca una carta como seleccionada por el jugador humano y aplica un efecto visual.
     *
     * @param card carta seleccionada (modelo)
     * @param view ImageView asociado a la carta seleccionada
     */
    private void selectCard(CardModel card, ImageView view) {
        if (selectedCardView != null) {
            // Quitar efecto de la selección anterior
            selectedCardView.setStyle("");
        }

        selectedCard = card;
        selectedCardView = view;

        if (selectedCardView != null) {
            selectedCardView.setStyle("-fx-effect: dropshadow(gaussian, rgba(125,73,255,0.65), 20, 0.8, 0, 0);");
        }
    }

    /**
     * Maneja el evento de jugar la carta seleccionada por el jugador humano.
     *
     * Flujo:
     *
     *   valida que haya una carta seleccionada,
     *   intenta aplicar la jugada en el GameEngine,
     *   si la jugada es válida: limpia la selección, refresca la UI y avanza turno,
     *   si el siguiente jugador es CPU, se procesan sus turnos con delays para la UX.
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

        // Actualizar UI
        refreshAllHands();
        updateBoard();

        // Avanzar turno
        gameEngine.nextTurn();

        // Verifica si el humano puede seguir jugando o procesar CPUs
        if (gameEngine.currentPlayer().isHuman()) {
            checkIfHumanCanPlay();
        } else {
            // Retardo antes de procesar CPUs para mejor UX
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

        if (!human.isHuman()) return; // seguridad: si no es humano, salir

        // Verificar si tiene cartas jugables
        boolean hasPlayableCard = human.getHand().getCards()
                .stream()
                .anyMatch(card -> gameEngine.isPlayable(card));

        if (!hasPlayableCard) {
            // El humano no puede jugar, queda eliminado
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
     *
     * La CPU elige una carta según su estrategia:
     *
     *   si no puede jugar: es eliminada y se avanza turno,
     *   si juega con éxito: se actualiza la UI y se avanza,
     *   si la jugada falla: se elimina y se continua.
     *
     * Entre turnos se usa un PauseTransition para dar sensación de turno humano.
     */
    private void processSingleCPUTurn() {
        // Verifica si hay un ganador antes de procesar
        if (gameEngine.hasWinner()) {
            checkWinner();
            return;
        }

        PlayerModel cpu = gameEngine.currentPlayer();

        // CPU elige carta
        CardModel chosen = gameEngine.cpuChooseCard(cpu);

        if (chosen == null) {
            // CPU no tiene cartas jugables: eliminarla y avanzar turno
            gameEngine.eliminateIfStuck(cpu);
            gameEngine.nextTurn();

            // Verificar ganador después de eliminar
            if (gameEngine.hasWinner()) {
                checkWinner();
                return;
            }

            // Si el siguiente también es CPU, continuar con retardo; si es humano, revisar su estado
            if (!gameEngine.currentPlayer().isHuman()) {
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> processSingleCPUTurn());
                pause.play();
            } else {
                checkIfHumanCanPlay();
            }
            return;
        }

        // CPU intenta jugar la carta elegida
        GameEngine.ApplyResult result = gameEngine.applyResult(chosen);

        if (result.ok()) {
            refreshAllHands();
            updateBoard();
            gameEngine.nextTurn();

            // Verificar ganador después de jugar
            if (gameEngine.hasWinner()) {
                checkWinner();
                return;
            }

            // Si el siguiente también es CPU, continuar con retardo
            if (!gameEngine.currentPlayer().isHuman()) {
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> processSingleCPUTurn());
                pause.play();
            } else {
                // Turno humano: verificar si puede jugar
                checkIfHumanCanPlay();
            }
        } else {
            // Jugada inválida: eliminar CPU y avanzar
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
                checkIfHumanCanPlay();
            }
        }
    }

    /**
     * Comprueba si hay un ganador y muestra un mensaje con el resultado.
     *
     * Nota: en el código original está marcado como "NO ESTA FUNCIONANDO".
     * Si observáis que no se muestra la alerta en algunos casos, comprobar
     * que gameEngine.hasWinner() y gameEngine.getWinner() funcionan correctamente
     * y que no se está llamando a este método desde un hilo no-UI.
     */
    private void checkWinner() {
        if (gameEngine.hasWinner()) {
            PlayerModel winner = gameEngine.getWinner();
            AlertModel.warning("¡Juego terminado!", winner.getName() + " ha ganado!");
        }
    }

    /**
     * Maneja el evento de abandonar la partida.
     *
     * Pide confirmación al usuario; si confirma, muestra la vista de inicio
     * y cierra la ventana actual.
     *
     * @param e evento de acción proveniente de JavaFX
     */
    @FXML
    private void Fold(javafx.event.ActionEvent e) {
        boolean ok = AlertModel.confirm("Confirmacion", "¿Deseas abandonar la partida?");
        if (ok) {
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