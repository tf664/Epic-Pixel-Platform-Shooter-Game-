package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class GameMap {

    private int[][] tileIds;

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

    public boolean isSolidTileAt(float worldX, float worldY) {
        int tileX = (int) (worldX / GameConstants.FloorTile.WIDTH);
        int tileY = (int) (worldY / GameConstants.FloorTile.HEIGHT);

        // Prevent out-of-bounds crashes
        if (tileX < 0 || tileY < 0 || tileX >= getArrayWidth() || tileY >= getArrayHeight()) {
            return false;
        }

        // Check for a solid tile (non-zero tileId means solid)
        int tileId = tileIds[tileY][tileX];
        return tileId != 0;
    }
}
