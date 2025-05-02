package com.example.epicpixelplatformershootergame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.epicpixelplatformershootergame.entities.GameCharacters;
import com.example.epicpixelplatformershootergame.helper.GameConstants;
import com.example.epicpixelplatformershootergame.inputs.TouchEvents;

import java.util.Random;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint();
    private SurfaceHolder holder;

    private Random rand = new Random();
    private Debug debug;

    private GameLoop gameLoop;
    private TouchEvents touchEvents;

    private int playerAnimationIndexX, playerAnimationIndexY = 0, playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    private int gruntTwoAnimationIndexY;

    private float x, y;
    private boolean moveLeft = false, moveRight = false;
    private int screenWidth, screenHeight;
    private int animationFrame;

    private int animationTick;
    private double animationSpeed = 9.5;

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);
        debug = new Debug(); // Create instance for debug mode
        debug.setDebugMode(true); // sets debug mode

        gameLoop = new GameLoop(this);
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        touchEvents.draw(c);

        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX), x, y, null);
        c.drawBitmap(GameCharacters.GRUNTTWO.getSprite(gruntTwoAnimationIndexY, 0), 800, 500, null);

        c.drawBitmap(GameCharacters.PLAYER.getSprite(0, 1), 900, 400, null);
        c.drawBitmap(GameCharacters.PLAYER.getSprite(0, 2), 1200, 400, null);
        c.drawBitmap(GameCharacters.PLAYER.getSprite(0, 3), 1500, 400, null);
        c.drawBitmap(GameCharacters.PLAYER.getSprite(1, 0), 1800, 400, null);

        if (debug.isDebugMode())
            debug.drawDebug(c, x, y, 32 * 10, 48 * 10);


        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        c.drawRect(900, 400, 900 + 320, 400 + 480, paint);  // Weitere Player-Sprites
        c.drawRect(1200, 400, 1200 + 320, 400 + 480, paint);
        c.drawRect(1500, 400, 1500 + 320, 400 + 480, paint);
        c.drawRect(1800, 400, 1800 + 320, 400 + 480, paint);

        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        updateAnimation();

        if (moveLeft) {
            if (x >= 0)
                x -= 10; // speed

            //  playerFaceDirection = GameConstants.Facing_Direction.LEFT; // not yet implemented
            //  playerAnimationIndexY = 0; // not yet implemented
        } else if (moveRight) {
            int spriteWidth = GameCharacters.PLAYER.getSprite(0, 0).getWidth();
            if (x + spriteWidth < screenWidth) {
                x += 10; // speed

                //     playerFaceDirection = GameConstants.Facing_Direction.RIGHT; // not yet implemented
                //       playerAnimationIndexY = 1; // not yet implemented
            }
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


}
