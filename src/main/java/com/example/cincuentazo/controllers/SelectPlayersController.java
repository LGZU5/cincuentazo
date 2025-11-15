package com.example.cincuentazo.controllers;

import com.example.cincuentazo.views.GameView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SelectPlayersController {

    /**
     * FXML event handler triggered when the user selects the '2 Players' option.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    public void onTwoPlayers(ActionEvent event) {
        openGameAndClose(event, 2);
    }

    /**
     * FXML event handler triggered when the user selects the '3 Players' option.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    public void onThreePlayers(ActionEvent event) {
        openGameAndClose(event, 3);
    }

    /**
     * FXML event handler triggered when the user selects the '4 Players' option.
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    public void onFourPlayers(ActionEvent event) {
        openGameAndClose(event, 4);
    }

    /**
     * Initializes the GameView with the selected number of players,
     * displays the game window, and closes the current player selection stage.
     * @param event
     * @param playersCount
     */
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