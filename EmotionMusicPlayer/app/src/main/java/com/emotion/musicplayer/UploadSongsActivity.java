package com.emotion.musicplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup emotionpick;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_songs);
        songUtil=new SongUtil();
        button=findViewById(R.id.upload);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectSongsButtonClick(view);
            }
        });

        emotionpick=(RadioGroup)findViewById(R.id.radioGroup);

    }

    public void onSelectSongsButtonClick(View view) {

        RadioButton r=(RadioButton)findViewById(emotionpick.getCheckedRadioButtonId());
        selectedEmotion=String.valueOf(r.getText());

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Songs"), 1);

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
            onSubmit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onSubmit() {
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
            String id= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
            songUtil.storeSong(id,song.getKey(),song.getValue(),selectedEmotion);
        }

        Toast.makeText(this, "Songs Uploaded Successfully!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        setResult(10, intent);
        finish();

    }
}
