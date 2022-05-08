package com.emotion.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.emotion.musicplayer.model.Song;
import com.emotion.musicplayer.utils.UserUtil;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerActivity extends AppCompatActivity{


    Button btnplay,btnprev,btnnext,btnfav,btnplist;
    TextView txtsname,txtsstart,txtsstop;
    SeekBar seekmusic;
    BarVisualizer visualizer;

    String sname;
    public static final String EXTRA_NAME="songname";

    static MediaPlayer mediaPlayer;

    int position;
    ArrayList<Song> mySongs;

    private final Handler mainHandler=new Handler();

    Thread updateSeekbar;

    Runnable runnable;

    class Threadp extends Thread{

        public void startAnimation(View view)
        {

        }

        public String createTime(int duration)
        {
            String time="";
            int min=duration/1000/60;
            int sec=duration/1000%60;
            time+=min+":";
            time+=(sec<10)?"0"+sec:sec;
            return time;
        }

        @Override
        public void run() {
            synchronized (this) {
                getSupportActionBar().setTitle("Now Playing");

                Log.i("TAG", "Im in " + Thread.currentThread().getName());

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

                btnplay = findViewById(R.id.playbtn);
                btnprev = findViewById(R.id.btnprev);
                btnnext = findViewById(R.id.btnnext);
                btnfav = findViewById(R.id.btnfav);
                btnplist = findViewById(R.id.btnplst);


                txtsname = findViewById(R.id.txtsn);
                txtsstart = findViewById(R.id.txtsstart);
                txtsstop = findViewById(R.id.txtsstop);

                seekmusic = findViewById(R.id.seekbar);
                visualizer = findViewById(R.id.blast);

                ImageView imageView = findViewById(R.id.imageview);


                Intent i = getIntent();
                Bundle bundle = i.getExtras();

                Log.i("Plact Returns: ", "" + i.getExtras());

                mySongs=(ArrayList) bundle.getParcelableArrayList("songs");


                String songName = i.getStringExtra("songname");
                String id = i.getStringExtra("id");
                position = bundle.getInt("pos", 0);
                txtsname.setSelected(true);

                String songUrl=mySongs.get(position).getSongUrl();
                sname = mySongs.get(position).getSongName();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            if (mySongs.get(position).getIsFavourite().equals("Yes")) {
                                btnfav.setBackgroundResource(R.drawable.ic_favorite);
                            } else {
                                btnfav.setBackgroundResource(R.drawable.ic_favorite_blank);
                        }
                    }
                });
                try {
                    if(mediaPlayer!=null)
                    {
                        mediaPlayer.stop();
                    }
                    mediaPlayer =new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                    try {
                        mediaPlayer.setDataSource(songUrl);
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String endTime=createTime(mediaPlayer.getDuration());
                }
                catch(IllegalStateException ex)
                {
                    ex.printStackTrace();
                }

                updateSeekbar=new Thread()
                {
                    @Override
                    public void run() {
                        if(mediaPlayer!=null)
                        {
                            try {
                            int totalduration=mediaPlayer.getDuration();
                            int currentposition=0;
                            while(currentposition<totalduration)
                            {
                                    sleep(500);
                                    currentposition=mediaPlayer.getCurrentPosition();
                                    seekmusic.setProgress(currentposition);
                                }

                            }
                              catch(InterruptedException | IllegalStateException ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }
                };


                updateSeekbar.start();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            seekmusic.setMax(mediaPlayer.getDuration());
                            seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.MULTIPLY);
                            seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.teal_200), PorterDuff.Mode.SRC_IN);
                        }
                        catch(IllegalStateException ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                });


                seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });

                final Handler handler=new Handler(Looper.getMainLooper());
                final int delay=1000;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            if(mediaPlayer!=null){
                            if (mediaPlayer.isPlaying()) {
                                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                                txtsstart.setText(currentTime);
                                txtsstop.setText(createTime(mediaPlayer.getDuration()));
                                for(String i:new ArrayList<>(Arrays.asList("Happy","Sad","Neutral","Angry")))
                                {
                                    if(sname.contains(i))
                                    {
                                        sname=sname.substring(7);
                                    }
                                }
                                txtsname.setText(sname);
                                handler.postDelayed(this, delay);
                            }
                        }
                    }
                });


                btnplay.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(mediaPlayer!=null)
                        if(mediaPlayer.isPlaying())
                        {
                            btnplay.setBackgroundResource(R.drawable.ic_play);
                            mediaPlayer.pause();
                        }
                        else
                        {
                            btnplay.setBackgroundResource(R.drawable.ic_pause);
                            mediaPlayer.start();
                        }
                    }
                });

                btnnext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            position = ((position + 1) % mySongs.size());
                        String songUrl=mySongs.get(position).getSongUrl();
                            if(mediaPlayer.isPlaying())
                            {
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                            }
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(songUrl);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                            sname = mySongs.get(position).getSongName();
                            txtsname.setText(sname);
                            btnplay.setBackgroundResource(R.drawable.ic_pause);
                            startAnimation(imageView);
                            seekmusic.setMax(mediaPlayer.getDuration());
                            int audioSessionId = mediaPlayer.getAudioSessionId();
                            if (audioSessionId != -1) {
                                visualizer.setAudioSessionId(audioSessionId);
                            }
                    }
                });

                btnprev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        position=((position-1)%mySongs.size());

                        if(mediaPlayer.isPlaying())
                        {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }
                        String songUrl=mySongs.get(position).getSongUrl();

                        try{
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(songUrl);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        sname=mySongs.get(position).getSongName();

                        txtsname.setText(sname);
                        btnplay.setBackgroundResource(R.drawable.ic_pause);
                        startAnimation(imageView);
                        seekmusic.setMax(mediaPlayer.getDuration());

                        int audioSessionId=mediaPlayer.getAudioSessionId();
                        if(audioSessionId!=-1)
                        {
                            visualizer.setAudioSessionId(audioSessionId);
                        }
                    }
                });



                btnfav.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        if (btnfav.getBackground().getConstantState().equals(getDrawable(R.drawable.ic_favorite_blank).getConstantState())) {
                            btnfav.setBackgroundResource(R.drawable.ic_favorite);
                            mySongs.get(position).setIsFavourite("Yes");
                        } else
                        {
                            btnfav.setBackgroundResource(R.drawable.ic_favorite_blank);
                            mySongs.get(position).setIsFavourite("No");
                        }
                        new UserUtil().getDocRef(i.getStringExtra("id")).update("mySongs",mySongs).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });

                btnplist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent resultIntent=new Intent(PlayerActivity.this, MusicActivity.class);
                        sname = mySongs.get(position).getSongName();
                        resultIntent.putExtra(EXTRA_NAME, sname);
                        resultIntent.putExtra("pos",position);
                        resultIntent.putExtra("mysongs",mySongs);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btnnext.performClick();
                    }
                });
                try {
                    int audioSessionId = mediaPlayer.getAudioSessionId();
                    if (audioSessionId != -1) {
                        visualizer.setAudioSessionId(audioSessionId);
                    }
                }
                catch(IllegalStateException ex)
                {
                    ex.printStackTrace();
                }
            }
            Log.i("TAG","Im in "+Thread.currentThread().getName());
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
                    Intent resultIntent=new Intent(PlayerActivity.this, MusicActivity.class);
                    sname = mySongs.get(position).getSongName();
                    resultIntent.putExtra("songname", sname);
                    resultIntent.putExtra("pos",position);
                    resultIntent.putExtra("mysongs",mySongs);
                    setResult(RESULT_OK,resultIntent);
                    finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer!=null)
        {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String TAG = "Toast :";

        Threadp thread = new Threadp();
        thread.start();

    }
}