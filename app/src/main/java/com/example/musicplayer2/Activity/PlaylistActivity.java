package com.example.musicplayer2.Activity;



import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer2.R;
import com.example.musicplayer2.adapters.PlaylistAdapter;
import com.example.musicplayer2.models.Playlist;
import com.example.musicplayer2.utils.FirebaseUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements PlaylistAdapter.OnPlaylistClickListener {

    private RecyclerView playlistRecyclerView;
    private ProgressBar progressBar;
    private PlaylistAdapter playlistAdapter;
    private List<Playlist> playlistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        playlistRecyclerView = findViewById(R.id.playlistRecyclerView);
        progressBar = findViewById(R.id.playlistProgressBar);

        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playlistList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(this, playlistList, this);
        playlistRecyclerView.setAdapter(playlistAdapter);

        loadPlaylists();
    }

    private void loadPlaylists() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUtils.getMusicCollection().get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                playlistList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Playlist playlist = document.toObject(Playlist.class);
                    playlistList.add(playlist);
                }
                playlistAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(PlaylistActivity.this, "Failed to load playlists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPlaylistClick(int position) {
        // Handle playlist click event (e.g., open playlist or play its contents)
        Toast.makeText(this, "Playlist clicked: " + playlistList.get(position).getName(), Toast.LENGTH_SHORT).show();
    }
}
