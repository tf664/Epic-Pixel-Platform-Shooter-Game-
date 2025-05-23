package com.example.epicpixelplatformershootergame.environments;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

/**
 * Represents a tile-based game map.
 */
public class GameMap {
    private int[][] tileIds;

    public GameMap(int[][] tileIds) {
        this.tileIds = tileIds;
    }

    /**
     * Returns the tile ID at the specified tile coordinates.
     * @param xIndex X index in the tile array.
     * @param yIndex Y index in the tile array.
     * @return Tile ID at the given coordinates.
     */
    public int getTileId(int xIndex, int yIndex) {
        return tileIds[yIndex][xIndex];
    }

    public int getArrayWidth() {
        return tileIds[0].length;
    }

    public int getArrayHeight() {
        return tileIds.length;
    }

    /**
     * Checks if there is a solid tile at the given world coordinates.
     * @param worldX X position in world coordinates.
     * @param worldY Y position in world coordinates.
     * @return true if a solid tile is present, false otherwise.
     */
    public boolean isSolidTileAt(float worldX, float worldY) {
        // Remove the vertical offset before converting to tile coordinates
        int tileX = (int) (worldX / GameConstants.FloorTile.WIDTH);
        int tileY = (int) (worldY / GameConstants.FloorTile.HEIGHT);

        if (tileX < 0 || tileY < 0 || tileX >= getArrayWidth() || tileY >= getArrayHeight())
            return false;

        // Excludes specific (like air and decoration) tiles from collision
        int tileId = tileIds[tileY][tileX];
        return GameConstants.Map.solidTileIds.contains(tileId);
    }
}
