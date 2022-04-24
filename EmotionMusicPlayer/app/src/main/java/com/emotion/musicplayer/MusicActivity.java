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
import com.emotion.musicplayer.utils.SongUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    SongUtil songUtil;
    ListView listView;
    String[] items;
    Button hbtnpause;
    TextView txtnp;
    static int position;
    static ArrayList<File> mySongs;
    ArrayList<Song> allsongslist;
    ArrayList<Song> emotionlist;
    static ArrayList<File> AllSongs;
    static Map<String,ArrayList<File>> music;
    static ArrayList<File> favSongs;
    static String songName;
    String emotion;
    LoadingAcitvity loadingAcitvity;

    class Threadt extends Thread implements NavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            ArrayList<String> emotions=new ArrayList<>(Arrays.asList("Happy","Sad","Neutral","Angry"));
            getAssets_songs(getExternalFilesDir("Songs"));
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
                    mySongs=AllSongs;
                    break;
                case R.id.fav:

                    setTitle("My Favourites");
                    getAssets(getExternalFilesDir("FavouriteSongs"),"FavouriteSongs");
                    mySongs=music.get("FavouriteSongs");
                    if(mySongs!=null) {
                        items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size(); i++) {
                            for (String b : emotions) {
                                if (mySongs.get(i).getName().contains(b)) {
                                    items[i] = mySongs.get(i).getName().substring(7).replace(".mp3", "").replace(".wav", "");
                                    break;
                                } else
                                    items[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
                            }
                        }
                        customAdapter customAdapter = new customAdapter();
                        listView.setAdapter(customAdapter);
                        flg =false;
                    }

                    break;
                case R.id.portfolio:
                    Intent intent = new Intent(MusicActivity.this,AboutActivity.class);
                    startActivity(intent);
                    break;
                case R.id.c_playlist:
                    Intent c_intent = new Intent(MusicActivity.this,CustomizePlaylistActivity.class);
                    c_intent.putExtra("Songs",findAllSongs(getExternalFilesDir("Songs")));
                    startActivity(c_intent);
                    break;
                case R.id.manage_media:
                    Intent d_intent = new Intent(MusicActivity.this,ManageMediaActivity.class);
                    d_intent.putExtra("Songs",findAllSongs(getExternalFilesDir("Songs")));
                    startActivity(d_intent);
                    break;

            }
            if(mySongs!=null && flg) {
                items = new String[mySongs.size()];
                for (int i = 0; i < mySongs.size(); i++) {
                    if(mySongs.get(i).getName().contains(a))
                        items[i] = mySongs.get(i).getName().substring(7).replace(".mp3", "").replace(".wav", "");
                    else
                        items[i] = mySongs.get(i).getName().replace(".mp3", "").replace(".wav", "");
                }
                customAdapter customAdapter = new customAdapter();
                listView.setAdapter(customAdapter);
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        private void Favourites()
        {
            File file=new File(getExternalFilesDir(null)+"/"+ "FavouriteSongs");
            if(!file.exists())
            {
                file.mkdir();
            }
            else
            {
                favSongs=findSongs(file);
            }
        }

        private void copyAssets1(String fname)
        {
            String dirpath=getExternalFilesDir(null)+"/";
            File fout=new File(dirpath,fname);
            if(!fout.exists())
            {
                fout.mkdir();
                dirpath+=fname;

                InputStream in=null;
                OutputStream out=null;
                try{
                    AssetManager assetManager=getResources().getAssets();
                    String[] arr= getResources().getAssets().list("");

                    for(String file:arr)
                    {
                        if(!file.contains(".tflite"))
                            fout = new File(dirpath, file);
                        in = assetManager.open(file);
                        out = new FileOutputStream(fout);
                        copyFile(in, out);
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }


        private void copyFile(InputStream in, OutputStream out) throws IOException
        {
            byte[] buffer =new byte[1024];
            int read;
            while((read=in.read(buffer))!=-1)
            {
                out.write(buffer,0,read);
            }
        }


        public ArrayList<File> findSongs(File file)
            {
                ArrayList<File> arrayList=new ArrayList<>();
                File[] files =file.listFiles();
                if(files!=null)
                    for(File fle:files) {
                        if (!fle.isDirectory()) {
                            if (fle.getName().endsWith(".mp3") || fle.getName().endsWith(".wav"))
                            {
                                arrayList.add(fle);
                            }
                        }
                    }
                return arrayList;
            }

        public ArrayList<File> findAllSongs (File file)
        {
            Log.d(currentThread().getName()+" Location: ",""+file.getAbsolutePath());

            ArrayList<File> arrayList = new ArrayList<>();
            File[] files = file.listFiles();
            if(files!=null)
                for (File singlefile: files)
                {
                    if (singlefile.isDirectory() && !singlefile.isHidden())
                    {
                        arrayList.addAll(findAllSongs(singlefile));
                    }
                    else
                    {
                        if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav"))
                        {
                            arrayList.add(singlefile);
                        }
                    }
                }
            return arrayList;
        }

        public void getAssets (File file,String name)
        {
            Log.d(currentThread().getName()+"Get Assets : ",""+file.getAbsolutePath());
            ArrayList<File> aList = new ArrayList<>();

                        aList=findSongs(file);
                        if(aList.size()>0) {
                                music.put(name,aList);
                        }
                    }

        public void getAssets_songs(File file)
        {
            Log.d(currentThread().getName()+"Get Assets : ",""+file.getAbsolutePath());
            String emotion;
            music.clear();
            ArrayList<File> cpList = new ArrayList<>();
            File[] files = file.listFiles();
            if(files!=null)
                for (File singlefile: files)
                {
                                emotion=singlefile.getName().substring(0,7).trim();
                                cpList.clear();
                                if(music.get(emotion)!=null)
                                cpList=music.get(emotion);
                                cpList.add(singlefile);
                                music.put(emotion,new ArrayList<>(cpList));
                                Log.d("","");
                }
        }
        @Override
        public void run() {
            music=new HashMap<>();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingAcitvity.startLoading();
                }
            });

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawerLayout = findViewById(R.id.drawer_layout);

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MusicActivity.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            AllSongs = findAllSongs(Environment.getExternalStorageDirectory());

            Task<QuerySnapshot> Allsongstask= songUtil.fetchAllSongs();
            Allsongstask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    allsongslist = (ArrayList<Song>)task.getResult().toObjects(Song.class);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });


            Favourites();
            copyAssets1("/Songs");


            getAssets_songs(getExternalFilesDir("Songs"));
            getAssets(getExternalFilesDir("FavouriteSongs"),"FavouriteSongs");

            for(String i:music.keySet())
                Log.d("Songs: "+i,""+music.get(i)+"\n");
            Task<QuerySnapshot> emotionlisttask=songUtil.fetchSongsByEmotion(emotion);

            emotionlisttask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    emotionlist =(ArrayList<Song>) task.getResult().toObjects(Song.class);
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });

            if(!emotion.equals("AllSongs"))
            mySongs=music.get(emotion);
//            mySongs=emotionlist;
            else
                mySongs=AllSongs;

            Collections.sort(mySongs);

            items = new String[mySongs.size()];
            for (int i=0;i<mySongs.size();i++)
            {
                items[i] = mySongs.get(i).getName().replace(".mp3","").replace(".wav","");
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

            loadingAcitvity.stopLoading();

            if(!emotion.equals("AllSongs")) {
                startActivityForResult(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", emotionlist)
                        .putExtra("songname", songName)
                        .putExtra("favSongs",music.get("FavouriteSongs"))
                        .putExtra("pos", 0), 1);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    songName = (String) listView.getItemAtPosition(position);
                    String sname = mySongs.get(position).getName();
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
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i=getIntent();
        Bundle bundle=i.getExtras();

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
