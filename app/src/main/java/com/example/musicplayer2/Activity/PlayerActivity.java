package com.example.musicplayer2.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer2.R;
import com.example.musicplayer2.utils.MediaPlayerManager;

public class PlayerActivity extends AppCompatActivity {

    private TextView titleTextView, artistTextView, currentTimeTextView, totalTimeTextView;
    private ImageButton btnPlayPause, btnPrevious, btnNext, btnShuffle, btnRepeat, btnBack;
    private SeekBar seekBar;
    private String musicUrl, musicTitle, musicArtist;
    private MediaPlayerManager mediaPlayerManager;
    private Handler handler;
    private Runnable updateSeekBar;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initializeViews();
        setupClickListeners();
        loadMusicData();
        setupMediaPlayer();
        setupSeekBarUpdate();
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.musicTitleTextView);
        artistTextView = findViewById(R.id.musicArtistTextView);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnBack = findViewById(R.id.btnBack);
        seekBar = findViewById(R.id.seekBar);
    }

    private void setupClickListeners() {
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnBack.setOnClickListener(v -> finish());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayerManager.seekTo(progress);
                    updateTimeLabels();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadMusicData() {
        musicUrl = getIntent().getStringExtra("MUSIC_URL");
        musicTitle = getIntent().getStringExtra("MUSIC_TITLE");
        musicArtist = getIntent().getStringExtra("MUSIC_ARTIST");

        titleTextView.setText(musicTitle);
        artistTextView.setText(musicArtist);
    }

    private void setupMediaPlayer() {
        mediaPlayerManager = new MediaPlayerManager();
        mediaPlayerManager.playMusicFromUrl(this, musicUrl);
        isPlaying = true;
        updatePlayPauseButton();
    }

    private void setupSeekBarUpdate() {
        handler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayerManager != null) {
                    int currentPosition = mediaPlayerManager.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    updateTimeLabels();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(updateSeekBar);
    }

    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayerManager.pauseMusic();
        } else {
            mediaPlayerManager.resumeMusic();
        }
        isPlaying = !isPlaying;
        updatePlayPauseButton();
    }

    private void updatePlayPauseButton() {
        btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateTimeLabels() {
        int currentPosition = mediaPlayerManager.getCurrentPosition();
        int duration = mediaPlayerManager.getDuration();

        currentTimeTextView.setText(formatTime(currentPosition));
        totalTimeTextView.setText(formatTime(duration));
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(updateSeekBar);
        }
        if (mediaPlayerManager != null) {
            mediaPlayerManager.releasePlayer();
        }
    }
}