package com.example.abhi.basiclauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;


public class FavPac implements Serializable {
    transient Drawable icon;
    String name;
    String packageName;
    String versionName;
    String versionCode;
    String label;
    String iconLocation;
    FavPac[] mFavPacs;
    FavAppsAdapter mFavAppsAdapterObject;
    int i = 0;

    public void cacheIcon() {
        if (iconLocation == null) {
            new File(MainActivity.activity.getApplicationInfo().dataDir + "/cachedApps/").mkdirs();
        }
        if (icon != null) {
            iconLocation = MainActivity.activity.getApplicationInfo().dataDir + "/cachedApps/" + packageName + name;

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

    public Bitmap getCachedIcon() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;

        if (iconLocation != null) {
            File cachedIcon = new File(iconLocation);

            if (cachedIcon.exists()) {
                return BitmapFactory.decodeFile(cachedIcon.getAbsolutePath(), options);
            }
        }

        return null;
    }


    public void addToFav(Context mContext, FavPac[] favPacs, FavAppsAdapter favAppsAdapterObject) {
        mFavPacs = favPacs;
        mFavAppsAdapterObject = favAppsAdapterObject;


        if (icon == null) {
            icon = new BitmapDrawable(mContext.getResources(), getCachedIcon());
        }

        i++;
        if (i >= 10) {
            i = 0;
        }

        try {
            mFavPacs[i].icon = icon;
            mFavPacs[i].label = label;
            mFavPacs[i].name = name;
            mFavPacs[i].packageName = packageName;
            mFavPacs[i].iconLocation = iconLocation;

            mFavPacs[i + 1].icon = null;
            mFavPacs[i + 1].label = null;
            mFavPacs[i + 1].name = null;
            mFavPacs[i + 1].packageName = null;
            mFavPacs[i + 1].iconLocation = null;
        } catch (NullPointerException e) {
        }


        favAppsAdapterObject.favPacsForAdapter = mFavPacs;

    }

}
