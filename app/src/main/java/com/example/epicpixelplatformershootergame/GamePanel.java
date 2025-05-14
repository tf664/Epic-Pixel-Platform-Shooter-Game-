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
    private final float JUMP_STRENGTH = -12;


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


        // Step 1: Draw the map tiles (background) with the camera offset
        mapManager.draw(c);

        mapManager.updateCamera(playerX);


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
        playerY += playerVelocityY;

        // ground collision at bottom of the map

        checkPlayerCollision();
        if (playerY + GameConstants.Player.HEIGHT >= screenHeight) {
            playerY = screenHeight - GameConstants.Player.HEIGHT;
            playerVelocityY = 0;
            isJumping = false;
        }

        if (moveLeft && playerX >= 0) {
            playerX -= 10; // speed

            //  playerFaceDirection = GameConstants.Facing_Direction.LEFT; // not yet implemented
            //  playerAnimationIndexY = 0; // not yet implemented
        } else if (moveRight && playerX + GameConstants.Player.WIDTH < screenWidth) {
            playerX += 10; // speed

            //     playerFaceDirection = GameConstants.Facing_Direction.RIGHT; // not yet implemented
            //       playerAnimationIndexY = 1; // not yet implemented
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
//            }

            gruntTwoAnimationIndexY++;

            if (gruntTwoAnimationIndexY >= 58) {
                gruntTwoAnimationIndexY = 0;
            }
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
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

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

    public void checkPlayerCollision() {
        int playerWidth = GameConstants.Player.WIDTH;
        int playerHeight = GameConstants.Player.HEIGHT;
        float nextX = playerX + playerVelocityX;
        float nextY = playerY + playerVelocityY;

        GameMap map = mapManager.getCurrentMap();

        // Horizontal collision
        if (!map.isSolidTileAt(nextX, playerY) && !map.isSolidTileAt(nextX + playerWidth - 1, playerY)) {
            playerX = nextX;
        } else {
            playerVelocityX = 0; // Stop horizontal movement
        }

        // Vertical collision detection
        if (!map.isSolidTileAt(playerX, nextY + playerHeight) &&
                !map.isSolidTileAt(playerX + playerWidth - 1, nextY + playerHeight)) {
            playerY = nextY; // No collision, move player down
        } else {
            // Find the tile Y position where player is landing
            int tileY = (int) ((nextY + playerHeight) / GameConstants.FloorTile.HEIGHT);

            // Adjust player Y to land exactly at the bottom of the tile
            playerY = (int) tileY * GameConstants.FloorTile.HEIGHT - playerHeight;

            // Stop vertical velocity and mark as not jumping
            playerVelocityY = 0;
            isJumping = false;

            System.out.println("Player Y before collision: " + playerY);
            System.out.println("Next Y: " + nextY);
            System.out.println("Tile Y: " + tileY);
            System.out.println("Adjusted Player Y: " + playerY);
        }
    }

}
