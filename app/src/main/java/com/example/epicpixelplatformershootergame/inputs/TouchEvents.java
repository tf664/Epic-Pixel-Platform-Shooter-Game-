package com.example.epicpixelplatformershootergame.inputs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.example.epicpixelplatformershootergame.GamePanel;
import com.example.epicpixelplatformershootergame.helper.GameConstants;

public class TouchEvents {
    private GamePanel gamePanel;

    private float xCenterLeft = GameConstants.Button.X_LEFT, yCenterLeft = GameConstants.Button.Y_LEFT;
    private float xCenterRight = GameConstants.Button.X_RIGHT, yCenterRight = GameConstants.Button.Y_RIGHT;
    private float xCenterJump = GameConstants.Button.X_JUMP, yCenterJump = GameConstants.Button.Y_JUMP;

    private Paint circlePaint;
    private Paint jumpPaint;

    // Track previous button states
    private boolean prevLeftPressed = false;
    private boolean prevRightPressed = false;
    private boolean prevJumpPressed = false;
    private long jumpBufferedAt = 0;
    private static final long MAX_JUMP_BUFFER_MS = 250; // TODO: GameConstants

    public TouchEvents(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        jumpPaint = new Paint();
        jumpPaint.setColor(Color.BLUE);
    }

    public void draw(Canvas c) {
        c.drawCircle(xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS, circlePaint);
        c.drawCircle(xCenterRight, yCenterRight, GameConstants.Button.RADIUS, circlePaint);
        c.drawCircle(xCenterJump, yCenterJump, GameConstants.Button.RADIUS, jumpPaint);
    }

    public boolean touchEvent(MotionEvent event) {
        boolean leftPressed = false;
        boolean rightPressed = false;
        boolean jumpPressed = false;

        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        // If all fingers are lifted, reset all flags
        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && pointerCount == 1) {
            gamePanel.setMoveLeft(false);
            gamePanel.setMoveRight(false);
            gamePanel.setJumpButtonHeld(false);
            prevLeftPressed = false;
            prevRightPressed = false;
            prevJumpPressed = false;
            return true;
        }

        // On pointer up, check remaining pointers
        if (action == MotionEvent.ACTION_POINTER_UP) {
            int upIndex = event.getActionIndex();
            for (int i = 0; i < pointerCount; i++) {
                if (i == upIndex) continue;
                float x = event.getX(i);
                float y = event.getY(i);
                if (isWithin(x, y, xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS))
                    leftPressed = true;
                if (isWithin(x, y, xCenterRight, yCenterRight, GameConstants.Button.RADIUS))
                    rightPressed = true;
                if (isWithin(x, y, xCenterJump, yCenterJump, GameConstants.Button.RADIUS))
                    jumpPressed = true;
            }
        } else {
            // For all other actions, scan all pointers
            for (int i = 0; i < pointerCount; i++) {
                float x = event.getX(i);
                float y = event.getY(i);
                if (isWithin(x, y, xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS))
                    leftPressed = true;
                if (isWithin(x, y, xCenterRight, yCenterRight, GameConstants.Button.RADIUS))
                    rightPressed = true;
                if (isWithin(x, y, xCenterJump, yCenterJump, GameConstants.Button.RADIUS))
                    jumpPressed = true;
            }
        }

        // Optional: Prioritize right if both are pressed
        if (leftPressed && rightPressed) {
            leftPressed = false;
            rightPressed = true;
        }

        if (leftPressed != prevLeftPressed) gamePanel.setMoveLeft(leftPressed);
        if (rightPressed != prevRightPressed) gamePanel.setMoveRight(rightPressed);
        if (jumpPressed != prevJumpPressed) gamePanel.setJumpButtonHeld(jumpPressed);

        if (jumpPressed && !prevJumpPressed) {
            jumpBufferedAt = System.currentTimeMillis();   // remember this press
        }

        prevLeftPressed = leftPressed;
        prevRightPressed = rightPressed;
        prevJumpPressed = jumpPressed;

        return true;
    }

    private boolean isWithin(float x, float y, float centerX, float centerY, float radius) {
        return Math.hypot(x - centerX, y - centerY) <= radius;
    }

    public boolean hasBufferedJump() {
        return (System.currentTimeMillis() - jumpBufferedAt) <= MAX_JUMP_BUFFER_MS
                && jumpBufferedAt != 0;
    }

    public void clearJumpBuffer() {
        jumpBufferedAt = 0;
    }

}