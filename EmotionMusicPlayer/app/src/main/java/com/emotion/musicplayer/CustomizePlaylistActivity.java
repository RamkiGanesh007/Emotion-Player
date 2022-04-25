package com.emotion.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.emotion.musicplayer.model.Song;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CustomizePlaylistActivity extends AppCompatActivity implements EmojiListener{

    ListView listView;
    String[] items;
    String sltd;
    static ArrayList<Song> music;
    View e_view;
    int e_position;

    class Threadp extends Thread {
        @Override
        public void run() {
            synchronized (this) {
            }
        }
    }

    @Override
    public String onEmojiSelected(int emoji_id) {
        sltd="";
        ImageView img;
        Song fle=music.get(e_position);
        img = e_view.findViewById(R.id.emotion);
        switch(emoji_id)
        {
            case 0:
                sltd="Angry  ";

                break;
            case 1:
                sltd="Sad    ";


                break;
            case 2:
                sltd="Happy  ";

                break;
            case 3:
                sltd="Neutral";

                break;
        }
        e_position=Integer.MIN_VALUE;
        return sltd;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            Intent resultIntent=new Intent(CustomizePlaylistActivity.this, MusicActivity.class);
            setResult(RESULT_OK,resultIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_playlist);
        String TAG="customize: ";

        getSupportActionBar().setTitle("Customize Playlist");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        music =(ArrayList) bundle.getParcelableArrayList("Songs");
        for(Song file:music)
        {
            Log.i(TAG,file.getSongName());
        }
        listView = findViewById(R.id.c_items);

        items = new String[music.size()];
            for (int i = 0; i < music.size(); i++) {
                items[i] = music.get(i).getSongName();
            }
            customAdapter customAdapter = new customAdapter();
            listView.setAdapter(customAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    e_view=view;
                    e_position=position;
                    getEmotionsDialog();
                }
            });

        Threadp thread = new Threadp();
        thread.start();

    }

    private DialogFragment getEmotionsDialog()
    {
        ReactionsLoad reactionsLoad=new ReactionsLoad();
        reactionsLoad.show(getSupportFragmentManager(),reactionsLoad.getClass().getSimpleName());
        return reactionsLoad;
    }

    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.customize_item,null);
            TextView textsong = view.findViewById(R.id.songname);
            ImageView emoview = view.findViewById(R.id.emotion);
            textsong.setSelected(true);
            String emotion=items[position].substring(0,7).trim();
            textsong.setText(items[position].substring(7));
            emoview.setImageDrawable(null);
            switch (emotion)
            {
                case "Happy":
                    emoview.setBackgroundResource(R.drawable.happy_emoji);
                    break;
                case "Sad":
                    emoview.setBackgroundResource(R.drawable.sad_emoji);
                    break;
                case "Neutral":
                    emoview.setBackgroundResource(R.drawable.neutral_emoji);
                    break;
                case "Angry":
                    emoview.setBackgroundResource(R.drawable.angry_emoji);
                    break;
            }

            return view;
        }
    }


}