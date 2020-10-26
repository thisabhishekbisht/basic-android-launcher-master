package com.example.abhi.basiclauncher;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;



public class DrawerLongClickListener {
    Context mContext;
    RelativeLayout homeViewForAdapter;
    List<Pac> pacsForListener;

    public DrawerLongClickListener(Context c, RelativeLayout homeView, List<Pac> pacs, AdapterView<?> parent, View view, int position, long id) {
        mContext = c;

        homeViewForAdapter = homeView;
        pacsForListener = pacs;

        addToHome(parent, view, position, id);
    }

    public boolean addToHome(AdapterView<?> parent, View view, int position, long id) {
        MainActivity.appLaunchable = false;

        AppSerializableData objectData = SerializationTools.loadSerializedData();

        if(objectData == null) {
            objectData = new AppSerializableData();
        }

        if(objectData.apps == null) {
            objectData.apps = new ArrayList<Pac>();
        }

        Pac pacToAdd = pacsForListener.get(position);
        pacToAdd.x = (int) view.getX();
        pacToAdd.y = (int) view.getY();
        pacToAdd.position = position;
        MainActivity.pacForUpdateSerialization.get(position).position = position;

        if(MainActivity.activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pacToAdd.landscape = true;
        }else {
            pacToAdd.landscape = false;
        }

        pacToAdd.cacheIcon();
        objectData.apps.add(pacToAdd);

        SerializationTools.serializeData(objectData);

        return false;
    }
}
