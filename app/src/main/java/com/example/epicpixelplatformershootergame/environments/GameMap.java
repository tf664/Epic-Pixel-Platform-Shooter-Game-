package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class GameMap {

    private int[][] tileIds;
    MapManager mapManager;

    public GameMap(int[][] tileIds) {
        this.tileIds = tileIds;
    }

    public int getTileId(int xIndex, int yIndex) {
        return tileIds[yIndex][xIndex];
    }

    public int getArrayWidth() {
        return tileIds[0].length;
    }

    public int getArrayHeight() {
        return tileIds.length;
    }

    public boolean isSolidTileAt(float worldX, float worldY, int mapOffsetY) {
        // Remove the vertical offset before converting to tile coordinates
        float adjustedY = worldY - mapOffsetY;

        int tileX = (int) (worldX / GameConstants.FloorTile.WIDTH);
        int tileY = (int) (adjustedY / GameConstants.FloorTile.HEIGHT);

        if (tileX < 0 || tileY < 0 || tileX >= getArrayWidth() || tileY >= getArrayHeight())
            return false;

        return tileIds[tileY][tileX] != 0;
    }
}
