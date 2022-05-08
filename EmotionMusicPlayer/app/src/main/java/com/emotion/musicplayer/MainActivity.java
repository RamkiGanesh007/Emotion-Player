package com.emotion.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.emotion.musicplayer.model.Song;
import com.emotion.musicplayer.model.User;
import com.emotion.musicplayer.utils.UserUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CardView emotion;
    private CardView normal;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            if(PlayerActivity.mediaPlayer!=null)
            {
                PlayerActivity.mediaPlayer.stop();
            }
        }
        catch(IllegalStateException ex)
        {
            ex.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void runtimepermission()
    {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "Permissions Granted!!", Toast.LENGTH_SHORT).show();
                            String id=Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
                            UserUtil userUtil=new UserUtil();

                            userUtil.getUser(id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                        }
                                    } else {
                                        Log.d("TAG", "Error getting documents: ", task.getException());
                                    }
                                }
                            });


                            Task<QuerySnapshot> task1 = userUtil.getUser(id).get();
                            task1.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    ArrayList<User> u;
                                    u=(ArrayList<User>) task.getResult().toObjects(User.class);
                                    User k=u.get(0);
                                    if(!k.getUserId().equals(id)) {
                                        userUtil.storeUser(id);
                                        Log.d("TAG", "onPermissionsChecked: " + "User Not Exists!!");
                                        return ;
                                    }
                                    Log.d("TAG", "onPermissio   nsChecked: " + "User is Existing");
                                    return ;
                                }
                        });
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emotion=findViewById(R.id.emotion);
        normal=findViewById(R.id.normal);

        runtimepermission();

        emotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(MainActivity.this, EmotionActivity.class);
                startActivityForResult(mIntent,0);
            }
        });

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(MainActivity.this, MusicActivity.class);
                mIntent.putExtra("emotion","AllSongs");
                startActivityForResult(mIntent,1);
            }
        });

    }

}