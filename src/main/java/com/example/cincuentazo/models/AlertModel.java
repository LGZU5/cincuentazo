package com.example.cincuentazo.models;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * A utility class for displaying alerts in the UI.
 * <p>
 * These methods are designed to be used safely from any thread.
 * Warning alerts are non-blocking and scheduled using {@link Platform#runLater(Runnable)}
 * to prevent errors if called during animations or layout processing.
 * </p>
 */
public final class AlertModel {

    private AlertModel() { /* utilitaria */ }

    /**
     * Displays a non-blocking warning alert.
     * <p>
     * This method uses {@code Platform.runLater} + {@code Alert.show()} to avoid
     * the {@code IllegalStateException} that can occur when using {@code showAndWait()}
     * during animations or layout processing.
     * </p>
     *
     * @param title   The title of the alert window.
     * @param content The text content to display.
     */
    public static void warning(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.show();
        });
    }

    /**
     * Displays a blocking confirmation dialog and returns the user's response.
     * <p>
     * This method uses {@code showAndWait()} because it is typically invoked
     * in response to a direct user action (e.g., a button click), where
     * blocking the UI thread is acceptable.
     * </p>
     *
     * @param title   The title of the dialog window.
     * @param content The question or text content of the dialog.
     * @return {@code true} if the user confirmed (pressed OK), {@code false} otherwise.
     */
    public static boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Optional<ButtonType> res = alert.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }
}