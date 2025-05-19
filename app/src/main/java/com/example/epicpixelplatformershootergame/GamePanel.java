package com.example.epicpixelplatformershootergame;

import static com.example.epicpixelplatformershootergame.MainActivity.GAME_HEIGHT;
import static com.example.epicpixelplatformershootergame.MainActivity.GAME_WIDTH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.epicpixelplatformershootergame.entities.GameCharacters;
import com.example.epicpixelplatformershootergame.environments.GameMap;
import com.example.epicpixelplatformershootergame.environments.MapManager;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.inputs.TouchEvents;

import java.util.ArrayList;
import java.util.List;
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
    private int animationFrame;

    private int animationTick;
    private double animationSpeed = 7.5;

    // Map
    private MapManager mapManager;
    private Bitmap background_back;
    private Bitmap background_front;
    private Bitmap cachedBackground;

    // Jumping Physics
    private float playerX = 100, playerY = 100;
    private float playerVelocityX = 0, playerVelocityY = 0;
    private boolean isJumping = false;

    private final float GRAVITY = 0.5f;
    private final float JUMP_STRENGTH = -16;

    private final List<Rect> collisionRects = new ArrayList<>();

    public GamePanel(Context context) {
        super(context);
        Debug.setDebugMode(GameConstants.DebugMode.debugMode);

        setZOrderOnTop(true);
        getHolder().setFormat(android.graphics.PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        loadBackgrounds(context);

        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);

        gameLoop = new GameLoop(this);
        mapManager = new MapManager();
    }

    public void render() {
        SurfaceHolder surfaceHolder = getHolder();
        Canvas c = surfaceHolder.lockCanvas();
        if (c == null) return;

        c.drawColor(Color.BLACK);
        c.drawBitmap(background_back, 0, 0, null);
        // background_front scroll
        if (background_front != null) {
            int cameraX = mapManager.getCameraX();
            int bgWidth = background_front.getWidth();
            int offset = cameraX % bgWidth;

            for (int x = -offset; x < getWidth(); x += bgWidth)
                c.drawBitmap(background_front, x, 0, null);
        }

        // Camera and Map
        mapManager.updateCamera(playerX);
        mapManager.draw(c);

        int mapOffsetY = mapManager.getMapOffsetY();
        int cameraX = mapManager.getCameraX();

        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX),
                playerX - cameraX, playerY + mapOffsetY, null);

        c.drawBitmap(GameCharacters.GRUNTTWO.getSprite(gruntTwoAnimationIndexY, 0), 800, 600, null);

        // Buttons
        touchEvents.draw(c);

        if (Debug.isDebugMode()) {
            float collisionOffsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
            float collisionOffsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
            Debug.drawDebugPlayer(
                    c,
                    playerX - cameraX + collisionOffsetX,
                    playerY + mapOffsetY + collisionOffsetY,
                    GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER,
                    GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER
            );
        }

        surfaceHolder.unlockCanvasAndPost(c);
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

        // Clamp player's nextX to map bounds before collision
        int mapPixelWidth = mapManager.getCurrentMap().getArrayWidth() * GameConstants.FloorTile.WIDTH;
        nextX = Math.max(0, Math.min(nextX, mapPixelWidth - GameConstants.Player.WIDTH));

        // Handle collisions based on predicted position
        checkPlayerCollision(nextX, nextY);

        // Update camera after finalized player position
        mapManager.updateCamera(playerX);

        // Clamp vertical position to screen
        if (playerY + GameConstants.Player.HEIGHT >= GameConstants.Screen.screenHeight) {
            playerY = GameConstants.Screen.screenHeight - GameConstants.Player.HEIGHT;
            playerVelocityY = 0;
            isJumping = false;
        }
    }

    private void updateAnimation() {
        animationTick++;

        if (animationTick >= animationSpeed) {
            animationTick = 0;

            if (moveRight) {
                playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
                // Walking right animation sequence
                int[] rightAnimY = {0, 0, 0, 1}; // rows
                int[] rightAnimX = {1, 2, 3, 0}; // columns

                animationFrame = (animationFrame + 1) % rightAnimX.length;
                playerAnimationIndexY = rightAnimY[animationFrame];
                playerAnimationIndexX = rightAnimX[animationFrame];
            } else if (moveLeft) {
                playerFaceDirection = GameConstants.Facing_Direction.LEFT;
                // Walking left animation sequence
                int[] leftAnimY = {1, 1, 2, 2}; // rows
                int[] leftAnimX = {2, 3, 0, 1}; // columns

                animationFrame = (animationFrame + 1) % leftAnimX.length;
                playerAnimationIndexY = leftAnimY[animationFrame];
                playerAnimationIndexX = leftAnimX[animationFrame];
                // Standing
            } else if (playerFaceDirection == GameConstants.Facing_Direction.RIGHT) {
                playerAnimationIndexY = 0;
                playerAnimationIndexX = 0;
            } else {
                playerAnimationIndexY = 1;
                playerAnimationIndexX = 1;


                // GruntTwo animation
                gruntTwoAnimationIndexY++;
                if (gruntTwoAnimationIndexY >= 58) {
                    gruntTwoAnimationIndexY = 0;
                }
            }
        }
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
        playerFaceDirection = GameConstants.Facing_Direction.LEFT;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
        playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    }

    public void setJump(boolean moveJump) {
        if (!isJumping) {
            playerVelocityY = JUMP_STRENGTH;
            isJumping = true;
        }
    }


    public void checkPlayerCollision(float nextX, float nextY) {
        GameMap map = mapManager.getCurrentMap();
        float collisionOffsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
        float collisionOffsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
        int playerWidth = GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER;
        int playerHeight = GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER;

        if (Debug.isDebugMode())
            collisionRects.clear();

        // --- Horizontal Collision ---
        boolean canMoveHorizontally = true;
        float[] testXs = {nextX + collisionOffsetX, nextX + collisionOffsetX + playerWidth - 1};
        float[] testYs = {playerY + collisionOffsetY, playerY + collisionOffsetY + playerHeight - 1};
        for (float tx : testXs) {
            for (float ty : testYs) {
                if (map.isSolidTileAt(tx, ty)) {
                    canMoveHorizontally = false;
                    if (Debug.isDebugMode()) {
                        Rect tileRect = getTileRectAtWorld(tx, ty);
                        collisionRects.add(tileRect);
                    }
                }
            }
        }
        if (canMoveHorizontally)
            playerX = nextX;
        else
            playerVelocityX = 0;

        // --- Vertical Collision ---
        boolean canMoveVertically = true;
        testXs = new float[]{playerX + collisionOffsetX, playerX + collisionOffsetX + playerWidth - 1};
        testYs = new float[]{nextY + collisionOffsetY + playerHeight - 1};
        for (float tx : testXs) {
            for (float ty : testYs) {
                if (map.isSolidTileAt(tx, ty)) {
                    canMoveVertically = false;
                    if (Debug.isDebugMode()) {
                        Rect tileRect = getTileRectAtWorld(tx, ty);
                        collisionRects.add(tileRect);
                    }
                }
            }
        }
        if (canMoveVertically) {
            playerY = nextY;
        } else {
            // Snap the collision box bottom to the top of the tile
            int tileY = (int) ((nextY + collisionOffsetY + playerHeight - 1) / GameConstants.FloorTile.HEIGHT);
            float tileTop = tileY * GameConstants.FloorTile.HEIGHT;
            playerY = tileTop - collisionOffsetY - playerHeight;
            playerVelocityY = 0;
            isJumping = false;
        }
    }

    // Helper to get the rectangle of a tile at a world position
    private Rect getTileRectAtWorld(float worldX, float worldY) {
        int tileWidth = GameConstants.FloorTile.WIDTH;
        int tileHeight = GameConstants.FloorTile.HEIGHT;
        int tileX = (int) (worldX / tileWidth);
        int tileY = (int) (worldY / tileHeight);
        int drawX = tileX * tileWidth - mapManager.getCameraX();
        int drawY = tileY * tileHeight + mapManager.getMapOffsetY(); // <-- Correct
        return new Rect(drawX, drawY, drawX + tileWidth, drawY + tileHeight);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { // Part of View, which SurfaceView extends and GamePanel extends SurfaceView
        return touchEvents.touchEvent(event);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        GameConstants.Screen.screenWidth = getWidth();
        GameConstants.Screen.screenHeight = getHeight();
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

        GameConstants.Screen.screenWidth = w;
        GameConstants.Screen.screenHeight = h;

        GameConstants.Camera.leftThreshold = w / 3;
        GameConstants.Camera.rightThreshold = w * 2 / 3;

        int mapRows = mapManager.getCurrentMap().getArrayHeight();
        int scale = h / (mapRows * GameConstants.FloorTile.BASE_HEIGHT);
        if (scale < 1) scale = 1;

        GameConstants.FloorTile.SCALE_MULTIPLIER = scale;
        GameConstants.FloorTile.WIDTH = GameConstants.FloorTile.BASE_WIDTH * scale;
        GameConstants.FloorTile.HEIGHT = GameConstants.FloorTile.BASE_HEIGHT * scale;

        mapManager.updateScreenSize(w, h);

        if (background_back != null) {
            background_back = Bitmap.createScaledBitmap(background_back, w, h, true);
        }
    }

    private void loadBackgrounds(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        background_back = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_back, options);
        background_back = Bitmap.createScaledBitmap(background_back, GameConstants.Screen.screenWidth,
                GameConstants.Screen.screenHeight, true);
        background_front = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_front, options);
        background_front = Bitmap.createScaledBitmap(background_front, GAME_WIDTH, GAME_HEIGHT, true);
    }
}

