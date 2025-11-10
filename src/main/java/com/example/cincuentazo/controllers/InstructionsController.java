package com.example.cincuentazo.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

/** Controlador de la vista de instrucciones. */
public class InstructionsController {

    /** Cierra la ventana actual o vuelve a la pantalla anterior. */
    @FXML
    private void onBack(javafx.event.ActionEvent e) {
        Object src = e.getSource();
        if (src instanceof Node) {
            Stage stage = (Stage) ((Node) src).getScene().getWindow();
            stage.close();
        }
    }
}
