package com.emotion.musicplayer.model;

import java.util.ArrayList;

public class User {

    public User()
    {

    }

    public User(String userId,ArrayList<Song> favouriteSongs, ArrayList<Song> mySongs) {
        UserId=userId;
        FavouriteSongs = favouriteSongs;
        MySongs = mySongs;
    }

    private ArrayList<Song> FavouriteSongs;

    @Override
    public String toString() {
        return "User{" +
                "FavouriteSongs=" + FavouriteSongs +
                ", MySongs=" + MySongs +
                ", UserId='" + UserId + '\'' +
                '}';
    }

    private ArrayList<Song> MySongs;
    private String UserId;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


    public User(String userId) {
        UserId=userId;
    }



    public ArrayList<Song> getFavouriteSongs() {
        return FavouriteSongs;
    }

    public void setFavouriteSongs(ArrayList<Song> favouriteSongs) {
        FavouriteSongs = favouriteSongs;
    }

    public ArrayList<Song> getMySongs() {
        return MySongs;
    }

    public void setMySongs(ArrayList<Song> mySongs) {
        MySongs = mySongs;
    }

}
