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

    boolean leftPressed = false, rightPressed = false, jumpPressed = false;

    private Paint circlePaint; // move to GameConstants?
    private Paint jumpPaint; // move to GameConstants?

    // Track previous button states
    private boolean prevLeftPressed = false, prevRightPressed = false, prevJumpPressed = false;
    private long jumpBufferedAt = 0;


    public TouchEvents(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        jumpPaint = new Paint();
        jumpPaint.setColor(Color.BLUE);
    }

    /**
     * Draws the movement buttons on the screen
     *
     * @param c Canvas to draw on
     */
    public void draw(Canvas c) {
        c.drawCircle(xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS, circlePaint);
        c.drawCircle(xCenterRight, yCenterRight, GameConstants.Button.RADIUS, circlePaint);
        c.drawCircle(xCenterJump, yCenterJump, GameConstants.Button.RADIUS, jumpPaint);
    }

    /**
     * Handles touch events for the movement buttons
     *
     * @param event MotionEvent to handle
     * @return true if the event was handled
     */
    public boolean touchEvent(MotionEvent event) {
        // Clear current button states
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        // Get info about the touch event
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        // No touch
        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && pointerCount == 1) {
            gamePanel.setMoveLeft(false);
            gamePanel.setMoveRight(false);
            gamePanel.setJumpButtonHeld(false);
            prevLeftPressed = false;
            prevRightPressed = false;
            prevJumpPressed = false;
            return true;
        }
        // One finger lifter with others remaining on screen
        if (action == MotionEvent.ACTION_POINTER_UP) {
            int upIndex = event.getActionIndex();
            for (int i = 0; i < pointerCount; i++) {
                if (i == upIndex) continue; // skip lifted finger
                updateButtonPresses(event.getX(i), event.getY(i));
            }
        }
        // Loop through all pointers to register press
        else {
            for (int i = 0; i < pointerCount; i++) {
                updateButtonPresses(event.getX(i), event.getY(i));
            }
        }

        // Prioritize right if both are pressed
        if (leftPressed && rightPressed) {
            leftPressed = false;
            rightPressed = true;
        }

        // Send input to gamePanel, while restricting redundant calls for smoothness
        if (leftPressed != prevLeftPressed) gamePanel.setMoveLeft(leftPressed);
        if (rightPressed != prevRightPressed) gamePanel.setMoveRight(rightPressed);
        if (jumpPressed != prevJumpPressed) gamePanel.setJumpButtonHeld(jumpPressed);

        // Buffer jump input
        if (jumpPressed && !prevJumpPressed) {
            jumpBufferedAt = System.currentTimeMillis();
        }

        // Update previous button states for next touch event processing
        prevLeftPressed = leftPressed;
        prevRightPressed = rightPressed;
        prevJumpPressed = jumpPressed;

        return true;
    }

    private void updateButtonPresses(float x, float y) {
        if (isWithin(x, y, xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS))
            leftPressed = true;
        if (isWithin(x, y, xCenterRight, yCenterRight, GameConstants.Button.RADIUS))
            rightPressed = true;
        if (isWithin(x, y, xCenterJump, yCenterJump, GameConstants.Button.RADIUS))
            jumpPressed = true;
    }

    private boolean isWithin(float x, float y, float centerX, float centerY, float radius) {
        return Math.hypot(x - centerX, y - centerY) <= radius;
    }

    public boolean hasBufferedJump() {
        return ((System.currentTimeMillis() - jumpBufferedAt) <= GameConstants.Physics.MAX_JUMP_BUFFER_MS)
                && jumpBufferedAt != 0;
    }

    public void clearJumpBuffer() {
        jumpBufferedAt = 0;
    }

}