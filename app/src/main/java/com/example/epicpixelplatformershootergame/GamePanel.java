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

import java.util.Random;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private Paint redPaint = new Paint();
    private SurfaceHolder holder;
    private float x, y;
    private Random rand = new Random();
    private GameLoop gameLoop;

    private int playerAnimationIndexX, playerAnimationIndexY, playerFaceDirection = GameConstants.Facing_Direction.LEFT;
    private int animationTick;
    private double animationSpeed = 8.5;

    public GamePanel(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        redPaint.setColor(Color.RED);

        gameLoop = new GameLoop(this);
    }

    public void render() {
        Canvas c = holder.lockCanvas();
        c.drawColor(Color.BLACK);

        // c.drawBitmap(GameCharacters.PLAYER.getSpriteSheet(), 500, 500, null);

        // c.drawBitmap(GameCharacters.PLAYER.getSprite(playerFaceDirection, playerAnimationIndexX), x, y, null);
        c.drawBitmap(GameCharacters.PLAYER.getSprite(playerAnimationIndexY, playerAnimationIndexX), x, y, null);
        // c.drawBitmap(GameCharacters.PLAYER.getSprite(0, 1), 400, 600, null);
        // c.drawBitmap(GameCharacters.PLAYER.getSprite(0, 2), 600, 600, null);
        // c.drawBitmap(GameCharacters.PLAYER.getSprite(0, 3), 800, 600, null);

        c.drawBitmap(GameCharacters.GRUNTTWO.getSprite(0, 0), 300, 1000, null);

        holder.unlockCanvasAndPost(c);
    }

    public void update(double delta) {
        updateAnimation();
    }

    private void updateAnimation() {
        animationTick++;

        if (animationTick >= animationSpeed) {
            animationTick = 0;
            playerAnimationIndexX++;

            if (playerAnimationIndexX >= 4) {
                playerAnimationIndexY = 1;
                playerAnimationIndexX = 0;
            }
            if (playerAnimationIndexY == 1 && playerAnimationIndexX >= 2) {
                playerAnimationIndexX = 0;
                playerAnimationIndexY = 0;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { // Part of View, which SurfaceView extends and GamePanel extends SurfaceView
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        }
        return true;
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

}
