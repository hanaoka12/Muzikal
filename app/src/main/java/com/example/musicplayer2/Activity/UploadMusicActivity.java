package com.example.musicplayer2.Activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicplayer2.R;
import com.example.musicplayer2.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class UploadMusicActivity extends AppCompatActivity {

    private static final int PICK_MUSIC_FILE = 1;

    private EditText titleEditText, artistEditText;
    private Button chooseFileButton, uploadButton;
    private ProgressBar progressBar;

    private Uri musicFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_music);

        titleEditText = findViewById(R.id.titleEditText);
        artistEditText = findViewById(R.id.artistEditText);
        chooseFileButton = findViewById(R.id.chooseFileButton);
        uploadButton = findViewById(R.id.uploadButton);
        progressBar = findViewById(R.id.uploadProgressBar);

        chooseFileButton.setOnClickListener(v -> openFileChooser());
        uploadButton.setOnClickListener(v -> uploadMusic());
    }

    // Open file chooser to select a music file
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_MUSIC_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MUSIC_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            musicFileUri = data.getData();
            Toast.makeText(this, "Music file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadMusic() {
        String title = titleEditText.getText().toString().trim();
        String artist = artistEditText.getText().toString().trim();

        if (title.isEmpty() || artist.isEmpty() || musicFileUri == null) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Upload file to Firebase Storage
        FirebaseUtils.uploadMusic(musicFileUri, title, artist, task -> {
            if (task.isSuccessful()) {
                String fileUrl = task.getResult().toString();
                // Save music data in Firestore
                FirebaseUtils.saveMusicData(title, artist, fileUrl, saveTask -> {
                    progressBar.setVisibility(View.GONE);
                    if (saveTask.isSuccessful()) {
                        Toast.makeText(UploadMusicActivity.this, "Music uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UploadMusicActivity.this, "Failed to save music data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UploadMusicActivity.this, "Failed to upload music file", Toast.LENGTH_SHORT).show();
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
            }
        });
    }
}
