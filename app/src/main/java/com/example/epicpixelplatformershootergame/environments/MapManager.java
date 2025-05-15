package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class MapManager {
    private GameMap currentMap;
    private int screenWidth;
    private int screenHeight;
    private int mapWidth;
    // Initial position of the camera (it starts in the middle of the screen)
    private int cameraX = 0;  // Camera's horizontal offset

    public MapManager() {
        initTestMap();
    }

    private void initTestMap() {
        int[][] tileIds = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        currentMap = new GameMap(tileIds);

        mapWidth = currentMap.getArrayWidth() * GameConstants.FloorTile.WIDTH;
    }

    public void draw(Canvas c) {
        int tileWidth = GameConstants.FloorTile.WIDTH;
        int tileHeight = GameConstants.FloorTile.HEIGHT;

        int startTileX = Math.max(0, cameraX / tileWidth);
        int endTileX = Math.min(currentMap.getArrayWidth(), startTileX + (screenWidth / tileWidth) + 8);

        int totalMapHeight = currentMap.getArrayHeight() * tileHeight;
        int verticalOffset = (screenHeight - totalMapHeight) / 2;

        for (int i = 0; i < currentMap.getArrayHeight(); i++) {
            for (int j = startTileX; j < endTileX; j++) {
                int tileId = currentMap.getTileId(j, i);
                if (tileId == 0)
                    continue;

                int drawX = (j * tileWidth) - cameraX;
                int mapOffsetY = screenHeight - (currentMap.getArrayHeight() * tileHeight);
                int drawY = i * tileHeight + mapOffsetY;

                c.drawBitmap(Floor.OUTSIDE.getTile(tileId), drawX, drawY, null);
            }
        }
    }

    // Update the camera position based on player position
    public void updateCamera(float playerX) {
        int leftThreshold = screenWidth / 3;
        int rightThreshold = screenWidth * 2 / 3;

        int playerScreenX = (int) playerX - cameraX;

        if (playerScreenX < leftThreshold) {
            cameraX = (int) playerX - leftThreshold;
        } else if (playerScreenX > rightThreshold) {
            cameraX = (int) playerX - rightThreshold;
        }

        // Clamp cameraX within map bounds
        int maxCameraX = Math.max(0, mapWidth - screenWidth);
        if (cameraX > maxCameraX) {
            cameraX = maxCameraX;
        } else if (cameraX < 0) {
            cameraX = 0;
        }
    }

    public int getCameraX() {
        return cameraX;
    }

    public GameMap getCurrentMap() {
        return currentMap;
    }

    public int getMapOffsetY() {
        return screenHeight - (currentMap.getArrayHeight() * GameConstants.FloorTile.HEIGHT);
    }

    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

}