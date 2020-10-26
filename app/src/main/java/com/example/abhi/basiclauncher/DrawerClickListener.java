package com.example.abhi.basiclauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;



public class DrawerClickListener implements AdapterView.OnItemClickListener {
    Context mContext;
    List<Pac> pacsForListener;
    PackageManager pmForListener;

    public DrawerClickListener(Context c, List<Pac> pacs, PackageManager pm) {
        mContext = c;
        pacsForListener = pacs;
        pmForListener = pm;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(MainActivity.appLaunchable) {
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cp = new ComponentName(pacsForListener.get(position).packageName, pacsForListener.get(position).name);
            launchIntent.setComponent(cp);

            mContext.startActivity(launchIntent);
        }
    }
}
