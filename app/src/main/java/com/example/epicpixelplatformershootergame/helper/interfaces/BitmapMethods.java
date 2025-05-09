package com.example.epicpixelplatformershootergame.helper.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

// split in separate File, so GameCharacters.java and Floor.java can both use these methods
public interface BitmapMethods {
    BitmapFactory.Options options = new BitmapFactory.Options();


    default Bitmap getScaledBitmap(Bitmap original) {
        return Bitmap.createScaledBitmap(original, original.getWidth() * GameConstants.Sprite.SCALE_MULTIPLIER, original.getHeight() * GameConstants.Sprite.SCALE_MULTIPLIER, false);
    }
}
