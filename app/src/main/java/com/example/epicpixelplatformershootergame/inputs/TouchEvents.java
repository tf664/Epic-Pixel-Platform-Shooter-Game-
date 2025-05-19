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
    private boolean isJumping = false;  // Track whether the jump button was touched

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

                // Move left button
                if (isWithin(xMove, yMove, xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS)) {
                    gamePanel.setMoveLeft(true);
                }
                // Move right button
                if (isWithin(xMove, yMove, xCenterRight, yCenterRight, GameConstants.Button.RADIUS)) {
                    gamePanel.setMoveRight(true);
                }
                // Jump button
                if (isWithin(xMove, yMove, xCenterJump, yCenterJump, GameConstants.Button.RADIUS) && !isJumping) {
                    gamePanel.setJump(true);  // Trigger jump action
                }
                break;
        }
        return true;
    }

    private boolean isWithin(float x, float y, float centerX, float centerY, float radius) {
        return Math.hypot(x - centerX, y - centerY) <= radius;
    }

    private void handleTouchDown(float x, float y) {
        handleMovementButtons(x, y);
        handleJumpButton(x, y);
    }

    private void handleTouchUp(float x, float y) {
        if (isWithin(x, y, xCenterRight, yCenterRight, GameConstants.Button.RADIUS)) {
            gamePanel.setMoveRight(false);
        } else if (isWithin(x, y, xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS)) {
            gamePanel.setMoveLeft(false);
        } else if (isWithin(x, y, xCenterJump, yCenterJump, GameConstants.Button.RADIUS)) {
            gamePanel.setJump(false);
            isJumping = false;  // Reset jump state after release
        }
    }

    private void handleMovementButtons(float x, float y) {
        if (isWithin(x, y, xCenterLeft, yCenterLeft, GameConstants.Button.RADIUS)) {
            gamePanel.setMoveLeft(true);
        }
        if (isWithin(x, y, xCenterRight, yCenterRight, GameConstants.Button.RADIUS)) {
            gamePanel.setMoveRight(true);
        }
    }

    private void handleJumpButton(float x, float y) {
        if (isWithin(x, y, xCenterJump, yCenterJump, GameConstants.Button.RADIUS) && !isJumping) {
            gamePanel.setJump(true);
            isJumping = true; // Mark that the jump was initiated
        }
    }


}
