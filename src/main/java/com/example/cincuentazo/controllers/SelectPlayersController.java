package com.example.cincuentazo.controllers;

import com.example.cincuentazo.views.GameView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SelectPlayersController {

    @FXML
    public void onTwoPlayers(ActionEvent event) {
        openGameAndClose(event, 2);
    }

    @FXML
    public void onThreePlayers(ActionEvent event) {
        openGameAndClose(event, 3);
    }

    @FXML
    public void onFourPlayers(ActionEvent event) {
        openGameAndClose(event, 4);
    }

    private void openGameAndClose(ActionEvent event, int playersCount) {
        try {
            GameView gameView = GameView.getInstance();
            gameView.setNumberOfPlayers(playersCount);
            gameView.show();

            Node source = (Node) event.getSource();
            Stage currentStage = (Stage) source.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}