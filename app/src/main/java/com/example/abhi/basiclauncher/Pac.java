package com.example.abhi.basiclauncher;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Pac {
    transient Drawable icon;
    String name;
    String packageName;
    String label;
    String versionCode;
    String mainActivity;
    String versionName;
    int x, y;
    String iconLocation;
    boolean landscape;
    int position;

    public void cacheIcon() {
        if (iconLocation == null) {
            new File(MainActivity.activity.getApplicationInfo().dataDir + "/cachedApps/").mkdirs();
        }
        if (icon != null) {
            iconLocation = MainActivity.activity.getApplicationInfo().dataDir + "/cachedApps/" + packageName + name + versionCode + versionName + mainActivity;
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(iconLocation);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (fos != null) {
                Tools.drawableToBitmap(icon).compress(Bitmap.CompressFormat.PNG, 100, fos);
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                iconLocation = null;
            }
        }
    }
}
