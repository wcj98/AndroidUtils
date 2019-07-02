package com.example.baseactivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapDecodeUtils {

    public static Bitmap decodeBitmap(byte[] b, int reqW, int reqH, boolean config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(b, 0, b.length, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = editBitmap(options, reqW, reqH);
        if (config) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length, options);
    }

    public static Bitmap decodeBitmap(Resources resources, int resId, int reqW, int reqH, boolean config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = editBitmap(options, reqW, reqH);
        if (config) {
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        return BitmapFactory.decodeResource(resources, resId, options);

    }

    private static int editBitmap(BitmapFactory.Options options, int reqW, int reqH) {
        final int outWidth = options.outWidth;
        final int outHeight = options.outHeight;
        if (outHeight > reqH || outWidth > reqW) {
            float h = (float) outHeight / reqH;
            float w = (float) outWidth / reqW;
            return h > w ? Math.round(h) : Math.round(w);
        }
        return 1;
    }

}
