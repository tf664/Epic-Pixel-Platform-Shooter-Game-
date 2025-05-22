package com.example.epicpixelplatformershootergame.entities;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

import java.util.List;
import java.util.Random;

public class Enemy {
    // Position & Movement
    public float x, y;
    public final float spawnX;
    public float velocityX = 2f; // TODO GameConstants
    public int direction = 1; // TODO GameConstants

    // Patrol Behavior
    public float patrolLeft, patrolRight;
    private final Random rand = new Random();

    // Health
    private int health = 3; // TODO GameConstants

    // Shooting
    private long lastShootTime = 0;
    private static final long SHOOT_COOLDOWN_MS = 800; // TODO GameConstants
    private int shotsFired = 0;
    private static final int MAX_SHOTS = 2;
    private boolean shouldShoot = false;
    private List<Bullet> pendingBullets;
    private float pendingGunX, pendingGunY, pendingVx, pendingVy;
    private static final long RELOAD_TIME_MS = 3000; // TODO adjust as needed
    private boolean isReloading = false;
    private long reloadStartTime = 0;

    // Animation
    private enum AnimState {IDLE, SHOOTING1, RELOADING, SHOOTING2, RETURN_IDLE}
    private AnimState animState = AnimState.IDLE;
    public int animFrame = 0;
    private int animFrameIdx = 0;
    private int animTick = 0;
    private static final int ANIM_SPEED = 10;
    // Animation frames
    private static final int[] IDLE_FRAMES = {0};
    private static final int[] SHOOTING1_FRAMES = {1, 2, 3, 4, 5};
    private static final int[] RELOADING_FRAMES = {6, 7, 8, 9, 10, 11, 12};
    private static final int[] SHOOTING2_FRAMES = {13, 14, 15, 16, 17};
    private static final int[] RETURN_IDLE_FRAMES = {18, 19, 20, 21};

    public Enemy(float x, float y) {
        this.x = x;
        this.y = y;
        this.spawnX = x;
        this.patrolLeft = x - 200;  // TODO GameConstants patrol 200px left/right
        this.patrolRight = x + 200;  // TODO GameConstants
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

        updateReload();
        updateAnimation();
    }

    public void updatePatrol(Player player) {
        float playerX = player.playerX;
        float playerY = player.playerY;
        if (isPlayerInSight(playerX, playerY)) {
            // Pursue player within patrol bounds
            if (playerX < x && x > patrolLeft) {
                direction = -1;
                x += velocityX * direction;
            } else if (playerX > x && x < patrolRight) {
                direction = 1;
                x += velocityX * direction;
            }
        } else {
            // Regular patrol logic
            x += velocityX * direction;
            if (x < patrolLeft) {
                x = patrolLeft;
                direction = 1;
            } else if (x > patrolRight) {
                x = patrolRight;
                direction = -1;
            }
        }

        updateReload();
        updateAnimation();
    }

    private boolean isPlayerInSight(float playerX, float playerY) {
        float gunOffsetX = direction > 0 ? 80 : 10;
        float gunOffsetY = 80;
        float gunX = x + gunOffsetX;
        float gunY = y + gunOffsetY;

        float dx = playerX - gunX;
        float dy = Math.abs(playerY - gunY);

        boolean facingPlayer = (dx > 0 && direction == 1) || (dx < 0 && direction == -1);
        return Math.abs(dx) < GameConstants.Enemy.SHOOT_RANGE &&
                dy < GameConstants.Enemy.VERTICAL_TOLERANCE &&
                facingPlayer;
    }

    public void updateReload() {
        if (isReloading) {
            long now = System.currentTimeMillis();
            if (now - reloadStartTime >= RELOAD_TIME_MS) {
                isReloading = false;
                shotsFired = 0;
                // Reset the animation state to IDLE after reloading
            }
        }
    }

    public void updateAnimation() {
        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            switch (animState) {
                case IDLE:
                    animFrame = IDLE_FRAMES[0];
                    break;
                case SHOOTING1:
                    animFrame = SHOOTING1_FRAMES[animFrameIdx];
                    if (animFrameIdx == 2 && shouldShoot) { // frame 3 (index 2)
                        spawnBullet();
                        shouldShoot = false;
                    }
                    animFrameIdx++;
                    if (animFrameIdx >= SHOOTING1_FRAMES.length) {
                        if (shotsFired >= MAX_SHOTS) {
                            animState = AnimState.RELOADING;
                        } else {
                            animState = AnimState.RETURN_IDLE;
                        }
                        animFrameIdx = 0;
                    }
                    break;
                case SHOOTING2:
                    animFrame = SHOOTING2_FRAMES[animFrameIdx];
                    if (animFrameIdx == 1 && shouldShoot) { // frame 14 (index 1)
                        spawnBullet();
                        shouldShoot = false;
                    }
                    animFrameIdx++;
                    if (animFrameIdx >= SHOOTING2_FRAMES.length) {
                        if (shotsFired >= MAX_SHOTS) {
                            animState = AnimState.RELOADING;
                        } else {
                            animState = AnimState.RETURN_IDLE;
                        }
                        animFrameIdx = 0;
                    }
                    break;
                case RELOADING:
                    animFrame = RELOADING_FRAMES[animFrameIdx++];
                    if (animFrameIdx == 1 && !isReloading) {
                        reloadStartTime = System.currentTimeMillis(); // Start reloading timer once
                        isReloading = true;
                    }
                    if (animFrameIdx >= RELOADING_FRAMES.length) {
                        animState = AnimState.RETURN_IDLE;
                        animFrameIdx = 0;
                    }
                    break;
                case RETURN_IDLE:
                    animFrame = RETURN_IDLE_FRAMES[animFrameIdx++];
                    if (animFrameIdx >= RETURN_IDLE_FRAMES.length) {
                        animState = AnimState.IDLE;
                        animFrameIdx = 0;
                    }
                    break;
            }
        }
    }

    public void startShooting(List<Bullet> enemyBullets, float gunX, float gunY, float vx, float vy, boolean isSecond) {
        if (animState == AnimState.IDLE) {
            animState = isSecond ? AnimState.SHOOTING2 : AnimState.SHOOTING1;
            animFrameIdx = 0;
            shouldShoot = true;
            pendingBullets = enemyBullets;
            pendingGunX = gunX;
            pendingGunY = gunY;
            pendingVx = vx;
            pendingVy = vy;
        }
    }

    private void spawnBullet() {
        if (pendingBullets != null) {
            pendingBullets.add(new Bullet(pendingGunX, pendingGunY, pendingVx, pendingVy));
            lastShootTime = System.currentTimeMillis();
            shotsFired++;
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void tryShoot(List<Bullet> enemyBullets, float targetX, float targetY) {
        if (isReloading || animState != AnimState.IDLE)
            return; // Blocks shooting during reload or animation

        long now = System.currentTimeMillis();
        float gunOffsetX = direction > 0 ? 80 : 10;
        float gunOffsetY = 80;
        float gunX = x + gunOffsetX;
        float gunY = y + gunOffsetY;
        float playerOffsetXFromGun = targetX - gunX;
        float playerOffsetYFromGun = Math.abs(targetY - gunY);

        boolean facingPlayer = (playerOffsetXFromGun > 0 && direction == 1) ||
                (playerOffsetXFromGun < 0 && direction == -1);
        boolean inSight = Math.abs(playerOffsetXFromGun) < GameConstants.Enemy.SHOOT_RANGE &&
                Math.abs(playerOffsetYFromGun) < GameConstants.Enemy.VERTICAL_TOLERANCE &&
                facingPlayer;

        if (inSight && now - lastShootTime >= SHOOT_COOLDOWN_MS && shotsFired < MAX_SHOTS) {
            float bulletSpeed = 12f;
            float vx = bulletSpeed * direction;
            float vy = 0;
            boolean isSecond = (shotsFired % 2 == 1);
            startShooting(enemyBullets, gunX, gunY, vx, vy, isSecond);
        }
    }
}
