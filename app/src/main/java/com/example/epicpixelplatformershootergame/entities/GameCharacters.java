package com.example.epicpixelplatformershootergame.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.epicpixelplatformershootergame.Debug;
import com.example.epicpixelplatformershootergame.MainActivity;
import com.example.epicpixelplatformershootergame.R;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.helper.interfaces.BitmapMethods;

public enum GameCharacters implements BitmapMethods {
    PLAYER(R.drawable.player_spritesheet_walking, 32, 48),
    GRUNTTWO(R.drawable.grunttwo_spritesheet_shooting, 105, 41);

    private int boxWidth, boxHeight;
    private Bitmap spriteSheet;
    private Bitmap[][] sprites;

    GameCharacters(int resId, int boxWidth, int boxHeight) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;

        options.inScaled = false;

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

    public Bitmap getSpriteSheet() {
        return spriteSheet;
    }

    private int getScaleMultiplier() {
        switch (this) {
            case PLAYER:
                return GameConstants.Player.SCALE_MULTIPLIER;
            case GRUNTTWO:
                return GameConstants.GruntTwo.SCALE_MULTIPLIER;
        }
        ;

        return 0;
    }

    public Bitmap getSprite(int yPos, int xPos) {
        return sprites[yPos][xPos];
    }

    public Rect getCollisionRect(int x, int y) {
        if (this == PLAYER) {
            int offsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
            int offsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
            return new Rect(
                    x + offsetX,
                    y + offsetY,
                    x + offsetX + GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER,
                    y + offsetY + GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER
            );
        }
        return null;
    }

}