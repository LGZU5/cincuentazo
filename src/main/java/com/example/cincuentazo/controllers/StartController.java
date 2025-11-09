package com.example.cincuentazo.controllers;


import com.example.cincuentazo.views.GameView;
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

    public void onPlay(ActionEvent event) throws IOException {
        SelectPlayersView selectPlayersView = SelectPlayersView.getInstance();
        selectPlayersView.show();
        // Cerrar la ventana de inicio
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    public void onGuia() {
        System.out.println("Guia button clicked");
    }
}
