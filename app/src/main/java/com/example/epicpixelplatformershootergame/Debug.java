package com.example.epicpixelplatformershootergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.List;

public class Debug {
    private static boolean debugMode;

    private Debug() {} // prevent instantiation

    public static void setDebugMode(boolean debugMode) {
        Debug.debugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    // Rectangle around character
    public static void drawDebugPlayer(Canvas c, float x, float y, float spriteWidth, float spriteHeight) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            c.drawRect(x, y, x + spriteWidth, y + spriteHeight, paint);
    }

    public static void drawDebugMap(Canvas c, List<Rect> collisionRects) {
        Paint bluePaint = new Paint();
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStrokeWidth(5);
        for (Rect r : collisionRects) {
            c.drawRect(r, bluePaint);
            }
    }

    public static void spriteInfo(Bitmap spriteSheet, int rows, int columns) {
        System.out.println("Height: " + spriteSheet.getHeight()); // debug
        System.out.println("Width: " + spriteSheet.getWidth()); // debug
        System.out.println("Rows: " + rows); // debug
        System.out.println("Columns: " + columns); // debug
    }
}
