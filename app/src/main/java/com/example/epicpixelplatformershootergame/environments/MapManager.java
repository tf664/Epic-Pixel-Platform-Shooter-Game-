package com.example.epicpixelplatformershootergame.environments;

import android.graphics.Canvas;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class MapManager {
    private GameMap currentMap;
    private int screenWidth = GameConstants.Screen.SCREENWIDTH;
    private int screenHeight = GameConstants.Screen.SCREENHEIGHT;
    private int mapWidth;
    private int cameraX = 0;  // Initial position of the camera

    public MapManager() {
        initMainMap();
    }

    public void draw(Canvas c) {
        int tileWidth = GameConstants.FloorTile.WIDTH;
        int tileHeight = GameConstants.FloorTile.HEIGHT;
        int startTileX = Math.max(0, cameraX / tileWidth);
        int tilesOnScreen = (screenWidth / tileWidth) + 2;
        int endTileX = Math.min(currentMap.getArrayWidth(), startTileX + tilesOnScreen);

        for (int i = 0; i < currentMap.getArrayHeight(); i++) {
            for (int j = startTileX; j < endTileX; j++) {
                int tileId = currentMap.getTileId(j, i);
                int drawX = (j * tileWidth) - cameraX;
                int mapOffsetY = screenHeight - (currentMap.getArrayHeight() * tileHeight);
                int drawY = i * tileHeight + mapOffsetY;
                drawTile(c, tileId, drawX, drawY);
            }
        }
    }

    // Update the camera position based on player position
    public void updateCamera(float playerX) {
        int playerScreenX = (int) playerX - cameraX;
        if (playerScreenX < GameConstants.Camera.leftThreshold) {
            cameraX = (int) playerX - GameConstants.Camera.leftThreshold;
        } else if (playerScreenX > GameConstants.Camera.rightThreshold) {
            cameraX = (int) playerX - GameConstants.Camera.rightThreshold;
        }
        clampCameraX();
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


    private void initMainMap() {
        currentMap = new GameMap(GameConstants.Map.tileIds);
        mapWidth = currentMap.getArrayWidth() * GameConstants.FloorTile.WIDTH;
    }

    private void drawTile(Canvas c, int tileId, int drawX, int drawY) {
        if (tileId == 0) return;
        c.drawBitmap(Floor.OUTSIDE.getTile(tileId), drawX, drawY, null);
    }

    private void clampCameraX() {
        int maxCameraX = Math.max(0, mapWidth - screenWidth);
        if (cameraX > maxCameraX) {
            cameraX = maxCameraX;
        } else if (cameraX < 0) {
            cameraX = 0;
        }
    }
}