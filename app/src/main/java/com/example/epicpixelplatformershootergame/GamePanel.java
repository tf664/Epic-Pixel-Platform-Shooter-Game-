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
    private float x, y;
    private boolean moveLeft = false, moveRight = false;
    private int screenWidth, screenHeight;
    private Random rand = new Random();
    private GameLoop gameLoop;
    private TouchEvents touchEvents;


    private int playerAnimationIndexX, playerAnimationIndexY = 0, playerFaceDirection = GameConstants.Facing_Direction.RIGHT;
    private int gruntTwoAnimationIndexY;

    private int animationTick;
    private double animationSpeed = 9.5;

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);
        touchEvents = new TouchEvents(this);

        gameLoop = new GameLoop(this);
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        touchEvents.draw(c);

        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX), x, y, null);
        c.drawBitmap(GameCharacters.GRUNTTWO.getSprite(gruntTwoAnimationIndexY, 0), 400, 500, null);

        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        updateAnimation();

        if (moveLeft) {
            if (x >= 0)
                x -= 10; // speed

            playerFaceDirection = GameConstants.Facing_Direction.LEFT; // not yet implemented
            playerAnimationIndexY = 0; // not yet implemented
        } else if (moveRight) {
            int spriteWidth = GameCharacters.PLAYER.getSprite(0, 0).getWidth();
            if (x + spriteWidth < screenWidth) {
                x += 10; // speed

                playerFaceDirection = GameConstants.Facing_Direction.RIGHT; // not yet implemented
                playerAnimationIndexY = 1; // not yet implemented
            }
        }
    }


        private void updateAnimation() {
            animationTick++;

            if (animationTick >= animationSpeed) {
                animationTick = 0;
                playerAnimationIndexX++;

                if (playerAnimationIndexX >= 4) {
                    playerAnimationIndexX = 1;

                    playerAnimationIndexY = 1;
                }

                if (playerAnimationIndexX >= 3 && playerAnimationIndexY >= 1) {
                    playerAnimationIndexX = 1;
                    playerAnimationIndexY = 0;
                }

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
        public boolean onTouchEvent (MotionEvent event)
        { // Part of View, which SurfaceView extends and GamePanel extends SurfaceView
            return touchEvents.touchEvent(event);
        }

        @Override
        public void surfaceCreated (@NonNull SurfaceHolder surfaceHolder){
            gameLoop.startGameLoop();
        }

        @Override
        public void surfaceChanged (@NonNull SurfaceHolder holder,int format, int width, int height)
        {

        }

        @Override
        public void surfaceDestroyed (@NonNull SurfaceHolder holder){

        }

        @Override
        protected void onSizeChanged ( int w, int h, int oldw, int oldh){
            super.onSizeChanged(w, h, oldw, oldh);
            screenWidth = w;
            screenHeight = h;
        }


        public void setMoveLeft ( boolean moveLeft){
            this.moveLeft = moveLeft;
        }

        public void setMoveRight ( boolean moveRight){
            this.moveRight = moveRight;
        }


    }
