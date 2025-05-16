package com.example.epicpixelplatformershootergame.helper;

import android.graphics.Color;
import android.graphics.Paint;

import com.example.epicpixelplatformershootergame.Debug;

/**
 * Uses as a config for constants
 */
public final class GameConstants {

    /**
     * Sets debug mode
     */
    public static final class DebugMode {
        public static final boolean debugMode = false;
    }

    /**
     * Provides better readability and logical understanding when using character sprite sheet
     */
    public static final class Facing_Direction {
        public static final int LEFT = 1;
        public static final int RIGHT = 0;

    }

    /**
     *
     */
    public static final class FloorTile {
        public static final int BASE_WIDTH = 32;
        public static final int BASE_HEIGHT = 32;
      //  public static final int SCALE_MULTIPLIER = 7;
     //   public int mapRows = currentMap.getArrayHeight();
       // public float scale = (float) screenHeight / (mapRows * GameConstants.FloorTile.BASE_HEIGHT);
        public static final int SCALE_MULTIPLIER = 1080 / ( BASE_HEIGHT * 5); // int should be amount of rows on map
        public static final int WIDTH = BASE_WIDTH * SCALE_MULTIPLIER;
        public static final int HEIGHT = BASE_HEIGHT * SCALE_MULTIPLIER;
    }

    public static final class Player {
        public static final int FRAME_WIDTH = 32;
        public static final int FRAME_HEIGHT = 48;
        public static final int SCALE_MULTIPLIER = 6;
        public static final int WIDTH = FRAME_WIDTH * SCALE_MULTIPLIER;
        public static final int HEIGHT = FRAME_HEIGHT * SCALE_MULTIPLIER;
    }

    public static final class GruntTwo {
        public static final int WIDTH = 105;
        public static final int HEIGHT = 41;
        public static final int SCALE_MULTIPLIER = 6;
    }

    public static final class Button {
        public static final int radius = 100;

    }

    public static final class Map {
        // 0 air, 1 sandfloor, 2 sandfloor, 3 sandfloor, 4,
        public static final int[][] tileIds = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 43, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1}
        };
    }
}

//reference the correct tile ID The tile ID is the index in the 1D array: id = row * tilesInWidth + column (rows and columns start at 0, top-left tile is id 0).

