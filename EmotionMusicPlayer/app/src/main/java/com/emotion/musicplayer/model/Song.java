package com.emotion.musicplayer.model;

import java.io.Serializable;
import java.util.Comparator;

public class Song implements Serializable {
    private String songName;

    public Song(String songName, String songUrl, String emotion, String isFavourite) {
        this.songName = songName;
        this.songUrl = songUrl;
        this.emotion = emotion;
        this.isFavourite = isFavourite;
    }

    private String songUrl;
    private String emotion;
    private String isFavourite;

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", emotion='" + emotion + '\'' +
                ", isFavourite='" + isFavourite + '\'' +
                '}';
    }
    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }


    public Song() {
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }
}
