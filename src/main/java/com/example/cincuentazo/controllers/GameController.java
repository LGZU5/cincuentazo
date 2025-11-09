package com.example.cincuentazo.controllers;

import com.example.cincuentazo.models.CardModel;
import com.example.cincuentazo.models.DeckModel;
import com.example.cincuentazo.models.PlayerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {

    @FXML private HBox playerTopPane, playerBottomPane, playerTopHand, playerBottomHand;
    @FXML private VBox playerLeftPane, playerRightPane, playerLeftHand, playerRightHand;
    @FXML private Label playerTopName, playerLeftName, playerRightName, playerBottomName;

    private int numberOfPlayers = 2;

    private DeckModel deck;
    private final List<PlayerModel> players = new ArrayList<>();

    // cache simple para no recargar imágenes desde disco/jar cada vez
    private final Map<String, Image> imageCache = new HashMap<>();
    private final String CARDS_FOLDER = "/com/example/cincuentazo/assets/images/cards/";
    private final String BACK_IMAGE = CARDS_FOLDER + "back.png";

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
        refreshAllHands();
    }

    private void initDeckAndDeal() {
        deck = new DeckModel();
        players.clear();
        players.add(new PlayerModel("Tú", true));
        for (int i = 1; i < numberOfPlayers; i++) players.add(new PlayerModel("CPU " + i, false));

        int cardsEach = 4;
        for (int round = 0; round < cardsEach; round++) {
            for (PlayerModel p : players) {
                CardModel c = deck.draw(); // draw() quita la carta del mazo
                if (c == null) continue;
                p.getHand().add(c);
            }
        }
    }

    private void refreshAllHands() {
        // bottom (humano)
        playerBottomHand.getChildren().clear();
        PlayerModel bottom = players.get(0);
        for (CardModel c : bottom.getHand().getCards()) {
            ImageView iv = createCardImageView(c, true, 100, 140);
            playerBottomHand.getChildren().add(iv);
        }

        if (numberOfPlayers == 2) {
            playerTopHand.getChildren().clear();
            PlayerModel other = players.get(1);
            for (CardModel c : other.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                playerTopHand.getChildren().add(iv);
            }
        } else if (numberOfPlayers == 3) {
            playerLeftHand.getChildren().clear();
            playerRightHand.getChildren().clear();
            PlayerModel left = players.get(1);
            PlayerModel right = players.get(2);
            for (CardModel c : left.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                playerLeftHand.getChildren().add(iv);
            }
            for (CardModel c : right.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                playerRightHand.getChildren().add(iv);
            }
        } else if (numberOfPlayers == 4) {
            playerLeftHand.getChildren().clear();
            playerTopHand.getChildren().clear();
            playerRightHand.getChildren().clear();
            PlayerModel left = players.get(1);
            PlayerModel top = players.get(2);
            PlayerModel right = players.get(3);
            for (CardModel c : left.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                playerLeftHand.getChildren().add(iv);
            }
            for (CardModel c : top.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                playerTopHand.getChildren().add(iv);
            }
            for (CardModel c : right.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                playerRightHand.getChildren().add(iv);
            }
        }
    }

    private ImageView createCardImageView(CardModel card, boolean faceUp, double w, double h) {
        Image img;
        if (!faceUp) {
            img = loadImage(BACK_IMAGE, w, h);
        } else {
            String path = imagePathForCard(card);
            img = loadImage(path, w, h);
            if (img == null) img = loadImage(BACK_IMAGE, w, h); // fallback
        }
        ImageView iv = new ImageView(img);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        return iv;
    }

    private String imagePathForCard(CardModel card) {
        if (card == null || card.rank == null || card.suit == null) return BACK_IMAGE;
        String r = card.rank;
        String s;
        switch (card.suit) {
            case "♠": s = "P"; break;
            case "♥": s = "C"; break;
            case "♦": s = "D"; break;
            case "♣": s = "T"; break;
            default: s = card.suit; break;
        }
        return CARDS_FOLDER + r + s + ".png";
    }

    private Image loadImage(String resourcePath, double width, double height) {
        String key = resourcePath + "|" + (int) width + "x" + (int) height;
        if (imageCache.containsKey(key)) return imageCache.get(key);
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            Image img = new Image(is, width, height, true, true);
            imageCache.put(key, img);
            return img;
        } catch (Exception e) {
            return null;
        }
    }

    private void showPlayers(List<String> names) {
        hideAllPlayers();
        playerBottomPane.setVisible(true);
        playerBottomName.setText(names.get(0));

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

    private void showPlayer(javafx.scene.layout.Pane pane, Label label, String name) {
        pane.setVisible(true);
        label.setText(name);
    }

    private void hideAllPlayers() {
        if (playerTopPane != null) playerTopPane.setVisible(false);
        if (playerLeftPane != null) playerLeftPane.setVisible(false);
        if (playerRightPane != null) playerRightPane.setVisible(false);
        if (playerBottomPane != null) playerBottomPane.setVisible(false);

        if (playerTopName != null) playerTopName.setText("");
        if (playerLeftName != null) playerLeftName.setText("");
        if (playerRightName != null) playerRightName.setText("");
        if (playerBottomName != null) playerBottomName.setText("");
    }
}