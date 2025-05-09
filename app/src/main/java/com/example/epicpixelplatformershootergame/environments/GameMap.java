package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;

public class GameMap {

    private int[][] tileIds;

    public GameMap(int [][] tileIds) {
        this.tileIds = tileIds;
    }

    public void draw (Canvas c) {
        for (int i = 0; i < tileIds.length; i++) {
            for (int j = 0; j < tileIds[i].length; j++) {
                c.drawBitmap(Floor.OUTSIDE.getTile(tileIds[i][j]), j * 96, i * 96, null);
            }
        }
    }
}
