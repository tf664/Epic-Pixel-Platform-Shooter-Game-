package com.example.epicpixelplatformershootergame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class GameState {
    public enum State {RUNNING, GAME_OVER}

    private State state = State.RUNNING;

    private Bitmap hudSheet;

    // Timer
    private int levelTimeSeconds = 300;
    private long timerStartMillis = System.currentTimeMillis();
    private boolean timerActive = true;
    private Bitmap clockIcon;
    private Typeface pixelFont;

    // Gun HUD
    private int ammoCount = 6; // TODO GameConstants
    private Bitmap gunIcon;
    private Bitmap[] ammoIcons = new Bitmap[ammoCount];

    // Game Over UI
    private Bitmap restartButtonBitmapUnpressed;
    private Bitmap restartButtonBitmapPressed;
    private boolean restartButtonPressed = false;

    public GameState(Bitmap btnUnpressed, Bitmap btnPressed, Typeface font, Context context) {
        this.restartButtonBitmapUnpressed = btnUnpressed;
        this.restartButtonBitmapPressed = btnPressed;
        this.pixelFont = font;

        // Load hud.png and extract the clock icon
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // Prevent automatic scaling
        hudSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.hud, options);
        int columns = 6; // TODO gamestate
        int rows = 2;
        int iconWidth = hudSheet.getWidth() / columns;
        int iconHeight = hudSheet.getHeight() / rows;
        int col = 4; // 0-based, fifth column
        int row = 1; // 0-based, second row
        clockIcon = Bitmap.createBitmap(hudSheet, col * iconWidth, row * iconHeight, iconWidth, iconHeight);
        gunIcon = Bitmap.createBitmap(hudSheet, 0, 0, iconWidth * 2, iconHeight);

        int[] ammoX = {0, 15, 0, 15, 0, 15};
        int[] ammoY = {38, 38, 51, 51, 64, 64};
        int ammoW = 13, ammoH = 12;
        for (int i = 0; i < ammoIcons.length; i++) {
            ammoIcons[i] = Bitmap.createBitmap(hudSheet, ammoX[i], ammoY[i], ammoW, ammoH);
        }
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

    public void drawTimer(Canvas c) {
        int iconSize = 140; // TODO Move scale as needed
        float x = GameConstants.Screen.SCREENWIDTH - 200;
        float y = 150;

        // Draw clock icon
        c.drawBitmap(Bitmap.createScaledBitmap(clockIcon, iconSize, iconSize, false), x - iconSize - 12, y - iconSize + 35, null);

        // Draw timer text
        Paint timerPaint = new Paint();
        timerPaint.setColor(android.graphics.Color.WHITE);
        timerPaint.setTextSize(100);
        timerPaint.setFakeBoldText(true);
        timerPaint.setTypeface(pixelFont);

        c.drawText("" + getTimeLeft(), x, y, timerPaint);
    }

    public void drawGunAndAmmo(Canvas c) {
        float gunX = 40;
        float gunY = 40;
        float scale = 5.0f;

        int gunWidth = (int) (gunIcon.getWidth() * scale);
        int gunHeight = (int) (gunIcon.getHeight() * scale);

        Bitmap scaledGun = Bitmap.createScaledBitmap(gunIcon, gunWidth, gunHeight, false);

        // Draw scaled gun icon
        c.drawBitmap(scaledGun, gunX, gunY, null);

        int iconIdx = Math.max(0, Math.min(ammoCount, ammoIcons.length)) - 1;
        if (iconIdx >= 0 && iconIdx < ammoIcons.length) {
            Bitmap ammoIconToDraw = ammoIcons[iconIdx];
            int ammoWidth = (int) (ammoIconToDraw.getWidth() * scale);
            int ammoHeight = (int) (ammoIconToDraw.getHeight() * scale);

            // Define ammo icon position (adjust as needed)
            float ammoX = gunX + gunWidth - ammoWidth - 35;
            float ammoY = gunY + (gunHeight - ammoHeight) / 2 + 60;

            Bitmap scaledAmmo = Bitmap.createScaledBitmap(ammoIconToDraw, ammoWidth, ammoHeight, false);
            c.drawBitmap(scaledAmmo, ammoX, ammoY, null);
        }
    }

    public void setAmmoCount(int count) {
        ammoCount = Math.max(0, Math.min(count, ammoIcons.length));
    }

    public void decreaseAmmo() {
        ammoCount = Math.max(0, ammoCount - 1);
    }
    public void reloadAmmo() {
        ammoCount = ammoIcons.length; // or your max ammo
    }
    public int getAmmoCount() {
        return ammoCount;
    }

    public void drawGameOverScreen(Canvas c) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(android.graphics.Color.argb(230, 30, 30, 50));
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
}