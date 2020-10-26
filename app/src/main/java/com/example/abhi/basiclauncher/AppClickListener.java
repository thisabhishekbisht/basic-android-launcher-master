package com.example.abhi.basiclauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;



public class AppClickListener implements View.OnClickListener {
    Context mContext;

    public  AppClickListener(Context c) {
        mContext = c;
    }

    @Override
    public void onClick(View v) {
        Pac data;
        data = (Pac) v.getTag();

        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cp = new ComponentName(data.packageName, data.name);
        launchIntent.setComponent(cp);

        mContext.startActivity(launchIntent);
    }
}
