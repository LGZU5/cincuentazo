package com.example.cincuentazo.views;

import com.example.cincuentazo.controllers.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GameView extends Stage {
    private GameController controller;

    public GameView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/cincuentazo/Table.fxml")
        );
        Parent root = fxmlLoader.load();
        this.controller = fxmlLoader.getController();
        Scene scene = new Scene(root);

        this.getIcons().add(new Image(
                getClass().getResourceAsStream("/com/example/cincuentazo/assets/images/icons/favicon.png")
        ));

        scene.getStylesheets().add(
                getClass().getResource("/com/example/cincuentazo/styles/styles.css").toExternalForm()
        );

        this.setScene(scene);
        this.setTitle("Cincuentazo");
    }

    public static GameView getInstance() throws IOException {
        if (GameView.GameViewHolder.INSTANCE == null) {
            GameView.GameViewHolder.INSTANCE = new GameView();
        }
        return GameView.GameViewHolder.INSTANCE;
    }

    public void setNumberOfPlayers(int players) {
        if (controller != null) {
            controller.setNumberOfPlayers(players);
        }
    }

    private static class GameViewHolder {
        private static GameView INSTANCE = null;
    }
}