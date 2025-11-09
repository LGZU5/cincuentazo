package com.example.cincuentazo.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StartView extends Stage {
    public StartView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/cincuentazo/StartMenu.fxml")
        );
        Parent root = fxmlLoader.load();
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

    public static StartView getInstance() throws IOException {
        if (StartView.StartViewHolder.INSTANCE == null) {
            StartView.StartViewHolder.INSTANCE = new StartView();
        }
        return StartView.StartViewHolder.INSTANCE;
    }

    /**
     * Clase estática interna que implementa el patrón Holder
          * para almacenar la instancia única de {@link StartView}
     */
    private static class StartViewHolder {
        private static StartView INSTANCE = null;
    }
}
