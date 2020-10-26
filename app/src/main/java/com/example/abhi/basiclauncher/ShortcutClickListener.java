package com.example.abhi.basiclauncher;

import android.content.Context;
import android.content.Intent;
import android.view.View;



public class ShortcutClickListener implements View.OnClickListener {
    Context mContext;

    public  ShortcutClickListener(Context c) {
        mContext = c;
    }

    @Override
    public void onClick(View v) {
        Intent data;
        data = (Intent) v.getTag();

        mContext.startActivity(data);
    }
}
