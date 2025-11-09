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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {

    @FXML private StackPane playerTopPane, playerBottomPane;
    @FXML private StackPane playerLeftPane, playerRightPane;

    @FXML private HBox playerTopHand, playerBottomHand;
    @FXML private TilePane playerLeftHand, playerRightHand;

    @FXML private Label playerTopName, playerLeftName, playerRightName, playerBottomName;

    @FXML private StackPane boardPane; // tablero central

    private int numberOfPlayers = 2;

    private DeckModel deck;
    private final List<PlayerModel> players = new ArrayList<>();

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
                CardModel c = deck.draw();
                if (c == null) continue;
                p.getHand().add(c);
            }
        }
    }

    private void refreshAllHands() {
        // bottom (humano)
        if (playerBottomHand != null) playerBottomHand.getChildren().clear();
        PlayerModel bottom = players.get(0);
        for (CardModel c : bottom.getHand().getCards()) {
            ImageView iv = createCardImageView(c, true, 100, 140);
            if (iv != null && playerBottomHand != null) playerBottomHand.getChildren().add(iv);
        }

        if (numberOfPlayers == 2) {
            if (playerTopHand != null) playerTopHand.getChildren().clear();
            PlayerModel other = players.get(1);
            for (CardModel c : other.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                if (iv != null && playerTopHand != null) playerTopHand.getChildren().add(iv);
            }
        } else if (numberOfPlayers == 3) {
            if (playerLeftHand != null) playerLeftHand.getChildren().clear();
            if (playerRightHand != null) playerRightHand.getChildren().clear();
            PlayerModel left = players.get(1);
            PlayerModel right = players.get(2);
            for (CardModel c : left.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                if (iv != null && playerLeftHand != null) playerLeftHand.getChildren().add(iv);
            }
            for (CardModel c : right.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                if (iv != null && playerRightHand != null) playerRightHand.getChildren().add(iv);
            }
        } else if (numberOfPlayers == 4) {
            if (playerLeftHand != null) playerLeftHand.getChildren().clear();
            if (playerTopHand != null) playerTopHand.getChildren().clear();
            if (playerRightHand != null) playerRightHand.getChildren().clear();
            PlayerModel left = players.get(1);
            PlayerModel top = players.get(2);
            PlayerModel right = players.get(3);
            for (CardModel c : left.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                if (iv != null && playerLeftHand != null) playerLeftHand.getChildren().add(iv);
            }
            for (CardModel c : top.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                if (iv != null && playerTopHand != null) playerTopHand.getChildren().add(iv);
            }
            for (CardModel c : right.getHand().getCards()) {
                ImageView iv = createCardImageView(c, false, 80, 112);
                if (iv != null && playerRightHand != null) playerRightHand.getChildren().add(iv);
            }
        }
    }

    private ImageView createCardImageView(CardModel card, boolean faceUp, double w, double h) {
        String path = faceUp ? imagePathForCard(card) : BACK_IMAGE;
        Image img = loadImage(path, w, h);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(true);
            iv.setUserData(card);
            return iv;
        }

        Label fallback = new Label(card == null ? "?" : card.toString());
        fallback.setMinWidth(w);
        fallback.setMinHeight(h);
        fallback.setStyle("-fx-border-color: #444; -fx-alignment: center; -fx-background-color: linear-gradient(#fff,#eee); -fx-padding:6;");
        javafx.scene.layout.StackPane wrapper = new javafx.scene.layout.StackPane(fallback);
        wrapper.setPrefSize(w, h);
        javafx.scene.image.WritableImage snap = wrapper.snapshot(null, null);
        ImageView iv = new ImageView(snap);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        iv.setUserData(card);
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
        String path = CARDS_FOLDER + r + s + ".png";
        return path;
    }

    private Image loadImage(String resourcePath, double width, double height) {
        String key = resourcePath + "|" + (int) width + "x" + (int) height;
        if (imageCache.containsKey(key)) return imageCache.get(key);
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            return null;
        }
        try (InputStream autoClose = is) {
            Image img = new Image(autoClose, width, height, true, true);
            imageCache.put(key, img);
            return img;
        } catch (Exception e) {
            System.err.println("error cargando imagen " + resourcePath + " -> " + e.getMessage());
            return null;
        }
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

    private void showPlayer(javafx.scene.layout.Pane pane, Label label, String name) {
        if (pane != null) {
            pane.setVisible(true);
            pane.setManaged(true);
        }
        if (label != null) label.setText(name);
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
