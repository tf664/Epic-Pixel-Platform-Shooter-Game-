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
        final int TARGET_FPS = 80;
        final long FRAME_TIME = 1000 / TARGET_FPS; // ms per frame

        long lastTime = System.currentTimeMillis();
        int fps = 0;
        long lastFPSCheck = System.currentTimeMillis();

        while (true) {
            long now = System.currentTimeMillis();
            long elapsed = now - lastTime;

            if (elapsed >= FRAME_TIME) {
                double delta = elapsed / 1000.0;
                gamePanel.update(delta);
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
