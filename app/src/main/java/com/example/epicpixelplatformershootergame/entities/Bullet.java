package com.example.epicpixelplatformershootergame.entities;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class Bullet {
    public float x, y, velocityX, velocityY;
    public boolean active = true;
    private float distanceTraveled = 0;

    public Bullet(float x, float y, float velocityX, float velocityY) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void update() {
        x += velocityX;
        y += velocityY;
        distanceTraveled += Math.abs(velocityX) + Math.abs(velocityY);
        if (distanceTraveled > GameConstants.Weapon.BULLET_MAX_DISTANCE) {
            active = false;
        }
    }
}
