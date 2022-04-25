package com.emotion.musicplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.emotion.musicplayer.model.Song;
import com.emotion.musicplayer.utils.SongUtil;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadSongsActivity extends AppCompatActivity {
    private WebView webview;

    private final int PICK_SONGS_INTENT = 1;
    private HashMap<String,Uri> selectedSongs;
    private String selectedEmotion;
    private String songName;
    private SongUtil songUtil;
//    private s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_songs);
        songUtil=new SongUtil();



    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_happy:
                if (checked)
                    selectedEmotion = "happy";
                break;
            case R.id.radio_sad:
                if (checked)
                    selectedEmotion = "sad";
                break;
            case R.id.radio_neutral:
                if (checked)
                    selectedEmotion = "neutral";
                break;
            case R.id.radio_angry:
                if (checked)
                    selectedEmotion = "angry";
                break;
        }
    }

    public void onSelectSongsButtonClick(View view) {
        Intent chooseFile;
        chooseFile = new Intent();
        chooseFile.setType("audio/*");
        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(chooseFile, PICK_SONGS_INTENT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_SONGS_INTENT) {
            this.selectedSongs = new HashMap<>();

            if(data != null) { // checking empty selection
                if(data.getClipData() != null) { // checking multiple selection or not
                    for(int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        songName="Random";
                        Cursor mcursor = getApplicationContext().getContentResolver()
                                .query(uri,null,null,null,null);

                        int indexedname = mcursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        mcursor.moveToFirst();
                        songName=mcursor.getString(indexedname);
                        selectedSongs.put(songName,uri);
                    }
                } else {
                    Uri uri = data.getData();
                    Cursor mcursor = getApplicationContext().getContentResolver()
                            .query(uri,null,null,null,null);

                    int indexedname = mcursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    mcursor.moveToFirst();
                    songName=mcursor.getString(indexedname);
                    selectedSongs.put(songName,uri);
                }
            }
        }
    }

    public void onSubmit(View view) {
        if(selectedEmotion == null) {
            Toast.makeText(this,"Select an emotion!", Toast.LENGTH_SHORT);
            return;
        }
        if(selectedSongs.isEmpty()) {
            Toast.makeText(this,"Select at least one song!", Toast.LENGTH_SHORT);
            return;
        }

        // upload each file looping through selectedSongs
        for(Map.Entry<String,Uri> song : selectedSongs.entrySet())
        {
            songUtil.storeSong(song.getKey(),song.getValue(),selectedEmotion);
        }
    }
}
