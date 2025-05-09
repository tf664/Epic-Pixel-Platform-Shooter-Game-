package com.example.epicpixelplatformershootergame.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.epicpixelplatformershootergame.MainActivity;
import com.example.epicpixelplatformershootergame.R;
import com.example.epicpixelplatformershootergame.helper.interfaces.BitmapMethods;

public enum GameCharacters implements BitmapMethods {
    PLAYER(R.drawable.player_spritesheet_walking, 32, 48),
    GRUNTTWO(R.drawable.grunttwo_spritesheet_shooting, 105, 41);

    private int boxWidth;
    private int boxHeight;

    private Bitmap spriteSheet;
    private Bitmap[][] sprites;


    GameCharacters(int resId, int boxWidth, int boxHeight) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;

        options.inScaled = false;

        spriteSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resId, options);

        int columns = Math.max(1, spriteSheet.getWidth() / boxWidth);
        int rows = Math.max(1, spriteSheet.getHeight() / boxHeight);

        System.out.println("Height: " + spriteSheet.getHeight()); // debug
        System.out.println("Width: " + spriteSheet.getWidth()); // debug
        System.out.println("Rows: " + rows); // debug
        System.out.println("Columns: " + columns); // debug

        sprites = new Bitmap[rows][columns];

        for (int j = 0; j < sprites.length; j++) {
            for (int i = 0; i < sprites[j].length; i++) {
                Bitmap frame = Bitmap.createBitmap(spriteSheet, i * boxWidth, j * boxHeight, boxWidth, boxHeight);
                sprites[j][i] = getScaledBitmap(frame);
            }
        }
    }

    public Bitmap getSpriteSheet() {
        return spriteSheet;
    }

    public Bitmap getSprite(int yPos, int xPos) {
        return sprites[yPos][xPos];
    }

}