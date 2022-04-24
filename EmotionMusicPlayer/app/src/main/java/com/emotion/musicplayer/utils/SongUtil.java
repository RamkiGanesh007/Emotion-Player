package com.emotion.musicplayer.utils;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.emotion.musicplayer.model.Song;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SongUtil {
    // Functionalities

    //1.Storing into database
    //2.fetching list based on emotion
    //3.upload song
    private FirebaseFirestore dbfire = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    public Task<UploadTask.TaskSnapshot> storeSong(String songName, Uri uri, String emotion) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Songs").child(uri.getLastPathSegment());

        return storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlSong = uriTask.getResult();
                String songUrl = urlSong.toString();

                storeSongDetails(songName, songUrl, emotion);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Uploading Song Failed");
            }
        });
    }

    public Task<Void> storeSongDetails(String songName, String songUrl, String emotion) {
        Song songObj = new Song(songName,songUrl,emotion);

        return dbfire.collection("Songs").document(UUID.randomUUID().toString()).set(songObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public Task<QuerySnapshot> fetchAllSongs() {
        return dbfire.collection("Songs").get();
    }

    public Task<QuerySnapshot> fetchSongsByEmotion(String emotion) {
        return dbfire.collection("Songs")
                .whereEqualTo("emotion", emotion).get();
    }

}
