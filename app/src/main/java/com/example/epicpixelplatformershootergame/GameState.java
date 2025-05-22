package com.example.epicpixelplatformershootergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class GameState {
    public enum State {RUNNING, GAME_OVER}

    private State state = State.RUNNING;

    // Timer
    private int levelTimeSeconds = 300;
    private long timerStartMillis = System.currentTimeMillis();
    private boolean timerActive = true;

    // Game Over UI
    private Bitmap restartButtonBitmapUnpressed;
    private Bitmap restartButtonBitmapPressed;
    private boolean restartButtonPressed = false;
    private Typeface pixelFont;

    public GameState(Bitmap btnUnpressed, Bitmap btnPressed, Typeface font) {
        this.restartButtonBitmapUnpressed = btnUnpressed;
        this.restartButtonBitmapPressed = btnPressed;
        this.pixelFont = font;
    }

    public void reset() {
        timerStartMillis = System.currentTimeMillis();
        timerActive = true;
        state = State.RUNNING;
        restartButtonPressed = false;
    }

    public void updateTimer() {
        if (timerActive) {
            long elapsed = (System.currentTimeMillis() - timerStartMillis) / 1000;
            int timeLeft = Math.max(0, levelTimeSeconds - (int) elapsed);
            if (timeLeft == 0) {
                timerActive = false;
                state = State.GAME_OVER;
            }
        }
    }

    public int getTimeLeft() {
        long elapsed = (System.currentTimeMillis() - timerStartMillis) / 1000;
        return Math.max(0, levelTimeSeconds - (int) elapsed);
    }

    public void setGameOver() {
        state = State.GAME_OVER;
    }

    public State getState() {
        return state;
    }

    public void setRestartButtonPressed(boolean pressed) {
        restartButtonPressed = pressed;
    }

    public boolean isRestartButtonPressed() {
        return restartButtonPressed;
    }

    public Bitmap getRestartButtonBitmapUnpressed() {
        return restartButtonBitmapUnpressed;
    }

    public Bitmap getRestartButtonBitmapPressed() {
        return restartButtonBitmapPressed;
    }

    public void drawGameOverScreen(Canvas c) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(android.graphics.Color.argb(220, 30, 30, 50));
        c.drawRect(0, 0, GameConstants.Screen.SCREENWIDTH, GameConstants.Screen.SCREENHEIGHT, bgPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(android.graphics.Color.WHITE);
        textPaint.setTextSize(200);
        textPaint.setTypeface(pixelFont);
        textPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText("Game Over", GameConstants.Screen.SCREENWIDTH / 2, GameConstants.Screen.SCREENHEIGHT / 2 - 100, textPaint);

        int btnWidth = restartButtonBitmapUnpressed.getWidth();
        int btnHeight = restartButtonBitmapUnpressed.getHeight();
        int btnX = GameConstants.Screen.SCREENWIDTH / 2 - btnWidth / 2;
        int btnY = (GameConstants.Screen.SCREENHEIGHT / 2 + 50) - 500;
        Bitmap btnBitmap = restartButtonPressed ? restartButtonBitmapPressed : restartButtonBitmapUnpressed;
        c.drawBitmap(btnBitmap, btnX, btnY, null);
    }

    public void drawTimer(Canvas c) {
        Paint timerPaint = new Paint();
        timerPaint.setColor(android.graphics.Color.WHITE);
        timerPaint.setTextSize(100);
        timerPaint.setFakeBoldText(true);
        timerPaint.setTypeface(pixelFont);

        float x = GameConstants.Screen.SCREENWIDTH - 300;
        float y = 150;

        c.drawText("O: " + getTimeLeft(), x, y, timerPaint);
    }
}