package com.example.baseactivity;

import android.content.Context;
import android.support.annotation.RequiresPermission;

import com.example.baseactivity.DiskLruCache.DiskLruCache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CacheUtils {

    private static File getCacheDir(Context context, String fileName) {
        String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator, fileName);
    }

    public static DiskLruCache createDiskLruCache(Context context, String name) {
        DiskLruCache diskLruCache = null;
        File cacheDir = getCacheDir(context, name);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        try {
            diskLruCache = DiskLruCache.open(cacheDir, 1, 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return diskLruCache;
    }

    private static String toHexMd5(String path) {

        String cacheKey = null;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(path.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return cacheKey;

    }

    private static String bytesToHexString(byte[] d) {
        StringBuilder sb = new StringBuilder();
        for (byte ad : d) {
            String hex = Integer.toHexString(0XFF & ad);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /*
    加入缓存和读缓存运行在子线程。
    */
    //加入缓存。
    public static void writeCache(Context context, String path, String name, InputStream inputStream) {
        OutputStream outputStream = null;
        String key = toHexMd5(path);
        DiskLruCache diskLruCache = createDiskLruCache(context, name);
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);
            int b;
            while ((b = inputStream.read()) != -1) {
                outputStream.write(b);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //读取图片缓存
    public static byte[] readCache(Context context, String path, String name) {
        ByteArrayOutputStream outputStream = null;
        String key = toHexMd5(path);
        DiskLruCache diskLruCache = createDiskLruCache(context, name);
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                InputStream inputStream = snapshot.getInputStream(0);
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
