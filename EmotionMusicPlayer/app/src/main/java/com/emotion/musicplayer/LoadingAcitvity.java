package com.emotion.musicplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;

public class LoadingAcitvity {
    Activity activity;
    AlertDialog dialog;


    LoadingAcitvity(Activity myActivity)
    {
        activity=myActivity;
    }

    void startLoading()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.custom_dialog,null));

        dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    void stopLoading()
    {
        if(dialog.isShowing())
     dialog.dismiss();
    }

}
