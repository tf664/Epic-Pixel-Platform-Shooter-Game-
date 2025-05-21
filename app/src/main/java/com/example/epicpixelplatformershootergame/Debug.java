package com.example.epicpixelplatformershootergame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.epicpixelplatformershootergame.entities.Bullet;
import com.example.epicpixelplatformershootergame.entities.Enemy;
import com.example.epicpixelplatformershootergame.helper.GameConstants;

import java.util.List;

public class Debug {
    private static boolean debugMode;

    private Debug() {
    } // prevent instantiation

    public static void setDebugMode(boolean debugMode) {
        Debug.debugMode = debugMode;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void spriteInfo(Bitmap spriteSheet, int rows, int columns) {
        System.out.println("Height: " + spriteSheet.getHeight()); // debug
        System.out.println("Width: " + spriteSheet.getWidth()); // debug
        System.out.println("Rows: " + rows); // debug
        System.out.println("Columns: " + columns); // debug
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

    public static void drawDebugHitAreas(Canvas c, List<Enemy> enemies, List<Bullet> bullets, int cameraX, int mapOffsetY) {
        Paint enemyPaint = new Paint();
        enemyPaint.setColor(Color.argb(120, 255, 0, 0)); // semi-transparent red
        enemyPaint.setStyle(Paint.Style.FILL);

        Paint bulletPaint = new Paint();
        bulletPaint.setColor(Color.argb(120, 255, 255, 0)); // semi-transparent yellow
        bulletPaint.setStyle(Paint.Style.FILL);

        // Draw enemy hitboxes
        for (Enemy enemy : enemies) {
            RectF rect = GamePanel.getScaledCollisionRect(enemy);
            // Adjust for camera and map offset if needed:
            float left = rect.left - cameraX;
            float top = rect.top + mapOffsetY;
            float right = rect.right - cameraX;
            float bottom = rect.bottom + mapOffsetY;

            c.drawRect(left, top, right, bottom, enemyPaint);
        }

        // Draw bullet hit areas
        float bulletRadius = 10;
        for (Bullet bullet : bullets) {
            if (bullet.active) {
                float cx = bullet.x - cameraX;
                float cy = bullet.y + mapOffsetY;
                c.drawCircle(cx, cy, bulletRadius, bulletPaint);
            }
        }
    }
}
