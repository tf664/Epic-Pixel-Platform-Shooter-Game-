package com.example.epicpixelplatformershootergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Debug {

    private static boolean debugMode = false;

    private Debug() {} // prevent instantiation

    public static void setDebugMode(boolean debugMode) {
        Debug.debugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    // Rectangle around character
    public static void drawDebug(Canvas c, float x, float y, float spriteWidth, float spriteHeight) {
        if (debugMode) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            c.drawRect(x, y, x + spriteWidth, y + spriteHeight, paint);
        }
    }

    public static void spriteInfo(Bitmap spriteSheet, int rows, int columns) {
        System.out.println("Height: " + spriteSheet.getHeight()); // debug
        System.out.println("Width: " + spriteSheet.getWidth()); // debug
        System.out.println("Rows: " + rows); // debug
        System.out.println("Columns: " + columns); // debug
    }
}
