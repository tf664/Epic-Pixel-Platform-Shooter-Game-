package com.example.epicpixelplatformershootergame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class GameState {
    public enum State {STARTING, RUNNING, GAME_OVER}

    private State state = State.STARTING;

    private Bitmap hudSheet;

    // Timer
    private int levelTimeSeconds = 100; // GameConstants
    private long timerStartMillis = System.currentTimeMillis();
    private boolean timerActive = true;
    private Bitmap clockIcon;
    private Typeface pixelFont;

    // Gun HUD
    private int ammoCount = 6; // hardcoded because of animation
    private Bitmap gunIcon;
    private Bitmap[] ammoIcons = new Bitmap[ammoCount];

    // Menu button UI
    private Bitmap startButtonBitmapUnpressed, startButtonBitmapPressed;
    private Bitmap restartButtonBitmapUnpressed, restartButtonBitmapPressed;
    private Bitmap settingsButtonBitmapUnpressed, settingsButtonBitmapPressed;
    private boolean startButtonPressed = false, restartButtonPressed = false, settingsButtonPressed = false;

    public GameState(Bitmap buttonSheet, Typeface font, Context context) {
        this.pixelFont = font;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        float scaleFactor = GameConstants.MenuButtons.scale;
        int buttonWidth = GameConstants.MenuButtons.btnWidth;
        int buttonHeight = GameConstants.MenuButtons.btnHeight;

        startButtonBitmapUnpressed = Bitmap.createScaledBitmap(
                Bitmap.createBitmap(buttonSheet, 0, 0, buttonWidth, buttonHeight),
                (int) (buttonWidth * scaleFactor),
                (int) (buttonHeight * scaleFactor),
                false
        );

        startButtonBitmapPressed = Bitmap.createScaledBitmap(
                Bitmap.createBitmap(buttonSheet, 0, buttonHeight, buttonWidth, buttonHeight),
                (int) (buttonWidth * scaleFactor),
                (int) (buttonHeight * scaleFactor),
                false
        );

        restartButtonBitmapUnpressed = Bitmap.createScaledBitmap(
                Bitmap.createBitmap(buttonSheet, 0, buttonHeight * 2, buttonWidth, buttonHeight),
                (int) (buttonWidth * scaleFactor),
                (int) (buttonHeight * scaleFactor),
                false
        );

        restartButtonBitmapPressed = Bitmap.createScaledBitmap(
                Bitmap.createBitmap(buttonSheet, 0, buttonHeight * 3, buttonWidth, buttonHeight),
                (int) (buttonWidth * scaleFactor),
                (int) (buttonHeight * scaleFactor),
                false
        );

        settingsButtonBitmapUnpressed = Bitmap.createScaledBitmap(
                Bitmap.createBitmap(buttonSheet, 0, buttonHeight * 4, buttonWidth, buttonHeight),
                (int) (buttonWidth * scaleFactor),
                (int) (buttonHeight * scaleFactor),
                false
        );

        settingsButtonBitmapPressed = Bitmap.createScaledBitmap(
                Bitmap.createBitmap(buttonSheet, 0, buttonHeight * 5, buttonWidth, buttonHeight),
                (int) (buttonWidth * scaleFactor),
                (int) (buttonHeight * scaleFactor),
                false
        );


        // Load hud.png and extract the clock icon
        hudSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.hud, options);
        int columns = 6; // TODO GameConstants
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


    public void drawTimer(Canvas c) {
        int iconSize = 140; // TODO GameConstants
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
        if (iconIdx >= 0) {
            Bitmap ammoIconToDraw = ammoIcons[iconIdx];
            int ammoWidth = (int) (ammoIconToDraw.getWidth() * scale);
            int ammoHeight = (int) (ammoIconToDraw.getHeight() * scale);

            // Define ammo icon position (adjust as needed)
            float ammoX = gunX + gunWidth - ammoWidth - 35;
            float ammoY = gunY + (gunHeight - ammoHeight) / 2.0f + 60;

            Bitmap scaledAmmo = Bitmap.createScaledBitmap(ammoIconToDraw, ammoWidth, ammoHeight, false);
            c.drawBitmap(scaledAmmo, ammoX, ammoY, null);
        }
    }

    public void decreaseAmmo() {
        ammoCount = Math.max(0, ammoCount - 1);
    }

    public void reloadAmmo() {
        ammoCount = ammoIcons.length;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public State getState() {
        return state;
    }

    public void drawStartScreen(Canvas c) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.argb(230, 10, 10, 30));
        c.drawRect(0, 0, GameConstants.Screen.SCREENWIDTH, GameConstants.Screen.SCREENHEIGHT, bgPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(180);
        textPaint.setTypeface(pixelFont);
        textPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText("Menu", GameConstants.Screen.SCREENWIDTH / 2, GameConstants.Screen.SCREENHEIGHT / 2 - 250, textPaint);

        Bitmap startBtn = startButtonPressed ? startButtonBitmapPressed : startButtonBitmapUnpressed;
        Bitmap settingsBtn = settingsButtonPressed ? settingsButtonBitmapPressed : settingsButtonBitmapUnpressed;

        int startBtnWidth = startBtn.getWidth();
        int startBtnX = (GameConstants.Screen.SCREENWIDTH - startBtnWidth) / 2;
        int startBtnY = GameConstants.Screen.SCREENHEIGHT / 2 - 100;

        int settingsBtnWidth = settingsBtn.getWidth();
        int settingsBtnX = (GameConstants.Screen.SCREENWIDTH - settingsBtnWidth) / 2;
        int settingsBtnY = GameConstants.Screen.SCREENHEIGHT / 2 + 50;

        c.drawBitmap(startBtn, startBtnX, startBtnY, null);
        c.drawBitmap(settingsBtn, settingsBtnX, settingsBtnY, null);
    }

    public void setStartButtonPressed(boolean pressed) {
        startButtonPressed = pressed;
    }

    public boolean isStartButtonPressed() {
        return startButtonPressed;
    }

    public Bitmap getStartButtonBitmapUnpressed() {
        return startButtonBitmapUnpressed;
    }

    public void startGame() {
        state = State.RUNNING;
        timerStartMillis = System.currentTimeMillis();
    }

    public void drawGameOverScreen(Canvas c) {
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.argb(230, 30, 30, 50));
        c.drawRect(0, 0, GameConstants.Screen.SCREENWIDTH, GameConstants.Screen.SCREENHEIGHT, bgPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(200);
        textPaint.setTypeface(pixelFont);
        textPaint.setTextAlign(Paint.Align.CENTER);
        c.drawText("Game Over", GameConstants.Screen.SCREENWIDTH / 2, GameConstants.Screen.SCREENHEIGHT / 2 - 100, textPaint);

        int btnX = GameConstants.MenuButtons.restartButtonX;
        int btnY = GameConstants.MenuButtons.restartButtonY;
        Bitmap btnBitmap = restartButtonPressed ? restartButtonBitmapPressed : restartButtonBitmapUnpressed;
        c.drawBitmap(btnBitmap, btnX, btnY, null);
    }

    public void setGameOver() {
        state = State.GAME_OVER;
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

    // TODO settings function not yet implemented
    public Bitmap getSettingsButtonBitmapPressed() {
        return settingsButtonBitmapPressed;
    }

    public Bitmap getSettingsButtonBitmapUnpressed() {
        return settingsButtonBitmapUnpressed;
    }

    public void setSettingsButtonPressed(boolean pressed) {
        settingsButtonPressed = pressed;
    }

    public boolean isSettingsButtonPressed() {
        return settingsButtonPressed;
    }
}
