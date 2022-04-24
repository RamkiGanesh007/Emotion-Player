package com.emotion.musicplayer.model;

public class Song {
    private String songName,songUrl,emotion;

    public Song() {
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", emotion='" + emotion + '\'' +
                '}';
    }

    public Song(String songName, String songUrl, String emotion) {
        this.songName = songName;
        this.songUrl = songUrl;
        this.emotion = emotion;
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
