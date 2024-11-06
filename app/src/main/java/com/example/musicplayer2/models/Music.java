package com.example.musicplayer2.models;


public class Music {
    private String musicId;
    private String title;
    private String artist;
    private String fileUrl; // Firebase Storage URL
    private String uploaderId; // The user who uploaded the music

    public Music() {
        // Empty constructor for Firebase
    }

    public Music(String musicId, String title, String artist, String fileUrl, String uploaderId) {
        this.musicId = musicId;
        this.title = title;
        this.artist = artist;
        this.fileUrl = fileUrl;
        this.uploaderId = uploaderId;
    }

    // Getters and Setters
    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }
}


