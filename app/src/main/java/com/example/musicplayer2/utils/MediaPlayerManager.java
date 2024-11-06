package com.example.musicplayer2.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class MediaPlayerManager {
    private static final String TAG = "MediaPlayerManager";
    private MediaPlayer mediaPlayer;
    private OnPreparedListener onPreparedListener;

    public interface OnPreparedListener {
        void onPrepared();
    }

    public MediaPlayerManager() {
        this.mediaPlayer = new MediaPlayer();
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    public void playMusicFromUrl(Context context, String fileUrl) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse(fileUrl));
            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                if (onPreparedListener != null) {
                    onPreparedListener.onPrepared();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "Error playing music", e);
        }
    }

    public void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    public void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}