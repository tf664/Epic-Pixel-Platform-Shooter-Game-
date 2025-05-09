package com.example.epicpixelplatformershootergame.helper.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// split in separate File, so GameCharacters.java and Floor.java can both use these methods
public interface BitmapMethods {
    BitmapFactory.Options options = new BitmapFactory.Options();


    default Bitmap getScaledBitmap(Bitmap original) {
        int scale = 10;
        return Bitmap.createScaledBitmap(original, original.getWidth() * scale, original.getHeight() * scale, false);
    }
}
