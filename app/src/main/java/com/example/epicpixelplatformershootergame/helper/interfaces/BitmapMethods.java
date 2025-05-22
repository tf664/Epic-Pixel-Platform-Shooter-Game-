package com.example.epicpixelplatformershootergame.helper.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.epicpixelplatformershootergame.helper.GameConstants;

public interface BitmapMethods {
    BitmapFactory.Options options = new BitmapFactory.Options();

    default Bitmap getScaledBitmap(Bitmap original, int scaleMultiplier) {
        return Bitmap.createScaledBitmap(original, original.getWidth() * scaleMultiplier,
                original.getHeight() * scaleMultiplier,
                false);
    }
}