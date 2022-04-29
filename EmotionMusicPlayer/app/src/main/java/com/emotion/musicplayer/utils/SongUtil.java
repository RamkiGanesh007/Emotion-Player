package com.emotion.musicplayer.utils;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.emotion.musicplayer.model.Song;
import com.emotion.musicplayer.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SongUtil {
    private ArrayList<Song> mysongs;
    Map<String, Object> userobj;
    // Functionalities

    //1.Storing into database
    //2.fetching list based on emotion
    //3.upload song

    public SongUtil()
    {
        dbfire = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }
    private FirebaseFirestore dbfire ;
    private StorageReference storageReference;


    public Task<UploadTask.TaskSnapshot> storeSong(String userId,String songName, Uri uri, String emotion) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Songs").child(uri.getLastPathSegment());

        return storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong = uriTask.getResult();
                String songUrl = urlSong.toString();

                storeSongDetails(userId,new Song(songName, songUrl, emotion));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Uploading Song Failed");
            }
        });
    }

    public void storeSongDetails(String UserId,Song songObj) {
        UserUtil userUtil=new UserUtil();
        Task<QuerySnapshot> k = dbfire.collection("Users").whereEqualTo("userId", UserId).get();

        k.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<User> u;
                u=(ArrayList<User>) k.getResult().toObjects(User.class);
                User userObj=u.get(0);
                mysongs=userObj.getMySongs();
                mysongs.add(songObj);
                userObj.setMySongs(mysongs);
                userUtil.getDocRef(UserId).update("favouriteSongs",userObj.getFavouriteSongs()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                userUtil.getDocRef(UserId).update("mySongs",mysongs).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

//                userUtil.updateUserList(UserId,userObj.getFavouriteSongs(),mysongs);
            }
        });
        return ;
    }

    public Task<QuerySnapshot> fetchAllSongs(String UserId) {
        return dbfire.collection("Songs").get();
    }

    public Task<QuerySnapshot> fetchSongsByEmotion(String emotion) {
        return dbfire.collection("Songs")
                .whereEqualTo("emotion", emotion).get();
    }

}
