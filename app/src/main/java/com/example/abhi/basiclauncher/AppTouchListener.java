package com.example.abhi.basiclauncher;

import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class AppTouchListener implements View.OnTouchListener {
    int leftMargin;
    int topMargin;
    int removeX;
    int removeY;
    int oldX;
    int oldY;
    int newX;
    int newY;
    RelativeLayout homeViewForAdapter;
    LinearLayout removeItems;

    public AppTouchListener(int x, int y) {
        oldX = x;
        oldY = y;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        homeViewForAdapter = (RelativeLayout) MainActivity.activity.findViewById(R.id.home_view);

        RelativeLayout.LayoutParams lp;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                ((AppCompatActivity) MainActivity.activity).getSupportActionBar().hide();

                removeX = (int) removeItems.getX();
                removeY = (int) removeItems.getY() + (removeItems.getHeight());

                lp = new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());

                leftMargin = (int) event.getRawX() - v.getWidth()/2;
                topMargin = (int) event.getRawY() - v.getHeight()/2;

                if(leftMargin + v.getWidth() > v.getRootView().getWidth()) {
                    leftMargin = v.getRootView().getWidth() - v.getWidth();
                }

                if(leftMargin <0) {
                    leftMargin = 0;
                }

                if(topMargin + v.getHeight() > ((View) v.getParent()).getHeight()) {
                    topMargin = ((View) v.getParent()).getHeight() - v.getHeight();
                }

                if(topMargin < 0) {
                    topMargin = 0;
                }

                if(topMargin <= removeY) {
                    v.bringToFront();
                    removeItems.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.redTransparent));

                }else {
                    removeItems.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.blackTransparent));

                }

                lp.leftMargin = leftMargin;
                lp.topMargin = topMargin;
                v.setLayoutParams(lp);
                break;
            case MotionEvent.ACTION_UP:
                v.setOnTouchListener(null);

                newX = (int) v.getX();
                newY = (int) v.getY();

                if(topMargin <= removeY) {

                    UpdateSerializedData.removeSerializedData(v, homeViewForAdapter, oldX, oldY);
                }else {
                    removeItems.setBackgroundColor(MainActivity.activity.getResources().getColor(R.color.blackTransparent));

                    UpdateSerializedData.update(v, oldX, oldY, newX, newY);
                }
                ((AppCompatActivity) MainActivity.activity).getSupportActionBar().show();
                break;
        }
        return true;
    }
}
