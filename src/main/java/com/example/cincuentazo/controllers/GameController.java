package com.example.cincuentazo.controllers;

import com.example.cincuentazo.models.CardModel;
import com.example.cincuentazo.models.DeckModel;
import com.example.cincuentazo.models.HandModel;
import com.example.cincuentazo.models.PlayerModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    // ruta CORREGIDA a los assets en tu repo
    private final String CARDS_FOLDER = "/com/example/cincuentazo/assets/images/cards/";
    private final String BACK_IMAGE = CARDS_FOLDER + "back.png";

    @FXML
    public void initialize() {
        hideAllPlayers();

        // listeners para redimensionar las cartas de la mano inferior cuando cambie el tamaño del contenedor
        Platform.runLater(() -> {
            if (playerBottomHand != null) {
                playerBottomHand.heightProperty().addListener((obs, oldV, newV) -> adjustBottomCardSizes());
                playerBottomHand.widthProperty().addListener((obs, oldV, newV) -> adjustBottomCardSizes());
            }
        });

        verifyCardResources(); // temporal: borra esta línea cuando confirmes que todo carga
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
        System.out.println("[GameController] Reparto completo. Cartas por jugador:");
        for (int i = 0; i < players.size(); i++) {
            System.out.println("  player " + i + " (" + players.get(i).getName() + "): " + players.get(i).getHand().size());
        }
        System.out.println("[GameController] Deck size after deal: " + (deck == null ? "null" : deck.size()));
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

        // Force layout update and ajustar tamaños de las cartas de la mano inferior después del layout
        if (playerBottomHand != null) playerBottomHand.requestLayout();
        Platform.runLater(this::adjustBottomCardSizes);
    }

    private ImageView createCardImageView(CardModel card, boolean faceUp, double w, double h) {
        String path = faceUp ? imagePathForCard(card) : BACK_IMAGE;
        Image img = loadImage(path, w, h);
        System.out.println("[GameController] createCardImageView -> path=" + path + " loaded=" + (img != null));
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            iv.setPreserveRatio(true);
            iv.setUserData(card);
            return iv;
        }

        // fallback visual — se verá aún si falta la imagen
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
        // mapping en español: picas, corazones, diamantes, treboles -> P, C, D, T
        switch (card.suit) {
            case "♠": s = "P"; break; // picas
            case "♥": s = "C"; break; // corazones
            case "♦": s = "D"; break; // diamantes
            case "♣": s = "T"; break; // treboles
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
            System.err.println("[GameController] recurso NO encontrado: " + resourcePath);
            return null;
        }
        try (InputStream autoClose = is) {
            Image img = new Image(autoClose, width, height, true, true);
            imageCache.put(key, img);
            System.out.println("[GameController] cargada imagen: " + resourcePath + " (" + img.getWidth() + "x" + img.getHeight() + ")");
            return img;
        } catch (Exception e) {
            System.err.println("[GameController] error cargando imagen " + resourcePath + " -> " + e.getMessage());
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
            pane.setManaged(true); // importante: que el layout gestione el espacio cuando se muestre
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

    /**
     * Ajusta dinámicamente fitHeight/fitWidth de las ImageView de la mano inferior
     * para evitar que se corten cuando el contenedor cambia de tamaño.
     */
    private void adjustBottomCardSizes() {
        if (playerBottomHand == null) return;
        double availableHeight = playerBottomHand.getHeight();
        if (availableHeight <= 0) return;
        // dejamos un pequeño margen
        double maxCardHeight = Math.min(140, Math.max(24, availableHeight - 12));
        for (Node n : playerBottomHand.getChildren()) {
            if (n instanceof ImageView) {
                ImageView iv = (ImageView) n;
                Image img = iv.getImage();
                double ratio = (img != null && img.getHeight() > 0) ? (img.getWidth() / img.getHeight()) : (100.0 / 140.0);
                iv.setFitHeight(maxCardHeight);
                iv.setFitWidth(maxCardHeight * ratio);
            }
        }
        playerBottomHand.requestLayout();
    }

    // helper temporal para depurar recursos
    private void verifyCardResources() {
        String[] checks = {
                BACK_IMAGE,
                CARDS_FOLDER + "AP.png",  // As de picas
                CARDS_FOLDER + "10C.png", // 10 corazones
                CARDS_FOLDER + "KD.png"   // K diamantes
        };
        for (String p : checks) {
            InputStream is = getClass().getResourceAsStream(p);
            System.out.println("[resources] " + p + " -> " + (is != null ? "OK" : "MISSING"));
            try { if (is != null) is.close(); } catch (Exception ignored) {}
        }

        // debug fx:id injection
        System.out.println("[injection] playerBottomHand = " + playerBottomHand);
        System.out.println("[injection] playerTopHand = " + playerTopHand);
        System.out.println("[injection] playerLeftHand = " + playerLeftHand);
        System.out.println("[injection] playerRightHand = " + playerRightHand);
    }
}