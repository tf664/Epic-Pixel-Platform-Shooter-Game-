package com.example.epicpixelplatformershootergame.helper;

import com.example.epicpixelplatformershootergame.Debug;

/**
 * Uses as a config for constants
 */
public final class GameConstants {

    /**
     * Sets debug mode
     */
    public static final class DebugMode {
        public static final boolean debugMode = true;
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
        public static final int SCALE_MULTIPLIER = 7;
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
        public static final int SCALE_MULTIPLIER = 5; // adjust visually
    }
}

