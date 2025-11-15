package com.example.cincuentazo.models;

import javafx.application.Platform;

import java.util.Random;

/**
 * A thread that executes CPU turns in a loop.
 *
 * <ul>
 * <li>Sleeps for a random time (2-4 seconds) to simulate "thinking".</li>
 * <li>All mutations on the {@link GameEngine} are thread-safe, using {@code synchronized(engine)}.</li>
 * <li>UI updates are posted to the JavaFX thread using {@link Platform#runLater(Runnable)} via callbacks.</li>
 * </ul>
 */
public class CpuTurnsThread extends Thread {

    private final GameEngine engine;
    private final Runnable uiRefresh;     // Callback to refresh hands and board on the FX thread
    private final Runnable uiCheckWinner; // Callback to check for and display a winner on the FX thread
    private final Runnable uiCheckHuman;  // Callback to check if the human player can play on the FX thread
    private final Random random = new Random();

    // Volatile flag to safely request the thread to stop
    private volatile boolean stop = false;

    /**
     * Constructs a new CPU turn processing thread.
     *
     * @param engine        The shared {@link GameEngine} instance.
     * @param uiRefresh     A {@link Runnable} to be called on the FX thread to refresh the UI.
     * @param uiCheckWinner A {@link Runnable} to be called on the FX thread to check for a winner.
     * @param uiCheckHuman  A {@link Runnable} to be called on the FX thread to check the human player's state.
     */
    public CpuTurnsThread(GameEngine engine,
                          Runnable uiRefresh,
                          Runnable uiCheckWinner,
                          Runnable uiCheckHuman) {
        this.engine = engine;
        this.uiRefresh = uiRefresh;
        this.uiCheckWinner = uiCheckWinner;
        this.uiCheckHuman = uiCheckHuman;
        // Set as daemon so it doesn't prevent the application from exiting
        setDaemon(true);
    }

    /**
     * Signals the thread to terminate its loop and exit.
     * It sets the volatile stop-flag and interrupts the thread
     * to wake it from any sleep states.
     */
    public void requestStop() {
        stop = true;
        interrupt();
    }

    /**
     * The main execution loop for the thread.
     * <p>
     * It continuously checks the game state:
     * <ol>
     * <li>If a winner is found, it notifies the UI and exits.</li>
     * <li>If it's the human's turn, it sleeps briefly and continues the loop.</li>
     * <li>If it's a CPU's turn, it sleeps (simulating thought), then executes the CPU's move
     * in a synchronized block.</li>
     * <li>After a CPU move, it notifies the UI to refresh.</li>
     * <li>If the next player is human, it notifies the UI to check their status.</li>
     * </ol>
     * </p>
     */
    @Override
    public void run() {
        try {
            while (!stop) {
                // If there's a winner, notify and exit
                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                PlayerModel current;
                synchronized (engine) {
                    current = engine.currentPlayer();
                }

                // If it's human's turn or game hasn't started, wait and continue
                if (current == null || current.isHuman()) {
                    try { Thread.sleep(100); } catch (InterruptedException ignored) { if (stop) break; }
                    continue;
                }

                // Wait for a random delay 2-4s to simulate CPU thinking time
                double delaySeconds = 2.0 + random.nextDouble() * 2.0;
                long delayMillis = (long) (delaySeconds * 1000);
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException ie) {
                    if (stop) break;
                    // Re-interrupt the thread if it wasn't a stop reques
                    Thread.currentThread().interrupt();
                }

                if (stop) break;
                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                // Execute the CPU's move in a synchronized block
                synchronized (engine) {
                    // Re-check current player in case state changed
                    current = engine.currentPlayer();
                    if (current == null || current.isHuman() || current.isEliminated()) {
                        continue;
                    }

                    CardModel chosen = engine.cpuChooseCard(current);

                    if (chosen == null) {
                        // CPU has no playable cards
                        engine.eliminateIfStuck(current);
                    } else {
                        GameEngine.ApplyResult res = engine.applyResult(chosen);
                        if (!res.ok()) {
                            // Should not happen if cpuChooseCard is correct, but as a safeguard
                            engine.eliminateIfStuck(current);
                        }
                    }

                    // Advance turn (if no winner)
                    if (!engine.hasWinner()) {
                        engine.nextTurn();
                    }
                } // end synchronized engine

                // Update UI on the FX thread
                Platform.runLater(uiRefresh);

                if (engine.hasWinner()) {
                    Platform.runLater(uiCheckWinner);
                    break;
                }

                // If it's now the human's turn, request a check on their status
                if (engine.currentPlayer().isHuman()) {
                    Platform.runLater(uiCheckHuman);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}