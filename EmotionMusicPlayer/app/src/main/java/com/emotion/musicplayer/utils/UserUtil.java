package com.emotion.musicplayer.utils;


import androidx.annotation.NonNull;

import com.emotion.musicplayer.model.Song;
import com.emotion.musicplayer.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserUtil {
    //1.Storing User into database
    //2.Checking if user exist in data base
    //3.updating list of the user into database  i.e fav song and mysongs list

    private FirebaseFirestore dbfire ;
    private SongUtil songUtil;
    private ArrayList<Song> mysongs;

    public UserUtil()
    {
        dbfire = FirebaseFirestore.getInstance();
        songUtil=new SongUtil();
    }

    public void storeUser(String UserId) {

        Task<QuerySnapshot> Allsongstask= songUtil.fetchAllSongs(UserId);

        Allsongstask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mysongs =(ArrayList<Song>) task.getResult().toObjects(Song.class);
                User user =new User(UserId,new ArrayList<Song>(),mysongs);
                dbfire.collection("Users").document(UserId).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        });
    }

    public Query fetchAllSongs(String UserId) {
        return dbfire.collection("Users").whereEqualTo("userId",UserId);
    }


    public boolean checkUser(String UserId)
    {
        return dbfire.collection("Users").whereEqualTo("UserId",UserId).equals(UserId);
    }

    public void updateUserList(String UserId, ArrayList<Song> fav, ArrayList<Song> mysongs)
    {
        // Update an existing document
        DocumentReference docRef = dbfire.collection("Users").document(UserId);
        docRef.update("FavouriteSongs",fav);
        docRef.update("MySongs",mysongs);
    }

    public DocumentReference getUserDetails(String UserId)
    {
        return dbfire.collection("Users").document(UserId);
    }
}
