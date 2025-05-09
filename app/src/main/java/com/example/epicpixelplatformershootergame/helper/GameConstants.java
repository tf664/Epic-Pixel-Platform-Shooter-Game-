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
        static {
            Debug.setDebugMode(true);
        }
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
    public static final class Sprite {
        public static final int DEFAULT_SIZE = 32;
        public static final int SCALE_MULTIPLIER = 3;
        public static final int SIZE = DEFAULT_SIZE * SCALE_MULTIPLIER;
    }
}

