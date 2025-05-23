package com.example.epicpixelplatformershootergame.entities;

import android.graphics.Canvas;

import com.example.epicpixelplatformershootergame.entities.GameEntityAssets;
import com.example.epicpixelplatformershootergame.environments.MapManager;

public class Horse {
    public float x, y;

    public Horse(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Draws the horse sprite on the canvas.
     *
     * @param c          Canvas to draw on.
     * @param cameraX    Camera X offset.
     * @param mapOffsetY Map Y offset.
     */
    public void draw(Canvas c, int cameraX, int mapOffsetY) {
        c.drawBitmap(GameEntityAssets.HORSE.getSprite(0, 0), x - cameraX, y + mapOffsetY, null);
    }
}