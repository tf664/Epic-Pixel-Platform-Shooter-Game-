package com.example.epicpixelplatformershootergame;

import static com.example.epicpixelplatformershootergame.MainActivity.GAME_HEIGHT;
import static com.example.epicpixelplatformershootergame.MainActivity.GAME_WIDTH;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.epicpixelplatformershootergame.entities.GameCharacters;
import com.example.epicpixelplatformershootergame.environments.GameMap;
import com.example.epicpixelplatformershootergame.environments.MapManager;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.inputs.TouchEvents;

import java.util.Random;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint();
    private SurfaceHolder holder;

    private Random rand = new Random();

    private GameLoop gameLoop;
    private TouchEvents touchEvents;

    private int playerAnimationIndexX, playerAnimationIndexY = 0, playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    private int gruntTwoAnimationIndexY;

    private boolean moveLeft = false, moveRight = false;
    private int screenWidth, screenHeight;
    private int animationFrame;

    private int animationTick;
    private double animationSpeed = 7.5;

    // Map
    private MapManager mapManager;

    // Jumping Physics
    private float playerX = 100, playerY = 100;
    private float playerVelocityX = 0, playerVelocityY = 0;
    private boolean isJumping = false;

    private final float GRAVITY = 0.5f;
    private final float JUMP_STRENGTH = -16;


    public GamePanel(Context context) {
        super(context);
        Debug.setDebugMode(GameConstants.DebugMode.debugMode);

        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);

        gameLoop = new GameLoop(this);
        mapManager = new MapManager();

    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        mapManager.updateCamera(playerX);
        mapManager.draw(c);

        touchEvents.draw(c);

        // Step 2: Draw the player and other characters (on top of the tiles)
        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX),
                playerX, playerY, null);
        c.drawBitmap(GameCharacters.GRUNTTWO.getSprite(gruntTwoAnimationIndexY, 0), 800, 500, null);

        if (Debug.isDebugMode())
            Debug.drawDebug(c, playerX, playerY,
                    GameConstants.Player.FRAME_WIDTH * GameConstants.Player.SCALE_MULTIPLIER,
                    GameConstants.Player.FRAME_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER);

        // Commit the drawing to the screen
        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        updateAnimation();

        // Apply gravity
        playerVelocityY += GRAVITY;

        // Handle input movement
        float moveSpeed = 10;
        playerVelocityX = 0;

        if (moveLeft) {
            playerVelocityX = -moveSpeed;
        }
        if (moveRight) {
            playerVelocityX = moveSpeed;
        }

        // Predict next position
        float nextX = playerX + playerVelocityX;
        float nextY = playerY + playerVelocityY;

        // Handle collisions based on predicted position
        checkPlayerCollision(nextX, nextY);

        mapManager.updateCamera(playerX);

        // Clamp player to within map bounds (optional, safety)
        int mapPixelWidth = mapManager.getCurrentMap().getArrayWidth() * GameConstants.FloorTile.WIDTH;
        playerX = Math.max(0, Math.min(playerX, mapPixelWidth - GameConstants.Player.WIDTH));

        // Clamp vertical position to screen
        if (playerY + GameConstants.Player.HEIGHT >= screenHeight) {
            playerY = screenHeight - GameConstants.Player.HEIGHT;
            playerVelocityY = 0;
            isJumping = false;
        }
    }


    private void updateAnimation() {
        animationTick++;

        if (animationTick >= animationSpeed) {
            animationTick = 0;

            if (moveLeft || moveRight) {
                animationFrame = (animationFrame + 1) % 4;

                if (animationFrame < 3) {
                    playerAnimationIndexY = 0;
                    playerAnimationIndexX = animationFrame + 1; // X = 1 to 3
                } else {
                    playerAnimationIndexY = 1;
                    playerAnimationIndexX = 0;
                }
            } else {
                animationFrame = 0;
                playerAnimationIndexX = 0;
                playerAnimationIndexY = 0;
            }


            // not yet implemented
//            if (playerFaceDirection == GameConstants.Facing_Direction.LEFT) {
//                playerAnimationIndexY = 0;
//            } else if (playerFaceDirection == GameConstants.Facing_Direction.RIGHT) {
//                playerAnimationIndexY = 1;

            gruntTwoAnimationIndexY++;

            if (gruntTwoAnimationIndexY >= 58) {
                gruntTwoAnimationIndexY = 0;
            }
        }
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }

    public void setJump(boolean moveJump) {
        if (!isJumping) {
            playerVelocityY = JUMP_STRENGTH;
            isJumping = true;
        }
    }

    public void checkPlayerCollision(float nextX, float nextY) {
        GameMap map = mapManager.getCurrentMap();
        int mapOffsetY = mapManager.getMapOffsetY();

        int playerWidth = GameConstants.Player.WIDTH;
        int playerHeight = GameConstants.Player.HEIGHT;

        // --- Horizontal Collision ---
        boolean canMoveHorizontally =
                !map.isSolidTileAt(nextX, playerY, mapOffsetY) &&
                        !map.isSolidTileAt(nextX + playerWidth - 1, playerY, mapOffsetY) &&
                        !map.isSolidTileAt(nextX, playerY + playerHeight - 1, mapOffsetY) &&
                        !map.isSolidTileAt(nextX + playerWidth - 1, playerY + playerHeight - 1, mapOffsetY);

        if (canMoveHorizontally) {
            playerX = nextX;
        } else {
            playerVelocityX = 0;
        }

        // --- Vertical Collision ---
        boolean canMoveVertically =
                !map.isSolidTileAt(playerX, nextY + playerHeight - 1, mapOffsetY) &&
                        !map.isSolidTileAt(playerX + playerWidth - 1, nextY + playerHeight - 1, mapOffsetY);

        if (canMoveVertically) {
            playerY = nextY;
        } else {
            // Snap to tile grid
            int tileY = (int) ((nextY + playerHeight - mapOffsetY) / GameConstants.FloorTile.HEIGHT);
            playerY = tileY * GameConstants.FloorTile.HEIGHT + mapOffsetY - playerHeight;
            playerVelocityY = 0;
            isJumping = false;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { // Part of View, which SurfaceView extends and GamePanel extends SurfaceView
        return touchEvents.touchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startGameLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;
    }
}
