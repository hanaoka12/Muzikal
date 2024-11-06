package com.example.musicplayer2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayer2.Activity.PlayerActivity;
import com.example.musicplayer2.R;
import com.example.musicplayer2.adapters.MusicAdapter;
import com.example.musicplayer2.models.Music;
import com.example.musicplayer2.utils.FirebaseUtils;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MusicAdapter.OnMusicClickListener {
    private RecyclerView musicRecyclerView;
    private ProgressBar progressBar;
    private MusicAdapter musicAdapter;
    private List<Music> musicList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        musicRecyclerView = view.findViewById(R.id.homeRecyclerView);
        progressBar = view.findViewById(R.id.homeProgressBar);

        musicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        musicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(getContext(), musicList, this);
        musicRecyclerView.setAdapter(musicAdapter);

        loadMusic();

        return view;
    }

    private void loadMusic() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUtils.getMusicCollection()
                .orderBy("uploadTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        musicList.clear();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                            Music music = document.toObject(Music.class);
                            if (music != null && music.getFileUrl() != null) {
                                musicList.add(music);
                            }
                        }
                        musicAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load music", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMusicClick(int position) {
        if (position >= 0 && position < musicList.size()) {
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("MUSIC_URL", musicList.get(position).getFileUrl());
            intent.putExtra("MUSIC_TITLE", musicList.get(position).getTitle());
            intent.putExtra("MUSIC_ARTIST", musicList.get(position).getArtist());
            startActivity(intent);
        }
    }
}
