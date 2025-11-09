package com.example.cincuentazo.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SelectPlayersView extends Stage {
    public SelectPlayersView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/cincuentazo/SelectPlayers.fxml")
        );
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        this.setScene(scene);
        this.setTitle("Cincuentazo");
    }

    public static SelectPlayersView getInstance() throws IOException {
        if (SelectPlayersView.SelectPlayersViewHolder.INSTANCE == null) {
            SelectPlayersView.SelectPlayersViewHolder.INSTANCE = new SelectPlayersView();
        }
        return SelectPlayersView.SelectPlayersViewHolder.INSTANCE;
    }

    /**
     * Clase estática interna que implementa el patrón Holder
     * para almacenar la instancia única de {@link SelectPlayersView}
     */
    private static class SelectPlayersViewHolder {
        private static SelectPlayersView INSTANCE = null;
    }
}
