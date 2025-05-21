package com.example.epicpixelplatformershootergame.entities;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

import java.util.List;

public class Enemy {
    public float x, y;
    public final float spawnX;
    public float velocityX = 2f; // TODO GameConstants
    public int direction = 1; // TODO GameConstants
    private int health = 3;
    private long lastShootTime = 0;
    private static final long SHOOT_COOLDOWN_MS = 800; // 1.5 seconds

    public float patrolLeft = 800, patrolRight = 1800; // TODO GameConstants
    // Animation
    public int animFrame = 0;
    public int animTick = 0;

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.patrolLeft = x - 200;  // Example: patrol 200px left/right
        this.patrolRight = x + 200;
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

    public void updatePatrol() {
        x += velocityX * direction;
        if (x < patrolLeft) {
            x = patrolLeft;
            direction = 1;
        } else if (x > patrolRight) {
            x = patrolRight;
            direction = -1;
        }
    }

    public void updateAnimation() {
        animTick++;
        if (animTick >= 10) { // Adjust speed as needed
            animTick = 0;
            animFrame = (animFrame + 1) % 59; // 59 frames TODO
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void tryShoot(List<Bullet> enemyBullets, float targetX, float targetY) {
        long now = System.currentTimeMillis();

        float gunOffsetX = direction > 0 ? 80 : 10; // match bullet spawn
        float gunOffsetY = 80;

        float gunX = x + gunOffsetX;
        float gunY = y + gunOffsetY;

        float playerOffsetXFromGun = targetX - gunX;
        float playerOffsetYFromGun = Math.abs(targetY - gunY);

        boolean facingPlayer = (playerOffsetXFromGun > 0 && direction == 1) ||
                (playerOffsetXFromGun < 0 && direction == -1);
        if (Math.abs(playerOffsetXFromGun) < 2500 && playerOffsetYFromGun < 40 && facingPlayer) {
            if (now - lastShootTime >= SHOOT_COOLDOWN_MS) {
                float bulletSpeed = 12f;
                float vx = bulletSpeed * direction; // Use direction for left/right
                float vy = 0;

                enemyBullets.add(new Bullet(gunX, gunY, vx, vy));
                lastShootTime = now;
            }
        }
    }
}
