    package com.emotion.musicplayer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emotion.musicplayer.model.Song;
import com.emotion.musicplayer.model.User;
import com.emotion.musicplayer.utils.SongUtil;
import com.emotion.musicplayer.utils.UserUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    SongUtil songUtil;
    UserUtil userUtil;
    ListView listView;
    String[] items;
    Button hbtnpause;
    TextView txtnp;
    static int position;
    static ArrayList<Song> mySongs;
    ArrayList<Song> allsongslist;
    ArrayList<Song> emotionlist;
    static ArrayList<Song> AllSongs;
    static Map<String,ArrayList<Song>> music;
    static ArrayList<Song> favSongs;
    static String songName;
    String emotion;
    LoadingAcitvity loadingAcitvity;
    private String id;
    private Map<String, Object> userobj;

    private void getMusic() {

        Task<QuerySnapshot> Allsongstask= userUtil.fetchAllSongs(id).get();

        Allsongstask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<User> u;
                u=(ArrayList<User>) task.getResult().toObjects(User.class);

                User k=u.get(0);
                allsongslist = k.getMySongs();
                updateEmotionlist(allsongslist);

        }
    });
    }

    private static void updateEmotionlist(ArrayList<Song> allsongslist) {
        ArrayList<Song>  dummy;
        for(Song i:allsongslist)
        {
            dummy=music.get(i.getEmotion());
            dummy.add(i);
            music.put(i.getEmotion(),dummy);
        }
    }

    class Threadt extends Thread implements NavigationView.OnNavigationItemSelectedListener{



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Intent intent=null;
            ArrayList<String> emotions=new ArrayList<>(Arrays.asList("Happy","Sad","Neutral","Angry"));
                    String a="klslkmdmksdlk";
                    boolean flg=true;
            listView.setAdapter(null);
            switch (menuItem.getItemId())
            {
                case R.id.happy:
                    setTitle("Happy Songs");
                    mySongs=music.get("Happy");
                    a="Happy";
                    break;
                case R.id.sad:
                    setTitle("Sad Songs");
                    mySongs=music.get("Sad");
                    a="Sad";
                    break;
                case R.id.neutral:
                    setTitle("Neutral Songs");
                    mySongs=music.get("Neutral");
                    a="Neutral";
                    break;
                case R.id.angry:
                    setTitle("Angry Songs");
                    mySongs=music.get("Angry");
                    a="Angry";
                    break;
                case R.id.all:
                    setTitle("All Songs");
                    mySongs=allsongslist;
                    break;
                case R.id.fav:

                    setTitle("My Favourites");
                    mySongs=music.get("FavouriteSongs");
                    if(mySongs!=null) {
                        items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size(); i++) {
                            for (String b : emotions) {
                                if (mySongs.get(i).getSongName().contains(b)) {
                                    items[i] = mySongs.get(i).getSongName();
                                    break;
                                } else
                                    items[i] = mySongs.get(i).getSongName();
                            }
                        }
                        customAdapter customAdapter = new customAdapter();
                        listView.setAdapter(customAdapter);
                        flg =false;
                    }

                    break;
                case R.id.upload:
                    intent = new Intent(MusicActivity.this,UploadSongsActivity.class);
                    startActivityForResult(intent,10);
                    getMusic();
                    updateEmotionlist(allsongslist);
                    mySongs=allsongslist;
                    break;

                case R.id.portfolio:
                    intent = new Intent(MusicActivity.this,AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.c_playlist:
                    intent = new Intent(MusicActivity.this,CustomizePlaylistActivity.class);

                    intent.putExtra("Songs",allsongslist);
                    startActivity(intent);
                    break;
                case R.id.manage_media:
                    intent = new Intent(MusicActivity.this,ManageMediaActivity.class);

                    intent.putExtra("Songs",allsongslist);
                    startActivity(intent);
                    break;
            }
            if(mySongs!=null && flg) {
                items = new String[mySongs.size()];
                for (int i = 0; i < mySongs.size(); i++) {
                    if(mySongs.get(i).getSongName().contains(a))
                        items[i] = mySongs.get(i).getSongName();
                    else
                        items[i] = mySongs.get(i).getSongName();
                }
                customAdapter customAdapter = new customAdapter();
                listView.setAdapter(customAdapter);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }



        @Override
        public void run() {
            music=new HashMap<>();

            for(String i:getResources().getStringArray(R.array.emotions))
            {
                music.put(i,new ArrayList<>());
            }
            music.put("AllSongs",new ArrayList<>());

            music.put("FavouriteSongs",new ArrayList<>());

            Toolbar toolbar = findViewById(R.id.toolbar);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingAcitvity.startLoading();

                }
            });
            setSupportActionBar(toolbar);
            drawerLayout = findViewById(R.id.drawer_layout);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MusicActivity.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            Task<QuerySnapshot> Allsongstask= userUtil.fetchAllSongs(id).get();

            Allsongstask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<User> u;
                    u=(ArrayList<User>) task.getResult().toObjects(User.class);

                    User k=u.get(0);
//                    k.getMySongs()
//                    updateEmotionlist(allsongslist);
                    allsongslist = k.getMySongs();
                    updateEmotionlist(allsongslist);

                    loadingAcitvity.stopLoading();

                        for(String i:music.keySet())
                            Log.d("Songs: "+i,""+music.get(i)+"\n");

                        emotionlist=music.get(emotion);

                        if(!emotion.equals("AllSongs"))
                            mySongs=emotionlist;
                        else
                            mySongs=allsongslist;


                        Collections.sort(mySongs, new Comparator<Song>() {
                            @Override
                            public int compare(Song s1, Song s2) {
                                return s1.getSongName().compareTo(s2.getSongName());
                            }
                        });

                        items = new String[mySongs.size()];
                        for (int i=0;i<mySongs.size();i++)
                        {
                            items[i] = mySongs.get(i).getSongName();
                        }

                        customAdapter customAdapter = new customAdapter();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(customAdapter);
                                txtnp.setSelected(true);
                                txtnp.setVisibility(View.VISIBLE);
                            }
                        });



                        if(!emotion.equals("AllSongs")) {
                            startActivityForResult(new Intent(getApplicationContext(), PlayerActivity.class)
                                    .putExtra("songs", emotionlist)
                                    .putExtra("songname", songName)
                                    .putExtra("favSongs",music.get("FavouriteSongs"))
                                    .putExtra("pos", 0), 1);
                        }

                    }});


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    songName = mySongs.get(position).getSongName();
                    startActivityForResult(new Intent(getApplicationContext(),PlayerActivity.class)
                            .putExtra("songs", allsongslist)
                            .putExtra("songname",songName)
                            .putExtra("favSongs",music.get("FavouriteSongs"))
                            .putExtra("pos", position),1);

                    //Changes done here
                }
            });
        }


    }

    RelativeLayout rv;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==1)
        {
            if(resultCode==RESULT_OK)
            {
                songName=data.getStringExtra(PlayerActivity.EXTRA_NAME);
                position=data.getIntExtra("pos",0);

                for(String i:new ArrayList<>(Arrays.asList("Happy","Sad","Neutral","Angry"))) {
                    if (songName.contains(i)) {
                        songName = songName.substring(7);
                        break;
                    }
                }
                hbtnpause.setBackgroundResource(R.drawable.ic_pause_circle);
                txtnp.setText(songName);
                txtnp.setSelected(true);
            }

        }
        if(resultCode==10)
        {
            getMusic();
            listView.setAdapter(null);
            items = new String[allsongslist.size()];
            for (int i = 0; i < allsongslist.size(); i++) {
                    items[i] = allsongslist.get(i).getSongName();
            }
            customAdapter customAdapter = new customAdapter();
            listView.setAdapter(customAdapter);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songUtil=new SongUtil();
        userUtil=new UserUtil();
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        id= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        emotion=i.getStringExtra("emotion");

        setTitle(emotion);

        setContentView(R.layout.music_activity);
        loadingAcitvity=new LoadingAcitvity(MusicActivity.this);

        rv=findViewById(R.id.playercard);

        rv.setVisibility(View.VISIBLE);


        listView = findViewById(R.id.listviewsongs);
        txtnp = findViewById(R.id.txtnp);

        txtnp.setText("Idle..");
        txtnp.setSelected(true);

        runtimepermission();

        hbtnpause = findViewById(R.id.hbtnpause);

        txtnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayerActivity.mediaPlayer==null)
                {
                    Toast.makeText(MusicActivity.this, "No Song Is Playing", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent mIntent=new Intent(MusicActivity.this, PlayerActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mIntent.putExtra("pos",position);
                     mIntent.putExtra("songs", mySongs);
                     mIntent.putExtra("favSongs",music.get("FavouriteSongs"));
                    mIntent.putExtra("songname",songName);
                    startActivityForResult(mIntent,1);
                }
            }
        });


        hbtnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayerActivity.mediaPlayer!=null)
                {
                    if (PlayerActivity.mediaPlayer.isPlaying())
                    {
                        hbtnpause.setBackgroundResource(R.drawable.ic_play_circle);
                        PlayerActivity.mediaPlayer.pause();

                    }
                    else
                    {
                        hbtnpause.setBackgroundResource(R.drawable.ic_pause_circle);
                        PlayerActivity.mediaPlayer.start();
                    }
                }
            }
        });


    }

    public void runtimepermission()
    {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        display();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    void display()
    {
        Threadt songsthread=new Threadt();
        songsthread.start();
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

            View view = getLayoutInflater().inflate(R.layout.song_item,null);
            TextView textsong = view.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[position]);
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
           finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
