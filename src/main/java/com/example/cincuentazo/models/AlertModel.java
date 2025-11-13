package com.example.cincuentazo.models;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utilidad para mostrar alertas en la UI.
 *
 * Los métodos están preparados para usarse desde cualquier parte del código.
 * Las advertencias (warning) se muestran de forma no bloqueante y se programan
 * mediante Platform.runLater para evitar errores si se llaman durante animaciones
 * o durante el procesamiento de layout.
 */
public final class AlertModel {

    private AlertModel() { /* utilitaria */ }

    /**
     * Muestra una alerta de advertencia (no bloqueante).
     *
     * Usamos Platform.runLater + Alert.show() para evitar IllegalStateException
     * que aparece cuando se intenta usar showAndWait() durante animaciones o
     * procesamiento de layout.
     *
     * @param title   título de la alerta
     * @param content texto a mostrar
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
     * Muestra un diálogo de confirmación y devuelve la respuesta del usuario.
     *
     * Este método usa showAndWait() porque típicamente se invoca en respuesta a
     * una acción directa del usuario (p. ej. botón), donde el bloqueo es aceptable.
     * Si lo invocas en contexto de animación, considera usar una versión asíncrona.
     *
     * @param title   título del diálogo
     * @param content texto del diálogo
     * @return true si el usuario confirma (OK), false en otro caso
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