package com.example.musicplayer2.models;


import java.util.List;

public class Playlist {
    private String playlistId;
    private String name;
    private String ownerId; // User who created the playlist
    private List<String> songIds; // List of song IDs in the playlist

    public Playlist() {
        // Empty constructor for Firebase
    }

    public Playlist(String playlistId, String name, String ownerId, List<String> songIds) {
        this.playlistId = playlistId;
        this.name = name;
        this.ownerId = ownerId;
        this.songIds = songIds;
    }

    // Getters and Setters
    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }
}

