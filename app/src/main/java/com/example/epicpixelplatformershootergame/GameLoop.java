package com.example.epicpixelplatformershootergame;

public class GameLoop implements Runnable {
    private Thread gameThread;
    private GamePanel gamePanel;


    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        gameThread = new Thread(this);
    }

    @Override
    public void run() {
        long lastFPSCheck = System.currentTimeMillis();
        int fps = 0;

        long lastDelta = System.nanoTime();
        long nanoSec = 1_000_000_000;

        while (true) {
            long nowDelta = System.nanoTime();
            double timeSinceLastDelta = nowDelta - lastDelta;
            double delta = timeSinceLastDelta / nanoSec;

            gamePanel.update(delta);
            gamePanel.render();
            lastDelta = nowDelta;
            fps++;

            long now = System.currentTimeMillis();
            if (now - lastFPSCheck >= 1000) {
                System.out.println("FPS: " + fps);
                fps = 0;
                lastFPSCheck += 1000;
            }
        }
    }

    public void startGameLoop() {
        gameThread.start();
    }
}
