package com.example.abhi.basiclauncher;

import android.content.res.Configuration;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


public class UpdateSerializedData {

    static List<Pac> pacsForSerialize = MainActivity.pacForUpdateSerialization;

    public static void update(View v, int oldX, int oldY, int newX, int newY) {
        AppSerializableData objectData = SerializationTools.loadSerializedData();
        int position = -1;
        int positionArray = -1;

        if (objectData == null) {
            objectData = new AppSerializableData();
        }

        if (objectData.apps == null) {
            objectData.apps = new ArrayList<Pac>();
        } else {
            outer:
            for (int i = 0; i < pacsForSerialize.size(); i++) {
                for (int j = 0; j < objectData.apps.size(); j++) {
                    //if(pacsForSerialize[i].position != 0) {
                    if (pacsForSerialize.get(i).label.equals(objectData.apps.get(j).label)) {
                        if (objectData.apps.get(j).x == oldX && objectData.apps.get(j).y == oldY) {
                            position = objectData.apps.get(j).position;
                            positionArray = j;
                            break outer;
                        }
                    }
                    //}
                }
            }
        }

        if (positionArray != -1) {
            objectData.apps.remove(positionArray);

            Pac pacToAdd = pacsForSerialize.get(position);
            pacToAdd.x = newX;
            pacToAdd.y = newY;
            pacToAdd.position = position;
            MainActivity.pacForUpdateSerialization.get(position).position = position;

            if (MainActivity.activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                pacToAdd.landscape = true;
            } else {
                pacToAdd.landscape = false;
            }

            pacToAdd.cacheIcon();
            objectData.apps.add(pacToAdd);

            SerializationTools.serializeData(objectData);
        }

    }

    public static void removeSerializedData(View v, RelativeLayout homeView, int oldX, int oldY) {
        AppSerializableData objectData = SerializationTools.loadSerializedData();
        int positionArray = -1;

        if (objectData == null) {
            objectData = new AppSerializableData();
        }

        if (objectData.apps == null) {
            objectData.apps = new ArrayList<Pac>();
        } else {
            outer:
            for (int i = 0; i < pacsForSerialize.size(); i++) {
                for (int j = 0; j < objectData.apps.size(); j++) {
                    //if(pacsForSerialize[i].position != 0) {
                    if (pacsForSerialize.get(i).label.equals(objectData.apps.get(j).label)) {
                        if (objectData.apps.get(j).x == oldX && objectData.apps.get(j).y == oldY) {
                            positionArray = j;
                            break outer;
                        }
                    }
                }
            }
        }

        if (positionArray != -1) {
            objectData.apps.remove(positionArray);

            homeView.removeView(v);
            SerializationTools.serializeData(objectData);
        }
    }
}
