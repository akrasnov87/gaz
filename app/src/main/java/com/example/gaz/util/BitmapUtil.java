package com.example.gaz.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {
    public final static int QUALITY_120p = 120;
    public final static int QUALITY_720p = 720;

    public final static int IMAGE_QUALITY = 60;

    /**
     * создания изображения для кэша
     * @param bitmap Изображение
     * @param quality качество создаваемого изображения в процентах от 0 до 100
     * @param p Высота изображения. Использовать одно из полей QUALITY_[number]p
     * @return массив байтов
     */
    public static byte[] cacheBitmap(Bitmap bitmap, int quality, int p) {
        Bitmap resizeBmp = scaleToFitHeight(bitmap, p);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resizeBmp.compress(Bitmap.CompressFormat.WEBP, quality, bos);
        return bos.toByteArray();
    }

    /**
     * создания изображения для кэша
     * @param bytes массив байтов
     * @param quality качество создаваемого изображения в процентах от 0 до 100
     * @param p Высота изображения. Использовать одно из полей QUALITY_[number]p
     * @return массив байтов
     */
    public static byte[] cacheBitmap(byte[] bytes, int quality, int p) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return cacheBitmap(bitmap, quality, p);
    }

    // Scale and maintain aspect ratio given a desired width
    // BitmapScale.scaleToFitWidth(bitmap, 100);
    public static Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }


    // Scale and maintain aspect ratio given a desired height
    // BitmapScale.scaleToFitHeight(bitmap, 100);
    public static Bitmap scaleToFitHeight(Bitmap b, int height)
    {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    public static byte[] convertToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
}
