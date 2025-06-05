package com.example.epicpixelplatformershootergame.helper;

import android.content.res.Resources;

import java.util.Arrays;
import java.util.List;

/**
 * Uses as a config for constants
 */
public final class GameConstants {

    public static final class DebugMode {
        public static final boolean DEBUG_MODE = false;
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
        public static final int RELOAD_RADIUS = 60;
        public static final float X_LEFT = 200f;
        public static final float Y_LEFT = 800f;
        public static final float X_RIGHT = 500f;
        public static final float Y_RIGHT = 800f;
        public static final float X_JUMP = Screen.SCREENWIDTH - 400f;
        public static final float Y_JUMP = 800f;
        public static final float X_SHOOT = Screen.SCREENWIDTH - 150f;
        public static final float Y_SHOOT = 800f;
        public static final float X_RELOAD = X_SHOOT + 150f;
        public static final float Y_RELOAD = Y_SHOOT - 150f;
    }

    public static final class MenuButtons {
        public static final int btnWidth = 64;
        public static final int btnHeight = 26;
        public static final float scale = 4f;
        public static final int startButtonX = (Screen.SCREENWIDTH - (int)(btnWidth * scale)) / 2;
        public static final int startButtonY = (Screen.SCREENHEIGHT - (int)(btnHeight * scale)) / 2 - 100;
        public static final int settingsButtonX = (Screen.SCREENWIDTH - (int)(btnWidth * scale)) / 2;
        public static final int settingsButtonY = (Screen.SCREENHEIGHT - (int)(btnHeight * scale)) / 2 + 100;
        public static final int restartButtonX = (Screen.SCREENWIDTH - (int)(btnWidth * scale)) / 2;
        public static final int restartButtonY = (Screen.SCREENHEIGHT - (int)(btnHeight * scale)) / 2 - 100;
        public static final int menuButtonX = (Screen.SCREENWIDTH - (int)(btnWidth * scale)) / 2;
        public static final int menuButtonY = (Screen.SCREENHEIGHT - (int)(btnHeight * scale)) / 2;
    }


    public static final class Map {
        //tile id = row * tilesInWidth (7)  + column (rows and columns start at 0, top-left tile is id 0).

        // 0 air, 1 sand floor, 2 sand floor, 3 sand floor
        // plants/vegetation:  7, 8, 9, 10
        // stone: 14
        // sign: 15
        // barrel: 37, 38
        // chest: 35, 36
        // caravan: 21-23, 28-30
        // water tower: 4-6, 11-13, 18-20, 25-27, 32-34
        // red house: 42-76 green house: 84-118
        // when chest opened: 36 instead of 35
        // when barrel opened: 38 instead of 37
        // Item IDs: coin = 17, jumpboost = 24, health = 31
        public static final int[][] tileIds = {
                {0, 42, 43, 44, 45, 46, 47, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 84, 85, 86, 87, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 49, 50, 51, 52, 53, 54, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 91, 92, 93, 94, 95, 96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 12, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 56, 57, 58, 59, 60, 61, 62, 0, 0, 0, 0, 0, 0, 0, 0, 0, 98, 99, 100, 101, 102, 103, 104, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 18, 19, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 63, 64, 65, 66, 67, 68, 69, 0, 0, 0, 0, 0, 0, 0, 0, 0, 105, 106, 107, 108, 109, 110, 111, 0, 21, 22, 23, 0, 0, 7, 0, 0, 0, 25, 26, 27, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 70, 71, 72, 73, 74, 75, 76, 0, 0, 0, 0, 14, 0, 0, 9, 10, 112, 113, 114, 115, 116, 117, 118, 0, 28, 29, 30, 8, 3, 1, 2, 0, 0, 32, 33, 34, 0, 15, 7, 0, 0, 0, 0, 10, 0, 35, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 2, 1, 3, 3, 3, 3, 3, 2, 1, 1, 1, 1, 1, 3, 3, 3, 1, 1, 3, 2, 1, 1, 1, 2, 3, 1, 1, 3, 3, 2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 2, 1, 2, 2}
        };

        static { // checks if tileIds is rectangular
            for (int i = 1; i < tileIds.length; i++) {
                if (tileIds[i].length != tileIds[0].length) {
                    throw new IllegalArgumentException("Row " + i + " in tileIds does not have " + tileIds[0].length + " elements (has " + tileIds[i].length + ")");
                }
            }
        }

        public static final List<Integer> solidTileIds = Arrays.asList(1, 2, 3, 35, 36, 56, 57, 58, 59, 60, 61, 62, 98, 99, 100, 101, 102, 103, 104);
    }

    public static final class FloorTile {
        public static final int BASE_WIDTH = 32;
        public static final int BASE_HEIGHT = 32;
        public static int SCALE_MULTIPLIER = 1;
        public static int WIDTH = BASE_WIDTH;
        public static int HEIGHT = BASE_HEIGHT;
        public static int TOTAL_ROWS = 576 / BASE_HEIGHT;
        public static int TOTAL_COLUMNS = 224 / BASE_WIDTH;
    }


    public static final class Player {
        public static final int FRAME_WIDTH = 32;
        public static final int FRAME_HEIGHT = 48;
        public static final float START_X = 6;
        public static final float START_Y = 10;
        public static final int PLAYER_COLLISION_WIDTH = 20;
        public static final int PLAYER_COLLISION_HEIGHT = 46;
        public static final int SCALE_MULTIPLIER = 6;
        public static final int WIDTH = FRAME_WIDTH * SCALE_MULTIPLIER;
        public static final int HEIGHT = FRAME_HEIGHT * SCALE_MULTIPLIER;
        public static final int TOTAL_HEALTH = 5;
        public static int HEALTH = TOTAL_HEALTH;
    }

    public static final class Enemy {
        public static final float LAST_ENEMY_X =Map.tileIds[0].length * FloorTile.WIDTH - GruntTwo.COLLISION_WIDTH + 4500; // TODO fix end of map calculation
        public static final float[] SPAWN_X = {2000, 2700, 4000, LAST_ENEMY_X };
        public static final float[] SPAWN_Y = {552, 552, 552, 552};
        public static final float SHOOT_RANGE = 900f;
        public static final float VERTICAL_TOLERANCE = 50f;
    }

    public static final class GruntTwo {
        public static final int FRAME_WIDTH = 104;
        public static final int FRAME_HEIGHT = 41;
        public static final int SCALE_MULTIPLIER = 6;
        public static final int COLLISION_WIDTH = 25;
        public static final int COLLISION_HEIGHT = 41;
        public static final int COLLISION_OFFSET_X = 0;
        public static final int COLLISION_OFFSET_Y = 0;
    }

    public static class Horse {
        public static final int WIDTH = 150;
        public static final int HEIGHT = 100;
        public static final int SCALE_MULTIPLIER = 4;
        public static final int mapPixelWidth = Map.tileIds[0].length * FloorTile.WIDTH;
        public static final int horseX = mapPixelWidth - GameConstants.Horse.WIDTH + 5200; // TODO fix end of map calculation
        public static final int horseY = 420; // TODO implement an dynamic way of getting the floor at the X

    }

    public static final class Weapon {
        public static final float BULLET_MAX_DISTANCE = 800f;
    }

    public static final class Physics {
        public static final float GRAVITY = 0.5f;
        public static final float JUMP_STRENGTH = -14f;
        public static final float PLAYER_MOVE_SPEED = 10f;
        public static final float ANIMATION_SPEED = 7.5f;
        public static final float GRAVITY_BOOST_HOLDING = 0.45f;
        public static final long MAX_JUMP_BUFFER_MS = 250;
    }

    public static int getCollisionOffsetX() {
        return (Player.FRAME_WIDTH - Player.PLAYER_COLLISION_WIDTH) / 2;
    }

    public static int getCollisionOffsetY() {
        return (Player.FRAME_HEIGHT - Player.PLAYER_COLLISION_HEIGHT) / 2;
    }
}

