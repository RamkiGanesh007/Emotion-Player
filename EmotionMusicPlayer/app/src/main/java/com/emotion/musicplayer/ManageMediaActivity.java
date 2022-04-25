package com.emotion.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
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

public class ManageMediaActivity extends AppCompatActivity{

    ListView listView;
    String[] items;
    static ArrayList<Song> music;
    int m_position;

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            Intent resultIntent=new Intent(ManageMediaActivity.this, MusicActivity.class);
            setResult(RESULT_OK,resultIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSongs()
    {
        items = new String[music.size()];
        for (int i = 0; i < music.size(); i++) {
            items[i] = music.get(i).getSongName();
        }
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_media);
        String TAG="customize: ";

        getSupportActionBar().setTitle("Manage Media");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        music =(ArrayList) bundle.getParcelableArrayList("Songs");
        for(Song file:music)
        {
            Log.i(TAG,file.getSongName());
        }
        listView = findViewById(R.id.delete_items);
        updateSongs();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song file=music.get(position);
                m_position=position;
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        });
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete Song")
                .setMessage("Do you want to Delete the Song")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Song fle=music.get(m_position);
                        if(true)//implement deleting a song
                        {
//                            music.remove(m_position);
//                            updateSongs();
//                            m_position=Integer.MIN_VALUE;
                        }
                        else
                        {
//                            m_position=Integer.MIN_VALUE;
                        }
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return myQuittingDialogBox;
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

            View view = getLayoutInflater().inflate(R.layout.d_item,null);
            TextView textsong = view.findViewById(R.id.songname);
            textsong.setSelected(true);
            String emotion=items[position].substring(0,7).trim();
            textsong.setText(items[position].substring(7));
            return view;
        }
    }
}