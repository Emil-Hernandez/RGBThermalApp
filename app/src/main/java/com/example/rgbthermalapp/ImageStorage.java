package com.example.rgbthermalapp;

import android.graphics.Bitmap;

public class ImageStorage {
    private static Bitmap image;

    public static void setImage(Bitmap img) {
        image = img;
    }

    public static Bitmap getImage() {
        return image;
    }
}
