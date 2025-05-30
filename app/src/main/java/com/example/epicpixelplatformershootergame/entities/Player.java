package com.example.epicpixelplatformershootergame.entities;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.example.epicpixelplatformershootergame.GameState;
import com.example.epicpixelplatformershootergame.entities.Bullet;
import com.example.epicpixelplatformershootergame.entities.Enemy;
import com.example.epicpixelplatformershootergame.entities.GameEntityAssets;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.inputs.TouchEvents;
import com.example.epicpixelplatformershootergame.environments.MapManager;

import java.util.List;

public class Player {
    public int playerAnimationIndexX, playerAnimationIndexY;
    public int playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    public int playerAnimationFrame;
    public int animationTick;

    public float playerX = GameConstants.Player.START_X, playerY = GameConstants.Player.START_Y;
    public float playerVelocityX = 0, playerVelocityY = 0;
    public boolean isJumping = false;
    public boolean jumpButtonHeld = false;

    private GameState gameState;
    public boolean isShooting = false;
    public boolean pendingShoot = false;

    public boolean moveLeft = false, moveRight = false;

    // Dependencies
    private TouchEvents touchEvents;
    private MapManager mapManager;
    private List<Bullet> bullets;

    public Player(TouchEvents touchEvents, MapManager mapManager, List<Bullet> bullets, GameState gameState) {
        this.touchEvents = touchEvents;
        this.mapManager = mapManager;
        this.bullets = bullets;
        this.gameState = gameState;
    }

    /**
     * Sets whether the player is moving left.
     * @param moveLeft true to move left, false otherwise.
     */
    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
        if (moveLeft)
            playerFaceDirection = GameConstants.Facing_Direction.LEFT;
    }

    /**
     * Sets whether the player is moving right.
     * @param moveRight true to move right, false otherwise.
     */
    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
        if (moveRight)
            playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    }

    /**
     * Sets whether the jump button is held.
     * @param held true if jump button is held.
     */
    public void setJumpButtonHeld(boolean held) {
        this.jumpButtonHeld = held;
    }

    public void updateAnimation() {
        animationTick++;

        if (animationTick >= GameConstants.Physics.ANIMATION_SPEED) {
            animationTick = 0;

            if (isShooting && gameState.getAmmoCount() > 0) {
                setPlayerShootingAnimation();
                return;
            }
            if (moveRight) {
                setPlayerAnimationRight();
            } else if (moveLeft) {
                setPlayerAnimationLeft();
            } else {
                setPlayerAnimationIdle();
            }
        }
    }

    public void setPlayerAnimationRight() {
        int[] rightAnimY = {0, 0, 0, 1};
        int[] rightAnimX = {1, 2, 3, 0};
        playerAnimationFrame = (playerAnimationFrame + 1) % rightAnimX.length;
        playerAnimationIndexY = rightAnimY[playerAnimationFrame];
        playerAnimationIndexX = rightAnimX[playerAnimationFrame];
    }

    public void setPlayerAnimationLeft() {
        int[] leftAnimY = {1, 1, 2, 2};
        int[] leftAnimX = {2, 3, 0, 1};
        playerAnimationFrame = (playerAnimationFrame + 1) % leftAnimX.length;
        playerAnimationIndexY = leftAnimY[playerAnimationFrame];
        playerAnimationIndexX = leftAnimX[playerAnimationFrame];
    }

    public void setPlayerShootingAnimation() {
        final int[] rightShootingAnimY = {2, 2, 3};
        final int[] rightShootingAnimX = {2, 3, 0};
        final int[] leftShootingAnimY = {3, 3, 3};
        final int[] leftShootingAnimX = {1, 2, 3};

        int[] animY, animX;
        if (playerFaceDirection == GameConstants.Facing_Direction.RIGHT) {
            animY = rightShootingAnimY;
            animX = rightShootingAnimX;
        } else {
            animY = leftShootingAnimY;
            animX = leftShootingAnimX;
        }

        if (playerAnimationFrame == 0)
            spawnBullet();

        if (playerAnimationFrame >= animX.length) {
            isShooting = false;
            playerAnimationFrame = 0;
            setPlayerAnimationIdle();
            if (touchEvents.hasBufferedShoot()) {
                startShooting();
                touchEvents.clearShootBuffer();
            }
            return;
        }
        playerAnimationIndexY = animY[playerAnimationFrame];
        playerAnimationIndexX = animX[playerAnimationFrame];
        playerAnimationFrame++;
    }

    public void startShooting() {
        if (gameState != null && gameState.getAmmoCount() == 0) return;
        isShooting = true;
        playerAnimationFrame = 0;
        setPlayerShootingAnimation();
    }

    public void setPlayerAnimationIdle() {
        if (playerFaceDirection == GameConstants.Facing_Direction.RIGHT) {
            playerAnimationIndexY = 0;
            playerAnimationIndexX = 0;
        } else {
            playerAnimationIndexY = 1;
            playerAnimationIndexX = 1;
        }
    }

    /**
     * Draws the player sprite on the canvas.
     * @param c Canvas to draw on.
     */
    public void drawPlayer(Canvas c) {
        int mapOffsetY = mapManager.getMapOffsetY();
        int cameraX = mapManager.getCameraX();
        c.drawBitmap(GameEntityAssets.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX),
                playerX - cameraX, playerY + mapOffsetY, null);
    }

    public void tryConsumeJumpBuffer() {
        if (!isJumping && touchEvents.hasBufferedJump()) {
            playerVelocityY = GameConstants.Physics.JUMP_STRENGTH;
            isJumping = true;
            touchEvents.clearJumpBuffer();
        }
    }

    public void applyGravity() {
        if (playerVelocityY < 0 && jumpButtonHeld)
            playerVelocityY += GameConstants.Physics.GRAVITY * GameConstants.Physics.GRAVITY_BOOST_HOLDING;
        else
            playerVelocityY += GameConstants.Physics.GRAVITY;
    }

    public void handleMovement() {
        float moveSpeed = GameConstants.Physics.PLAYER_MOVE_SPEED;
        playerVelocityX = 0;
        if (moveLeft)
            playerVelocityX = -moveSpeed;
        else if (moveRight)
            playerVelocityX = moveSpeed;
    }

    /**
     * Clamps the player's X position within the map bounds.
     * @param nextX Proposed next X position.
     * @return Clamped X position.
     */
    public float clampPlayerPosition(float nextX) {
        int mapPixelWidth = mapManager.getCurrentMap().getArrayWidth() * GameConstants.FloorTile.WIDTH;
        return Math.max(0, Math.min(nextX, mapPixelWidth - GameConstants.Player.WIDTH));
    }

    public void spawnBullet() {
        if (gameState != null && gameState.getAmmoCount() > 0) {
            float bulletSpeed = 20f;
            float bulletX = playerX + (float) GameConstants.Player.WIDTH / 2;
            float bulletY = playerY + (float) GameConstants.Player.HEIGHT / 2;
            float dir = playerFaceDirection == GameConstants.Facing_Direction.RIGHT ? 1 : -1;
            bullets.add(new Bullet(bulletX, bulletY, bulletSpeed * dir, 0));
            gameState.decreaseAmmo();
        }
    }

    public void reload() {
        if (gameState != null) {
            gameState.reloadAmmo();
        }
    }

    public void takeDamage(int dmg) {
        GameConstants.Player.HEALTH -= dmg;
        if (GameConstants.Player.HEALTH < 0) GameConstants.Player.HEALTH = 0;
    }
}