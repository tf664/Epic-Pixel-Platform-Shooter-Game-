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

    public void draw(Canvas c, int cameraX, int mapOffsetY) {
        c.drawBitmap(GameEntityAssets.HORSE.getSprite(0, 0), x - cameraX, y + mapOffsetY, null);
    }
}