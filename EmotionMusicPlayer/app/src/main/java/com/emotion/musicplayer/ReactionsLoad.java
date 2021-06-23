package com.emotion.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ReactionsLoad extends DialogFragment implements View.OnClickListener {
    View view;
    ImageView angry,happy,neutral,sad;
    static String emotion;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.select_emotions,container,false);
        initializeComponent();
        return view;
    }

    @Nullable
    @Override
    public View getView()
    {
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.angry:
                emotion=listener.onEmojiSelected(0);
                getDialog().dismiss();
                break;
            case R.id.sad:
                emotion=listener.onEmojiSelected(1);
                getDialog().dismiss();
                break;
            case R.id.happy:
                emotion=listener.onEmojiSelected(2);
                getDialog().dismiss();
                break;
            case R.id.neutral:
                emotion=listener.onEmojiSelected(3);
                getDialog().dismiss();
                break;
        }

    }

    public void initializeComponent()
    {
        if(getView()==null)return;
        angry=getView().findViewById(R.id.angry);
        happy=getView().findViewById(R.id.happy);
        sad=getView().findViewById(R.id.sad);
        neutral=getView().findViewById(R.id.neutral);
        angry.setOnClickListener(this);
        happy.setOnClickListener(this);
        sad.setOnClickListener(this);
        neutral.setOnClickListener(this);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams manager=new WindowManager.LayoutParams();
        manager.width=WindowManager.LayoutParams.MATCH_PARENT;
        manager.height=WindowManager.LayoutParams.MATCH_PARENT;
        manager.dimAmount=0.0f;
        dialog.getWindow().getDecorView().setBackground(getResources().getDrawable(android.R.color.transparent));
        dialog.setCanceledOnTouchOutside(true);
        dialog.onBackPressed();
    }

    EmojiListener listener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            listener=(EmojiListener)activity;
        }
        catch(ClassCastException ex)
        {
           throw new ClassCastException(activity.toString());
        }
    }
}
