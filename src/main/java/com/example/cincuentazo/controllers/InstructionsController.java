package com.example.cincuentazo.controllers;

import com.example.cincuentazo.views.StartView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class InstructionsController {

    @FXML
    private void onBack(javafx.event.ActionEvent e) {
        try {
            // Open menu view (StartView)
            StartView startView = StartView.getInstance();
            startView.show();

            // Close this window
            Stage current = (Stage) ((Node) e.getSource()).getScene().getWindow();
            current.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
