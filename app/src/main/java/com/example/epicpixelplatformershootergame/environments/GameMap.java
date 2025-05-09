package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class GameMap {

    private int[][] tileIds;

    public GameMap(int [][] tileIds) {
        this.tileIds = tileIds;
    }

    public void draw (Canvas c) {
        for (int i = 0; i < tileIds.length; i++) {
            for (int j = 0; j < tileIds[i].length; j++) {
                c.drawBitmap(Floor.OUTSIDE.getTile(tileIds[i][j]), j * GameConstants.FloorTile.HEIGHT, i * GameConstants.FloorTile.WIDTH, null);
            }
        }
    }
}
