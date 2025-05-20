package com.example.epicpixelplatformershootergame;

import static com.example.epicpixelplatformershootergame.MainActivity.GAME_HEIGHT;
import static com.example.epicpixelplatformershootergame.MainActivity.GAME_WIDTH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.epicpixelplatformershootergame.entities.Enemy;
import com.example.epicpixelplatformershootergame.entities.GameCharacters;
import com.example.epicpixelplatformershootergame.environments.MapManager;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.inputs.TouchEvents;
import com.example.epicpixelplatformershootergame.physics.PlayerCollisionHandler;

import java.util.ArrayList;
import java.util.List;

// --- Constructor ---
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint(); // change
    private GameLoop gameLoop;
    private TouchEvents touchEvents;

    private int playerAnimationIndexX, playerAnimationIndexY;
    private int playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    private int gruntTwoAnimationIndexY;
    private List<Enemy> enemies = new ArrayList<>();

    private boolean moveLeft = false, moveRight = false;
    private int playerAnimationFrame;
    private int animationTick;
    private double animationSpeed = GameConstants.Physics.ANIMATION_SPEED;

    // Map
    private MapManager mapManager;
    private Bitmap background_back;
    private Bitmap background_front;

    // Jumping Physics
    private float playerX = 100, playerY = 100; // Spawn playe // TODO hardcoded
    private float playerVelocityX = 0, playerVelocityY = 0;
    private boolean isJumping = false;
    private boolean jumpButtonHeld = false;

    private final float GRAVITY = GameConstants.Physics.GRAVITY;
    private final float JUMP_STRENGTH = GameConstants.Physics.JUMP_STRENGTH;


    private final PlayerCollisionHandler collisionHandler = new PlayerCollisionHandler();

    public GamePanel(Context context) {
        super(context);
        Debug.setDebugMode(GameConstants.DebugMode.DEBUG_MODE);

        setZOrderOnTop(true);
        getHolder().setFormat(android.graphics.PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
        loadBackgrounds(context);

        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);

        gameLoop = new GameLoop(this);
        mapManager = new MapManager();

        enemies.add(new Enemy(1000, 330)); // TODO hardcoded
        enemies.add(new Enemy(1500, 330)); // TODO hardcoded
    }


    //--- SurfaceView/SurfaceHolder/Android lifecycle methods ---
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        GameConstants.Screen.SCREENWIDTH = getWidth();
        GameConstants.Screen.SCREENHEIGHT = getHeight();
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

        GameConstants.Screen.SCREENWIDTH = w;
        GameConstants.Screen.SCREENHEIGHT = h;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) { // Part of View, which SurfaceView extends and GamePanel extends SurfaceView
        return touchEvents.touchEvent(event);
    }


    // --- Game Loop ---
    public void render() {
        SurfaceHolder surfaceHolder = getHolder();
        Canvas c = surfaceHolder.lockCanvas();
        if (c == null) return;

        drawBackground(c);
        mapManager.updateCamera(playerX);
        mapManager.draw(c);
        drawPlayer(c);
        drawEnemies(c);
        touchEvents.draw(c);
        drawDebug(c);

        surfaceHolder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        tryConsumeJumpBuffer();
        updateAnimation();
        applyGravity();
        handleMovement();

        float nextX = clampPlayerPosition(playerX + playerVelocityX);
        float nextY = playerY + playerVelocityY;

        // Update all enemies (patrol logic)
        for (Enemy enemy : enemies) {
            float leftBound = enemy.spawnX - 100; // TODO left bounds
            float rightBound = enemy.spawnX + 100; // TODO right bounds
            enemy.update(leftBound, rightBound);
        }

        checkPlayerCollision(nextX, nextY);
        mapManager.updateCamera(playerX);

        if (playerY + GameConstants.Player.HEIGHT >= GameConstants.Screen.SCREENHEIGHT) {
            playerY = GameConstants.Screen.SCREENHEIGHT - GameConstants.Player.HEIGHT;
            playerVelocityY = 0;
            isJumping = false;
        }
    }


    //--- Movement methods ---
    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
        playerFaceDirection = GameConstants.Facing_Direction.LEFT;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
        playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    }

    public void setJump(boolean isJumping) {
        if (!this.isJumping && isJumping) {
            playerVelocityY = JUMP_STRENGTH;
            this.isJumping = true;
        }
    }

    public void setJumpButtonHeld(boolean held) {
        this.jumpButtonHeld = held;
    }

    // --- Animation methods ---
    private void updateAnimation() {
        animationTick++;

        if (animationTick >= animationSpeed) {
            animationTick = 0;

            if (moveRight) {
                playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
                setPlayerAnimationRight();
            } else if (moveLeft) {
                playerFaceDirection = GameConstants.Facing_Direction.LEFT;
                setPlayerAnimationLeft();
            } else
                setPlayerAnimationIdle();

            // Grunt Two Animation
            gruntTwoAnimationIndexY++;
            if (gruntTwoAnimationIndexY >= 58)
                gruntTwoAnimationIndexY = 0;
        }
    }

    private void setPlayerAnimationRight() {
        int[] rightAnimY = {0, 0, 0, 1};
        int[] rightAnimX = {1, 2, 3, 0};
        playerAnimationFrame = (playerAnimationFrame + 1) % rightAnimX.length;
        playerAnimationIndexY = rightAnimY[playerAnimationFrame];
        playerAnimationIndexX = rightAnimX[playerAnimationFrame];
    }

    private void setPlayerAnimationLeft() {
        int[] leftAnimY = {1, 1, 2, 2};
        int[] leftAnimX = {2, 3, 0, 1};
        playerAnimationFrame = (playerAnimationFrame + 1) % leftAnimX.length;
        playerAnimationIndexY = leftAnimY[playerAnimationFrame];
        playerAnimationIndexX = leftAnimX[playerAnimationFrame];
    }

    private void setPlayerAnimationIdle() {
        if (playerFaceDirection == GameConstants.Facing_Direction.RIGHT) {
            playerAnimationIndexY = 0;
            playerAnimationIndexX = 0;
        } else {
            playerAnimationIndexY = 1;
            playerAnimationIndexX = 1;
        }
    }


    // --- Drawing methods ---
    private void drawBackground(Canvas c) {
        c.drawColor(Color.BLACK);
        c.drawBitmap(background_back, 0, 0, null);
        int cameraX = mapManager.getCameraX();
        int bgWidth = background_front.getWidth();
        int offset = cameraX % bgWidth;
        for (int x = -offset; x < getWidth(); x += bgWidth)
            c.drawBitmap(background_front, x, 0, null);
    }

    private void drawPlayer(Canvas c) {
        int mapOffsetY = mapManager.getMapOffsetY();
        int cameraX = mapManager.getCameraX();
        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX),
                playerX - cameraX, playerY + mapOffsetY, null);
    }

    private void drawEnemies(Canvas c) {
        int cameraX = mapManager.getCameraX();
        int mapOffsetY = mapManager.getMapOffsetY();
        for (Enemy enemy : enemies) {
            c.drawBitmap(
                    GameCharacters.GRUNTTWO.getSprite(gruntTwoAnimationIndexY, 0),
                    enemy.x - cameraX,
                    enemy.y + mapOffsetY,
                    null
            );
        }
    }

    private void drawDebug(Canvas c) {
        if (Debug.isDebugMode()) {
            float collisionOffsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
            float collisionOffsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
            Debug.drawDebugPlayer(
                    c,
                    playerX - mapManager.getCameraX() + collisionOffsetX,
                    playerY + mapManager.getMapOffsetY() + collisionOffsetY,
                    GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER,
                    GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER
            );
        }
    }


    // --- Collision detection ---
    public void checkPlayerCollision(float nextX, float nextY) {
        PlayerCollisionHandler.PlayerCollisionResult collisionResult = collisionHandler.checkCollision(
                mapManager.getCurrentMap(), playerY,
                nextX, nextY,
                playerVelocityX, playerVelocityY);
        playerX = collisionResult.x;
        playerY = collisionResult.y;
        playerVelocityX = collisionResult.velocityX;
        playerVelocityY = collisionResult.velocityY;
        isJumping = collisionResult.isJumping;
    }

    // --- private resource loading
    private void loadBackgrounds(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        background_back = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_back, options);
        background_back = Bitmap.createScaledBitmap(background_back, GameConstants.Screen.SCREENWIDTH,
                GameConstants.Screen.SCREENHEIGHT, true);
        background_front = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_front, options);
        background_front = Bitmap.createScaledBitmap(background_front, GAME_WIDTH, GAME_HEIGHT, true);
    }


    // --- private physics helpers ---
    private void applyGravity() {
        if (playerVelocityY < 0 && jumpButtonHeld)
            playerVelocityY += GRAVITY * 0.4f; // Less gravity while holding jump
        else
            playerVelocityY += GRAVITY; // Normal gravity

    }

    private void handleMovement() {
        float moveSpeed = GameConstants.Physics.PLAYER_MOVE_SPEED;
        playerVelocityX = 0;
        if (moveLeft)
            playerVelocityX = -moveSpeed;
        else if (moveRight)
            playerVelocityX = moveSpeed;
    }

    private float clampPlayerPosition(float nextX) {
        int mapPixelWidth = mapManager.getCurrentMap().getArrayWidth() * GameConstants.FloorTile.WIDTH;
        return Math.max(0, Math.min(nextX, mapPixelWidth - GameConstants.Player.WIDTH));
    }

    private void tryConsumeJumpBuffer() {
        if (!isJumping && touchEvents.hasBufferedJump()) {
            playerVelocityY = JUMP_STRENGTH;
            isJumping = true;
            touchEvents.clearJumpBuffer();
        }

    }
}

