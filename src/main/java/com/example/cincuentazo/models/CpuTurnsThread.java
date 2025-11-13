package com.example.cincuentazo.models;

import javafx.application.Platform;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hilo que ejecuta turnos de CPU cuando el Timer indica que está listo (cpuReady=true).
 * No calcula tiempos; solo espera la señal del temporizador, juega, refresca UI y pasa turno.
 */
public class CpuTurnsThread extends Thread {

    private final GameEngine engine;
    private final AtomicBoolean cpuReadySignal;

    // Callbacks UI (se invocan en FX Thread)
    private final Runnable uiRefresh;
    private final Runnable uiCheckWinner;
    private final Runnable uiCheckHuman;

    private volatile boolean stop = false;

    public CpuTurnsThread(GameEngine engine,
                          AtomicBoolean cpuReadySignal,
                          Runnable uiRefresh,
                          Runnable uiCheckWinner,
                          Runnable uiCheckHuman) {
        this.engine = engine;
        this.cpuReadySignal = cpuReadySignal;
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
            while (!stop && !engine.hasWinner() && !engine.currentPlayer().isHuman()) {

                // Esperar a que el timer conceda la jugada
                while (!stop && !engine.hasWinner() && !engine.currentPlayer().isHuman() && !cpuReadySignal.get()) {
                    Thread.sleep(50);
                }
                if (stop || engine.hasWinner() || engine.currentPlayer().isHuman()) break;

                // Consumir la señal
                cpuReadySignal.set(false);

                // Elegir y aplicar jugada
                PlayerModel cpu = engine.currentPlayer();
                CardModel chosen = engine.cpuChooseCard(cpu);

                if (chosen == null) {
                    engine.eliminateIfStuck(cpu);
                } else {
                    GameEngine.ApplyResult res = engine.applyResult(chosen);
                    if (!res.ok()) engine.eliminateIfStuck(cpu);
                }

                // Refrescar UI
                Platform.runLater(uiRefresh);

                // ¿Ganador?
                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                // Pasar turno
                engine.nextTurn();

                // Si ahora es humano, notificar
                if (engine.currentPlayer().isHuman()) {
                    Platform.runLater(uiCheckHuman);
                    break;
                }
                // Si sigue CPU, esperará la próxima señal del Timer
            }
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
