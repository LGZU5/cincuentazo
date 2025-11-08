package com.example.cincuentazo.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartController {

    @FXML
    private Button quitButton;



    @FXML
    private void onQuit() {
        System.exit(0);
    }

        public void onPlay() {
        System.out.println("Play button clicked");
    }


    public void onGuia() {
        System.out.println("Guia button clicked");
    }
}
