package com.example.cincuentazo.models;

import javafx.application.Platform;

import java.util.Random;

/**
 * Hilo que ejecuta turnos de CPU en bucle.
 *
 * - Duerme un tiempo aleatorio entre 2 y 4 segundos para simular "pensamiento".
 * - Todas las mutaciones sobre engine se hacen synchronised(engine).
 * - Actualizaciones de UI se realizan mediante Platform.runLater en los callbacks.
 */
public class CpuTurnsThread extends Thread {

    private final GameEngine engine;
    private final Runnable uiRefresh;     // actualizar manos y tablero en FX thread
    private final Runnable uiCheckWinner; // comprobar y mostrar ganador en FX thread
    private final Runnable uiCheckHuman;  // comprobar si humano puede jugar en FX thread
    private final Random random = new Random();

    private volatile boolean stop = false;

    public CpuTurnsThread(GameEngine engine,
                          Runnable uiRefresh,
                          Runnable uiCheckWinner,
                          Runnable uiCheckHuman) {
        this.engine = engine;
        this.uiRefresh = uiRefresh;
        this.uiCheckWinner = uiCheckWinner;
        this.uiCheckHuman = uiCheckHuman;
        setDaemon(true);
    }

    public void requestStop() {
        stop = true;
        interrupt();
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                // Si hay ganador, notificar y salir
                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                PlayerModel current;
                synchronized (engine) {
                    current = engine.currentPlayer();
                }

                // Si es humano o null, esperar y continuar (no tomar turnos)
                if (current == null || current.isHuman()) {
                    try { Thread.sleep(100); } catch (InterruptedException ignored) { if (stop) break; }
                    continue;
                }

                // Esperar delay aleatorio 2-4s para simular tiempo de CPU
                double delaySeconds = 2.0 + random.nextDouble() * 2.0;
                long delayMillis = (long) (delaySeconds * 1000);
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException ie) {
                    if (stop) break;
                    Thread.currentThread().interrupt();
                }

                if (stop) break;
                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                // Ejecutar jugada de CPU de forma sincronizada
                synchronized (engine) {
                    current = engine.currentPlayer();
                    if (current == null || current.isHuman() || current.isEliminated()) {
                        continue;
                    }

                    CardModel chosen = engine.cpuChooseCard(current);

                    if (chosen == null) {
                        engine.eliminateIfStuck(current);
                    } else {
                        GameEngine.ApplyResult res = engine.applyResult(chosen);
                        if (!res.ok()) {
                            engine.eliminateIfStuck(current);
                        }
                    }

                    // Avanzar turno (si no hay ganador)
                    if (!engine.hasWinner()) {
                        engine.nextTurn();
                    }
                } // fin synchronized engine

                // Actualizar UI en FX thread
                Platform.runLater(uiRefresh);

                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                // Si ahora es humano, pedir comprobación de su estado
                if (engine.currentPlayer().isHuman()) {
                    Platform.runLater(uiCheckHuman);
                }
                // Siguientes iteraciones del bucle contemplan si siguiente es CPU o humano
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}