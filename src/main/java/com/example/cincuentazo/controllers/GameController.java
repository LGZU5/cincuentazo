package com.example.cincuentazo.controllers;

import com.example.cincuentazo.models.CardModel;
import com.example.cincuentazo.models.DeckModel;
import com.example.cincuentazo.models.PlayerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    @FXML private StackPane playerTopPane, playerBottomPane;
    @FXML private StackPane playerLeftPane, playerRightPane;

    @FXML private HBox playerTopHand, playerBottomHand;
    @FXML private TilePane playerLeftHand, playerRightHand;

    @FXML private Label playerTopName, playerLeftName, playerRightName, playerBottomName;

    @FXML private ImageView deckImage;
    @FXML private ImageView lastPlayedImage;
    @FXML private Label counterLabel;

    private int numberOfPlayers = 2;

    private DeckModel deck;
    private final List<PlayerModel> players = new ArrayList<>();

    private final String CARDS_FOLDER = "/com/example/cincuentazo/assets/images/cards/";
    private final String BACK_IMAGE = CARDS_FOLDER + "back.png";
    private static final double HUMAN_WIDTH = 100, HUMAN_HEIGHT = 140, NPC_WIDTH = 80, NPC_HEIGHT  = 112;

    private CardModel lastPlayed;
    private int counterValue = 0;

    @FXML
    public void initialize() {
        hideAllPlayers();
    }

    public void setNumberOfPlayers(int playersCount) {
        numberOfPlayers = Math.max(2, Math.min(playersCount, 4));
        List<String> names = new ArrayList<>();
        names.add("Tú");
        for (int i = 1; i < numberOfPlayers; i++) names.add("CPU " + i);
        showPlayers(names);
        initDeckAndDeal();
        initBoardStart();
        refreshAllHands();
        updateBoard();
    }

    private void initDeckAndDeal() {
        deck = new DeckModel();
        players.clear();
        players.add(new PlayerModel("Tú", true));
        for (int i = 1; i < numberOfPlayers; i++) players.add(new PlayerModel("CPU " + i, false));

        int cardsEach = 4;
        for (int round = 0; round < cardsEach; round++) {
            for (PlayerModel p : players) {
                CardModel c = deck.draw();
                if (c == null) continue;
                p.getHand().add(c);
            }
        }
    }

    private void initBoardStart() {
        lastPlayed = deck.draw();
        counterValue = 0;
    }

    private void refreshAllHands() {
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
            container.getChildren().add(cardImage);
        }
    }

    private void updateBoard() {
        // Mazo (reverso)
        Image deckImg = loadImage(BACK_IMAGE, HUMAN_WIDTH, HUMAN_HEIGHT);
        if (deckImage != null && deckImg != null) {
            deckImage.setImage(deckImg);
        }

        if (lastPlayedImage != null) {
            Image face = loadImage(imagePathForCard(lastPlayed), HUMAN_WIDTH, HUMAN_HEIGHT);
            if (face != null) lastPlayedImage.setImage(face);
        }

        if (counterLabel != null) counterLabel.setText(String.valueOf(counterValue));
    }

    private ImageView createCardImageView(CardModel card, boolean faceUp, double width, double height) {
        String path = faceUp ? imagePathForCard(card) : BACK_IMAGE;
        Image img = loadImage(path, width, height);
        if (img == null) return null; // sin imagen -> sin vista

        ImageView cardImage = new ImageView(img);
        cardImage.setFitWidth(width);
        cardImage.setFitHeight(height);
        cardImage.setPreserveRatio(true);
        cardImage.setUserData(card);
        return cardImage;
    }

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
}
