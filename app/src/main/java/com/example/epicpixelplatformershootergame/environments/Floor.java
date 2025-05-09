package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.epicpixelplatformershootergame.MainActivity;
import com.example.epicpixelplatformershootergame.R;
import com.example.epicpixelplatformershootergame.helper.interfaces.BitmapMethods;

public enum Floor implements BitmapMethods {

    OUTSIDE(R.drawable.tilesetfloorsand, 3, 1);

    private Bitmap[] tiles;


    Floor(int resId, int tilesInWidth, int tilesInHeight) {
        options.inScaled = false;
        tiles = new Bitmap[tilesInHeight * tilesInWidth];
        Bitmap tileSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resId, options);

        // i: Row, j: Column
        for (int i = 0; i < tilesInHeight; i++) {
            for (int j = 0; j < tilesInWidth; j++) {
                int index = i * tilesInWidth + j;
                tiles[index] = getScaledBitmap(Bitmap.createBitmap(tileSheet, 32 * j, 32 * i, 32, 32));
            }
        }
    }

    public Bitmap getTile(int id) {
        return tiles[id];
    }
}
