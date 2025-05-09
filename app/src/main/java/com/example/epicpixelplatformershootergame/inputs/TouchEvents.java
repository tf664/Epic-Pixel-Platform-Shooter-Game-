package com.example.epicpixelplatformershootergame.inputs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.example.epicpixelplatformershootergame.GamePanel;

public class TouchEvents {
    private GamePanel gamePanel;

    private float xCenterLeft = 150, yCenterLeft = 800;
    private float xCenterRight = 500, yCenterRight = 800;
    private float radius = 100;
    private Paint circlePaint;

    private Paint jumpPaint;
    private float xCenterJump = 1800, yCenterJump = 800;

    private boolean isJumping = false;  // To track whether the jump button was touched

    public TouchEvents(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);

        jumpPaint = new Paint();
        jumpPaint.setColor(Color.BLUE);
    }

    public void draw(Canvas c) {
        c.drawCircle(xCenterLeft, yCenterLeft, radius, circlePaint);
        c.drawCircle(xCenterRight, yCenterRight, radius, circlePaint);

        c.drawCircle(xCenterJump, yCenterJump, radius, jumpPaint);
    }

    public boolean touchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);
                handleTouchDown(x, y);
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                float x = event.getX(pointerIndex);
                float y = event.getY(pointerIndex);
                handleTouchUp(x, y);
                break;
            }

            case MotionEvent.ACTION_MOVE:
                float xMove = event.getX();
                float yMove = event.getY();

                // Reset movement flags
                gamePanel.setMoveLeft(false);
                gamePanel.setMoveRight(false);

                // Only move if the touch is within the left or right button
                if (isWithin(xMove, yMove, xCenterLeft, yCenterLeft, radius)) {
                    gamePanel.setMoveLeft(true);
                }
                if (isWithin(xMove, yMove, xCenterRight, yCenterRight, radius)) {
                    gamePanel.setMoveRight(true);
                }

                // Handle jump logic - only initiate jump if the finger is on the jump button
                if (isWithin(xMove, yMove, xCenterJump, yCenterJump, radius) && !isJumping) {
                    gamePanel.setJump(true);  // Trigger jump action
                }
                break;
        }
        return true;
    }

    private void handleTouchDown(float x, float y) {
        if (isWithin(x, y, xCenterRight, yCenterRight, radius)) {
            gamePanel.setMoveRight(true);
        } else if (isWithin(x, y, xCenterLeft, yCenterLeft, radius)) {
            gamePanel.setMoveLeft(true);
        } else if (isWithin(x, y, xCenterJump, yCenterJump, radius)) {
            gamePanel.setJump(true);
            isJumping = true;  // Mark that jump has been initiated
        }
    }

    private void handleTouchUp(float x, float y) {
        if (isWithin(x, y, xCenterRight, yCenterRight, radius)) {
            gamePanel.setMoveRight(false);
        } else if (isWithin(x, y, xCenterLeft, yCenterLeft, radius)) {
            gamePanel.setMoveLeft(false);
        } else if (isWithin(x, y, xCenterJump, yCenterJump, radius)) {
            gamePanel.setJump(false);
            isJumping = false;  // Reset jump state after release
        }
    }

    private boolean isWithin(float x, float y, float centerX, float centerY, float radius) {
        return Math.hypot(x - centerX, y - centerY) <= radius;
    }
}
