package com.example.epicpixelplatformershootergame.entities;

public class Enemy {
    public float x, y;
    public final float spawnX;
    public float velocityX = 2f; // TODO GameConstants
    public int direction = 1; // TODO GameConstants

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
    }

    public void update(float leftBound, float rightBound) {
        x += velocityX * direction;
        if (x < leftBound) {
            x = leftBound;
            direction = 1;
        } else if (x > rightBound) {
            x = rightBound;
            direction = -1;
        }
    }
}
