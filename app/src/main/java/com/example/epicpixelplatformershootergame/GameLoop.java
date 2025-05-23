package com.example.epicpixelplatformershootergame;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

/**
 * Handles the main game loop, updating and rendering the game at a fixed frame rate.
 */
public class GameLoop implements Runnable {
    private Thread gameThread;
    private GamePanel gamePanel;


    /**
     * Constructs a new GameLoop for the given GamePanel.
     * @param gamePanel The main game panel to update and render.
     */
    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    /**
     * Runs the main game loop, updating and rendering at the target FPS.
     */
    @Override
    public void run() {
        final int TARGET_FPS = 60;
        final long FRAME_TIME = 1000 / TARGET_FPS; // ms per frame

        long lastTime = System.currentTimeMillis();
        int fps = 0;
        long lastFPSCheck = System.currentTimeMillis();

        while (true) {
            long now = System.currentTimeMillis();
            long elapsed = now - lastTime;

            if (elapsed >= FRAME_TIME) {
                gamePanel.update();
                gamePanel.render();
                lastTime = now;
                fps++;
            } else {
                try {
                    Thread.sleep(FRAME_TIME - elapsed);
                } catch (InterruptedException e) {
                    break;
                }
            }

            if (GameConstants.DebugMode.DEBUG_MODE) {
                Debug.debugFPS(fps, now, lastFPSCheck);

            }
        }
    }

    public void startGameLoop() {
        gameThread.start();
    }
}
