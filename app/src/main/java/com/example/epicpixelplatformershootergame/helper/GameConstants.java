package com.example.epicpixelplatformershootergame.helper;

import android.content.res.Resources;

/**
 * Uses as a config for constants
 */
public final class GameConstants {

    public static final class DebugMode {
        public static final boolean DEBUG_MODE = true;
    }

    /**
     * Provides better readability and logical understanding when using character sprite sheet
     */
    public static final class Facing_Direction {
        public static final int LEFT = 1;
        public static final int RIGHT = 0;

    }

    public static final class Screen {
        public static int SCREENWIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
        public static int SCREENHEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static final class Camera {
        public static int leftThreshold = Screen.SCREENWIDTH / 3;
        public static int rightThreshold = Screen.SCREENWIDTH * 2 / 3;
    }


    public static final class Button {
        public static final int RADIUS = 100;
        public static final float X_LEFT = 200f;
        public static final float Y_LEFT = 800f;
        public static final float X_RIGHT = 500f;
        public static final float Y_RIGHT = 800f;
        public static final float X_JUMP = 1800f;
        public static final float Y_JUMP = 800f;
    }

    public static final class FloorTile {
        public static final int BASE_WIDTH = 32;
        public static final int BASE_HEIGHT = 32;
        public static int SCALE_MULTIPLIER = 1;
        public static int WIDTH = BASE_WIDTH;
        public static int HEIGHT = BASE_HEIGHT;
    }

    public static final class Map {
        // 0 air, 1 sand floor, 2 sand floor, 3 sand floor, 4,
        //tile id = row * tilesInWidth + column (rows and columns start at 0, top-left tile is id 0).
        public static final int[][] tileIds = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 19, 20, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 26, 27, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 32, 33, 34, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 3, 0, 1, 2, 3, 1, 0, 1, 0, 3, 0, 0, 0, 0, 0, 39, 40, 41, 0, 0, 35, 36, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0}
        };
    }


    public static final class Player {
        public static final int FRAME_WIDTH = 32;
        public static final int FRAME_HEIGHT = 48;
        public static final int PLAYER_COLLISION_WIDTH = 20;
        public static final int PLAYER_COLLISION_HEIGHT = 46;
        public static final int SCALE_MULTIPLIER = 6;
        public static final int WIDTH = FRAME_WIDTH * SCALE_MULTIPLIER;
        public static final int HEIGHT = FRAME_HEIGHT * SCALE_MULTIPLIER;
    }

    public static final class Physics {
        public static final float GRAVITY = 0.5f;
        public static final float JUMP_STRENGTH = -16f;
        public static final float PLAYER_MOVE_SPEED = 10f;
        public static final double ANIMATION_SPEED = 7.5;
    }

    public static int getCollisionOffsetX() {
        return (Player.FRAME_WIDTH - Player.PLAYER_COLLISION_WIDTH) / 2;
    }

    public static int getCollisionOffsetY() {
        return (Player.FRAME_HEIGHT - Player.PLAYER_COLLISION_HEIGHT) / 2;
    }

    public static final class GruntTwo {
        public static final int WIDTH = 105;
        public static final int HEIGHT = 41;
        public static final int SCALE_MULTIPLIER = 6;
    }
}

