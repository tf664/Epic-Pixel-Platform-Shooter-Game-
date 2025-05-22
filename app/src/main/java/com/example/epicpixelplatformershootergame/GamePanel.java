package com.example.epicpixelplatformershootergame;

import static com.example.epicpixelplatformershootergame.MainActivity.GAME_HEIGHT;
import static com.example.epicpixelplatformershootergame.MainActivity.GAME_WIDTH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.epicpixelplatformershootergame.entities.Bullet;
import com.example.epicpixelplatformershootergame.entities.Enemy;
import com.example.epicpixelplatformershootergame.entities.GameEntityAssets;
import com.example.epicpixelplatformershootergame.entities.Player;
import com.example.epicpixelplatformershootergame.environments.MapManager;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.inputs.TouchEvents;
import com.example.epicpixelplatformershootergame.physics.PlayerCollisionHandler;

import java.util.ArrayList;
import java.util.List;

// --- Constructor ---
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint(); // TODO change
    private GameLoop gameLoop;
    private TouchEvents touchEvents;

    private int gruntTwoAnimationIndexY;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<Bullet> enemyBullets = new ArrayList<>();


    private GameState gameState;

    // Map
    private MapManager mapManager;
    private Bitmap background_back;
    private Bitmap background_front;

    private final PlayerCollisionHandler collisionHandler = new PlayerCollisionHandler();

    private Player player;

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

        Bitmap btnUnpressed = BitmapFactory.decodeResource(getResources(), R.drawable.restartbutton_unpressed);
        Bitmap btnPressed = BitmapFactory.decodeResource(getResources(), R.drawable.restartbutton_pressed);
        Typeface pixelFont = Typeface.createFromAsset(context.getAssets(), "fonts/VT323-Regular.ttf");
        gameState = new GameState(btnUnpressed, btnPressed, pixelFont, context);

        player = new Player(touchEvents, mapManager, bullets, gameState);
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
        GameConstants.FloorTile.WIDTH = GameConstants.FloorTile.BASE_WIDTH * GameConstants.FloorTile.SCALE_MULTIPLIER;
        GameConstants.FloorTile.HEIGHT = GameConstants.FloorTile.BASE_HEIGHT * GameConstants.FloorTile.SCALE_MULTIPLIER;

        mapManager.updateScreenSize(w, h);

        if (background_back != null)
            background_back = Bitmap.createScaledBitmap(background_back, w, h, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameState.getState() == GameState.State.GAME_OVER) {
            int btnWidth = gameState.getRestartButtonBitmapUnpressed().getWidth();
            int btnHeight = gameState.getRestartButtonBitmapUnpressed().getHeight();
            int btnX = GameConstants.Screen.SCREENWIDTH / 2 - btnWidth / 2;
            int btnY = (GameConstants.Screen.SCREENHEIGHT / 2 + 50) - 500;

            float x = event.getX();
            float y = event.getY();

            boolean inside = x >= btnX && x <= btnX + btnWidth && y >= btnY && y <= btnY + btnHeight;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (inside) {
                        gameState.setRestartButtonPressed(true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (gameState.isRestartButtonPressed() && inside) {
                        gameState.reset();

                        resetGameObjects();
                    }
                    gameState.setRestartButtonPressed(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    gameState.setRestartButtonPressed(false);
                    break;
            }
            return true;
        }
        if (gameState.getState() == GameState.State.RUNNING) {
            return touchEvents.touchEvent(event);
        }
        return true;
    }


    // --- Game Loop ---
    public void render() {
        SurfaceHolder surfaceHolder = getHolder();
        Canvas c = surfaceHolder.lockCanvas();
        if (c == null) return;

        drawBackground(c);
        mapManager.draw(c);
        player.drawPlayer(c);
        drawEnemies(c);

        drawBullets(c);
        drawEnemyBullets(c);

        // UI elements
        gameState.drawTimer(c);
        gameState.drawGunAndAmmo(c);
        drawPlayerHealthBar(c);
        drawDebug(c);
        touchEvents.draw(c);

        if (gameState.getState() == GameState.State.GAME_OVER) {
            gameState.drawGameOverScreen(c);
        }

        surfaceHolder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        if (gameState.getState() == GameState.State.GAME_OVER) {
            resetGameObjects();
            return;
        }
        // Handle player input and animation
        player.tryConsumeJumpBuffer();
        player.updateAnimation();
        player.applyGravity();
        player.handleMovement();
        // Calculate next player position
        float nextX = player.clampPlayerPosition(player.playerX + player.playerVelocityX);
        float nextY = player.playerY + player.playerVelocityY;

        // Update enemies
        for (Enemy enemy : enemies) {
            enemy.updatePatrol();
            enemy.updateAnimation();
            float playerCenterX = player.playerX + GameConstants.Player.WIDTH / 2f;
            float playerCenterY = player.playerY + GameConstants.Player.HEIGHT / 2f;
            enemy.tryShoot(enemyBullets, playerCenterX, playerCenterY);
        }

        // Update bullets
        for (Bullet bullet : bullets) bullet.update();
        for (Bullet bullet : enemyBullets) bullet.update();

        checkPlayerCollision(nextX, nextY);

        // Remove inactive bullets and dead enemies
        bullets.removeIf(b -> !b.active);
        enemyBullets.removeIf(b -> !b.active);
        enemies.removeIf(e -> !e.isAlive());

        // Handle shooting animation and input buffer
        if (!player.isShooting && touchEvents.hasBufferedShoot()) {
            player.isShooting = true;
            player.playerAnimationFrame = 0;
            touchEvents.clearShootBuffer();
            player.setPlayerShootingAnimation();
        } else if (player.isShooting && touchEvents.hasBufferedShoot()) {
            player.pendingShoot = true;
            touchEvents.clearShootBuffer();
        }

        // Bullet-enemy collision
        for (Bullet bullet : bullets) {
            for (Enemy enemy : enemies) {
                if (bullet.active && enemy.isAlive() && checkBulletPenetration(bullet, enemy)) {
                    enemy.takeDamage(1);
                    bullet.active = false;
                }
            }
        }
        // Bullet-player collision
        for (Bullet bullet : enemyBullets) {
            if (bullet.active && checkPlayerHitByEnemyBullet(bullet)) {
                player.takeDamage(1);
                bullet.active = false;
            }
        }

        // Update camera and game state
        mapManager.updateCamera(player.playerX);

        if (GameConstants.Player.HEALTH <= 0 && gameState.getState() != GameState.State.GAME_OVER) {
            gameState.setGameOver();
        }

        gameState.updateTimer();
    }

    private boolean checkBulletPenetration(Bullet bullet, Enemy enemy) {
        float bulletRadius = 10;

        float bulletLeft = bullet.x - bulletRadius;
        float bulletRight = bullet.x + bulletRadius;
        float bulletTop = bullet.y - bulletRadius;
        float bulletBottom = bullet.y + bulletRadius;

        RectF enemyRect = getScaledCollisionRect(enemy);

        return bulletRight > enemyRect.left && bulletLeft < enemyRect.right &&
                bulletBottom > enemyRect.top && bulletTop < enemyRect.bottom;
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

    private void drawEnemies(Canvas c) {
        int cameraX = mapManager.getCameraX();
        int mapOffsetY = mapManager.getMapOffsetY();
        for (Enemy enemy : enemies) {
            int spriteColumn = (enemy.direction == 1) ? 0 : 1;
            Bitmap sprite = GameEntityAssets.GRUNTTWO.getSprite(enemy.animFrame, spriteColumn);
            float drawX;
            if (spriteColumn == GameConstants.Facing_Direction.RIGHT) {
                drawX = enemy.x - cameraX - 2 * GameConstants.GruntTwo.SCALE_MULTIPLIER; // 2 = pivot point for facing right
            } else {
                drawX = enemy.x - cameraX - 79 * GameConstants.GruntTwo.SCALE_MULTIPLIER; // 79 = pivot point for facing left
            }
            c.drawBitmap(sprite, drawX, enemy.y + mapOffsetY, null);
        }
    }

    private void drawBullets(Canvas c) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            if (bullet.active)
                c.drawCircle(bullet.x - mapManager.getCameraX(), bullet.y + mapManager.getMapOffsetY(), 10, paint);
        }
    }

    private void drawEnemyBullets(Canvas c) {
        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        for (Bullet bullet : enemyBullets) {
            if (bullet.active)
                c.drawCircle(bullet.x - mapManager.getCameraX(), bullet.y + mapManager.getMapOffsetY(), 10, paint);
        }
    }

    private void drawPlayerHealthBar(Canvas c) {
        int maxHealth = GameConstants.Player.TOTAL_HEALTH;
        int currentHealth = GameConstants.Player.HEALTH;
        int barWidth = 100; // TODO move design to GameConstants
        int barHeight = 16;
        int barX = (int) (player.playerX - mapManager.getCameraX() + GameConstants.Player.WIDTH / 2 - barWidth / 2);
        int barY = (int) (player.playerY + mapManager.getMapOffsetY() - 30);

        // Background
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.DKGRAY);
        c.drawRect(barX, barY, barX + barWidth, barY + barHeight, bgPaint);

        // Health
        Paint healthPaint = new Paint();
        healthPaint.setColor(Color.GREEN);
        int healthWidth = (int) (barWidth * (currentHealth / (float) maxHealth));
        c.drawRect(barX, barY, barX + healthWidth, barY + barHeight, healthPaint);

        // Border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(2);
        c.drawRect(barX, barY, barX + barWidth, barY + barHeight, borderPaint);
    }

    private void drawDebug(Canvas c) {
        if (Debug.isDebugMode()) {
            float collisionOffsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
            float collisionOffsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
            Debug.drawDebugPlayer(
                    c,
                    player.playerX - mapManager.getCameraX() + collisionOffsetX,
                    player.playerY + mapManager.getMapOffsetY() + collisionOffsetY,
                    GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER,
                    GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER
            );
            Debug.drawDebugHitAreas(c, enemies, bullets, mapManager.getCameraX(), mapManager.getMapOffsetY());
        }
    }


    // --- Collision detection ---
    public void checkPlayerCollision(float nextX, float nextY) {
        PlayerCollisionHandler.PlayerCollisionResult collisionResult = collisionHandler.checkCollision(
                mapManager.getCurrentMap(), player.playerY,
                nextX, nextY,
                player.playerVelocityX, player.playerVelocityY);
        player.playerX = collisionResult.x;
        player.playerY = collisionResult.y;
        player.playerVelocityX = collisionResult.velocityX;
        player.playerVelocityY = collisionResult.velocityY;
        player.isJumping = collisionResult.isJumping;
    }

    private boolean checkPlayerHitByEnemyBullet(Bullet bullet) {
        float bulletHitRadius = 6; // TODO GameConstants

        // Use the same collision rect as the debug box
        float collisionOffsetX = GameConstants.getCollisionOffsetX() * GameConstants.Player.SCALE_MULTIPLIER;
        float collisionOffsetY = GameConstants.getCollisionOffsetY() * GameConstants.Player.SCALE_MULTIPLIER;
        float px = player.playerX + collisionOffsetX;
        float py = player.playerY + collisionOffsetY;
        float pw = GameConstants.Player.PLAYER_COLLISION_WIDTH * GameConstants.Player.SCALE_MULTIPLIER;
        float ph = GameConstants.Player.PLAYER_COLLISION_HEIGHT * GameConstants.Player.SCALE_MULTIPLIER;

        float bx = bullet.x;
        float by = bullet.y;

        return bx + bulletHitRadius > px && bx - bulletHitRadius < px + pw &&
                by + bulletHitRadius > py && by - bulletHitRadius < py + ph;
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

    public static RectF getScaledCollisionRect(Enemy enemy) {
        float scale = GameConstants.GruntTwo.SCALE_MULTIPLIER;

        float offsetX = GameConstants.GruntTwo.COLLISION_OFFSET_X * scale;
        float offsetY = GameConstants.GruntTwo.COLLISION_OFFSET_Y * scale;
        float width = GameConstants.GruntTwo.COLLISION_WIDTH * scale;
        float height = GameConstants.GruntTwo.COLLISION_HEIGHT * scale;

        float left = enemy.x + offsetX;
        float top = enemy.y + offsetY;
        return new RectF(left, top, left + width, top + height);
    }

    // --- Setters and getters senders ---
    public void setMoveLeft(boolean moveLeft) {
        if (player != null) {
            player.setMoveLeft(moveLeft);
        }
    }

    public void setMoveRight(boolean moveRight) {
        if (player != null) {
            player.setMoveRight(moveRight);
        }
    }

    public void setJumpButtonHeld(boolean held) {
        if (player != null) {
            player.setJumpButtonHeld(held);
        }
    }

    public Player getPlayer() {
        return player;
    }

    // --- Reset helper ---
    private void resetGameObjects() {
        // Reset player state
        player.playerX = GameConstants.Player.START_X;
        player.playerY = GameConstants.Player.START_Y;
        player.playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
        player.playerVelocityX = 0;
        player.playerVelocityY = 0;
        player.isJumping = false;
        player.moveLeft = false;
        player.moveRight = false;
        player.isShooting = false;
        gameState.reloadAmmo();
        player.pendingShoot = false;
        GameConstants.Player.HEALTH = GameConstants.Player.TOTAL_HEALTH;

        // Reset enemies
        enemies.clear();
        for (int i = 0; i < GameConstants.Enemy.SPAWN_X.length; i++) {
            enemies.add(new Enemy(GameConstants.Enemy.SPAWN_X[i], GameConstants.Enemy.SPAWN_Y[i]));
        }

        // Clear bullets
        bullets.clear();
        enemyBullets.clear();
    }
}

