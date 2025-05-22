package com.example.epicpixelplatformershootergame.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.epicpixelplatformershootergame.Debug;
import com.example.epicpixelplatformershootergame.MainActivity;
import com.example.epicpixelplatformershootergame.R;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.helper.interfaces.BitmapMethods;

public enum GameEntityAssets implements BitmapMethods {
    PLAYER(R.drawable.player_spritesheet_walking_shooting, GameConstants.Player.FRAME_WIDTH, GameConstants.Player.FRAME_HEIGHT),
    GRUNTTWO(R.drawable.grunttwo_spritesheet_shooting, GameConstants.GruntTwo.FRAME_WIDTH, GameConstants.GruntTwo.FRAME_HEIGHT);

    private int boxWidth, boxHeight;
    private Bitmap spriteSheet;
    private Bitmap[][] sprites;

    GameEntityAssets(int resId, int boxWidth, int boxHeight) {
        options.inScaled = false;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;

        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resId, options);
        int columns = Math.max(1, spriteSheet.getWidth() / boxWidth);
        int rows = Math.max(1, spriteSheet.getHeight() / boxHeight);

        if (Debug.isDebugMode())
            Debug.spriteInfo(spriteSheet, rows, columns);

        sprites = new Bitmap[rows][columns];

        for (int j = 0; j < sprites.length; j++) {
            for (int i = 0; i < sprites[j].length; i++) {
                int frameWidth = this.boxWidth;
                int frameHeight = this.boxHeight;

                Bitmap frame = Bitmap.createBitmap(
                        spriteSheet,
                        i * frameWidth,
                        j * frameHeight,
                        frameWidth,
                        frameHeight);
                sprites[j][i] = getScaledBitmap(frame, getScaleMultiplier());
            }
        }
    }

    private int getScaleMultiplier() {
        switch (this) {
            case PLAYER:
                return GameConstants.Player.SCALE_MULTIPLIER;
            case GRUNTTWO:
                return GameConstants.GruntTwo.SCALE_MULTIPLIER;
        }
        return 0;
    }

    public Bitmap getSprite(int yPos, int xPos) {
        return sprites[yPos][xPos];
    }
}