package com.example.epicpixelplatformershootergame.inputs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.example.epicpixelplatformershootergame.GamePanel;

import java.util.Collection;

public class TouchEvents {
    private GamePanel gamePanel;

    private float xCenterLeft = 150, yCenterLeft = 1700;
    private float xCenterRight = 950, yCenterRight = 1700;
    private float radius = 100;
    private Paint circlePaint;

    public TouchEvents(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
    }

    public void draw(Canvas c) {
        c.drawCircle(xCenterLeft, yCenterLeft, radius, circlePaint);
        c.drawCircle(xCenterRight, yCenterRight, radius, circlePaint);
    }

    public boolean touchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();

                float aLeft = Math.abs(x - xCenterLeft);
                float bLeft = Math.abs(y - yCenterLeft);
                float cLeft = (float) Math.hypot(aLeft, bLeft);

                float aRight = Math.abs(x - xCenterRight);
                float bRight = Math.abs(y - yCenterRight);
                float cRight = (float) Math.hypot(aRight, bRight);

                if (cRight <= radius) {
                    System.out.println("inside Right button"); // debug
                    gamePanel.setMoveRight(true);
                    gamePanel.setMoveLeft(false);
                } else if (cLeft <= radius) {
                    System.out.println("inside Left button"); // debug
                    gamePanel.setMoveLeft(true);
                    gamePanel.setMoveRight(false);
                }
                break;

            case MotionEvent.ACTION_UP:
                gamePanel.setMoveLeft(false);
                gamePanel.setMoveRight(false);
                break;
        }

        return true;
    }
}
