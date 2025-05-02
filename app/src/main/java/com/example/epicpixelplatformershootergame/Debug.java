package com.example.epicpixelplatformershootergame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Debug {

    private boolean debugMode = false;

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    // Rectangle around character
    public void drawDebug(Canvas c, float x, float y, float spriteWidth, float spriteHeight) {
        if (debugMode) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);

            c.drawRect(x, y, x + spriteWidth, y + spriteHeight, paint);
        }
    }
}
