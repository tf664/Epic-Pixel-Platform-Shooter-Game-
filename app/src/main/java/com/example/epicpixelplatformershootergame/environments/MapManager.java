package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class MapManager {
    private GameMap currentMap;
    private int screenWidth = GameConstants.Screen.screenWidth;
    private int screenHeight = GameConstants.Screen.screenHeight;
    private int mapWidth;
    // Initial position of the camera (it starts in the middle of the screen)
    private int cameraX = 0;  // Camera's horizontal offset

    public MapManager() {
        initMainMap();
    }

    private void initMainMap() {
        currentMap = new GameMap(GameConstants.Map.tileIds);

        mapWidth = currentMap.getArrayWidth() * GameConstants.FloorTile.WIDTH;
    }

    public void draw(Canvas c) {
        int tileWidth = GameConstants.FloorTile.WIDTH;
        int tileHeight = GameConstants.FloorTile.HEIGHT;

        int startTileX = Math.max(0, cameraX / tileWidth);
        int tilesOnScreen = (screenWidth / tileWidth) + 2; // + int loading buffer ahead
        int endTileX = Math.min(currentMap.getArrayWidth(), startTileX + tilesOnScreen);

        for (int i = 0; i < currentMap.getArrayHeight(); i++) {
            for (int j = startTileX; j < endTileX; j++) {
                int tileId = currentMap.getTileId(j, i);

                if (tileId == 0) continue;

                int drawX = (j * tileWidth) - cameraX;
                int mapOffsetY = screenHeight - (currentMap.getArrayHeight() * tileHeight);
                int drawY = i * tileHeight + mapOffsetY;

                c.drawBitmap(Floor.OUTSIDE.getTile(tileId), drawX, drawY, null);
            }
        }
    }

    // Update the camera position based on player position
    public void updateCamera(float playerX) {
        int playerScreenX = (int) playerX - cameraX;

        System.out.println("The playerScreenX: " + playerScreenX);
        System.out.println("playerX " + playerX);
        System.out.println("Threshold: " + GameConstants.Camera.leftThreshold + " " + GameConstants.Camera.rightThreshold);
        System.out.println("Screen dimension: " + GameConstants.Screen.screenHeight + " and " + GameConstants.Screen.screenWidth);

        if (playerScreenX < GameConstants.Camera.leftThreshold) {
            cameraX = (int) playerX - GameConstants.Camera.leftThreshold;
        } else if (playerScreenX > GameConstants.Camera.rightThreshold) {
          cameraX = (int) playerX - GameConstants.Camera.rightThreshold;
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

    public void updateScreenSize(int w, int h) {
        this.screenWidth = w;
        this.screenHeight = h;
        this.mapWidth = currentMap.getArrayWidth() * GameConstants.FloorTile.WIDTH;
    }
}