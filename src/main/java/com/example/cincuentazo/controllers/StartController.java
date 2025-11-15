package com.example.cincuentazo.controllers;

import com.example.cincuentazo.views.InstructionsView;
import com.example.cincuentazo.views.SelectPlayersView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {



    @FXML
    private void onQuit() {
        System.exit(0);
    }

    /**
     * Changes the window to SelectPlayersView
     * @param event
     * @throws IOException
     */
    public void onPlay(ActionEvent event) throws IOException {
        SelectPlayersView selectPlayersView = SelectPlayersView.getInstance();
        selectPlayersView.show();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    /**
     * Changes the window to InstructionsView
     * @param event
     * @throws IOException
     */
    public void onGuia(ActionEvent event) {
        try {
            InstructionsView instructionsView = InstructionsView.getInstance();
            instructionsView.show();

            // Cerrar la ventana de inicio
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
