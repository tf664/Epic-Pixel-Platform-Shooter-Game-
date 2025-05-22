package com.example.epicpixelplatformershootergame.physics;

import com.example.epicpixelplatformershootergame.environments.GameMap;
import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class PlayerCollisionHandler {
    private final int playerWidth, playerHeight;
    private final int tileWidth, tileHeight;
    private final float collisionOffsetX, collisionOffsetY;

    /*
     * Constructor initializes dimensions of the player and tiles, as well as the collision offsets, values are
     * based on constants defined in the GameConstants class.
     */
    public PlayerCollisionHandler() {
        this.playerWidth = GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER;
        this.playerHeight = GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER;
        this.tileWidth = GameConstants.FloorTile.WIDTH;
        this.tileHeight = GameConstants.FloorTile.HEIGHT;
        this.collisionOffsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
        this.collisionOffsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
    }


    public PlayerCollisionResult checkCollision(GameMap map, float playerY, float nextX, float nextY,
                                                float playerVelocityX, float playerVelocityY) {
        boolean isJumping = playerVelocityY != 0;
        float resolvedX = nextX;
        float resolvedY;

        // Horizontal collision
        boolean canMoveHorizontally = true;
        float newLeft = nextX + collisionOffsetX;
        float newRight = nextX + collisionOffsetX + playerWidth - 1;
        float top = playerY + collisionOffsetY;
        float bottom = playerY + collisionOffsetY + playerHeight - 1;
        float stepY = Math.max(1, tileHeight / 3f);

        for (float testY = top; testY <= bottom; testY += stepY) {
            float testX = (playerVelocityX > 0) ? newRight : newLeft;
            if (map.isSolidTileAt(testX, testY)) {
                canMoveHorizontally = false;
            }
        }
        if (canMoveHorizontally) {
            float testX = (playerVelocityX > 0) ? newRight : newLeft;
            if (map.isSolidTileAt(testX, bottom)) {
                canMoveHorizontally = false;
            }
        }
        if (canMoveHorizontally) {
            resolvedX = nextX;
        } else {
            if (playerVelocityX > 0) {
                int tileX = (int) (newRight / tileWidth);
                resolvedX = tileX * tileWidth - collisionOffsetX - playerWidth;
            } else if (playerVelocityX < 0) {
                int tileX = (int) (newLeft / tileWidth);
                resolvedX = (tileX + 1) * tileWidth - collisionOffsetX;
            }
        }

        // Vertical collision
        boolean canMoveVertically = true;
        float left = resolvedX + collisionOffsetX;
        float right = resolvedX + collisionOffsetX + playerWidth - 1;
        float newTop = nextY + collisionOffsetY;
        float newBottom = nextY + collisionOffsetY + playerHeight - 1;
        float stepX = Math.max(1, tileWidth / 3f);

        if (playerVelocityY >= 0) {
            for (float tx = left; tx <= right; tx += stepX) {
                if (map.isSolidTileAt(tx, newBottom)) {
                    canMoveVertically = false;
                }
            }
            if (canMoveVertically && map.isSolidTileAt(right, newBottom)) {
                canMoveVertically = false;
            }
        } else {
            for (float tx = left; tx <= right; tx += stepX) {
                if (map.isSolidTileAt(tx, newTop)) {
                    canMoveVertically = false;
                }
            }
            if (canMoveVertically && map.isSolidTileAt(right, newTop)) {
                canMoveVertically = false;
            }
        }

        if (canMoveVertically) {
            resolvedY = nextY;
        } else {
            if (playerVelocityY > 0) {
                int tileY = (int) (newBottom / tileHeight);
                float tileTop = tileY * tileHeight;
                resolvedY = tileTop - collisionOffsetY - playerHeight;
                isJumping = false;
                playerVelocityY = 0;
            } else {
                int tileY = (int) (newTop / tileHeight);
                float tileBottom = (tileY + 1) * tileHeight;
                resolvedY = tileBottom - collisionOffsetY;
                playerVelocityY = 0;
            }
        }

        return new PlayerCollisionResult(resolvedX, resolvedY, canMoveHorizontally ? playerVelocityX : 0, canMoveVertically ? playerVelocityY : 0, isJumping);
    }

    public static class PlayerCollisionResult {
        public final float x, y, velocityX, velocityY;
        public final boolean isJumping;

        public PlayerCollisionResult(float x, float y, float velocityX, float velocityY, boolean isJumping) {
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.isJumping = isJumping;
        }
    }
}