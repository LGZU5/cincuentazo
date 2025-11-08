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
}
