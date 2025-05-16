package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.epicpixelplatformershootergame.MainActivity;
import com.example.epicpixelplatformershootergame.R;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.helper.interfaces.BitmapMethods;

public enum Floor implements BitmapMethods {
    OUTSIDE(R.drawable.tilesetfloorsand, 7, 6);
    private Bitmap[] tiles;

    Floor(int resId, int tilesInWidth, int tilesInHeight) {
        options.inScaled = false;
        tiles = new Bitmap[tilesInHeight * tilesInWidth];
        Bitmap tileSheet = BitmapFactory.decodeResource(MainActivity.getGameContext().getResources(), resId, options);

        int baseWidth = GameConstants.FloorTile.BASE_WIDTH;
        int baseHeight = GameConstants.FloorTile.BASE_HEIGHT;

        for (int i = 0; i < tilesInHeight; i++) {
            for (int j = 0; j < tilesInWidth; j++) {
                int index = i * tilesInWidth + j;

                // Slice from raw tileSheet
                Bitmap tile = Bitmap.createBitmap(tileSheet, j * baseWidth, i * baseHeight, baseWidth, baseHeight);
                // Scale
                tiles[index] = getScaledBitmap(tile, GameConstants.FloorTile.SCALE_MULTIPLIER);
            }
        }
    }


    public Bitmap getTile(int id) {
        if (id < 0 || id >= tiles.length)
            return tiles[0]; // fallback tile

        return tiles[id];
    }
}
