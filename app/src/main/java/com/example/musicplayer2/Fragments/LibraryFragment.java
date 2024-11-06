package com.example.musicplayer2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer2.Activity.PlayerActivity;
import com.example.musicplayer2.R;
import com.example.musicplayer2.adapters.MusicAdapter;
import com.example.musicplayer2.models.Music;
import com.example.musicplayer2.utils.FirebaseUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment implements MusicAdapter.OnMusicClickListener {

    private RecyclerView musicRecyclerView;
    private ProgressBar progressBar;
    private MusicAdapter musicAdapter;
    private List<Music> musicList;

    private static final String TAG = "LibraryFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        musicRecyclerView = view.findViewById(R.id.musicRecyclerView);
        progressBar = view.findViewById(R.id.libraryProgressBar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        musicRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        musicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(requireContext(), musicList, this);
        musicRecyclerView.setAdapter(musicAdapter);

        loadMusic();
    }

    private void loadMusic() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUtils.getMusicCollection().get().addOnCompleteListener(task -> {
            if (isAdded()) {  // Check if Fragment is still attached to Activity
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful() && task.getResult() != null) {
                    handleMusicLoadSuccess(task);
                } else {
                    handleMusicLoadFailure(task);
                }
            }
        }).addOnFailureListener(e -> {
            if (isAdded()) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error loading music: ", e);
                Toast.makeText(requireContext(), "Failed to load music library", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleMusicLoadSuccess(Task<QuerySnapshot> task) {
        musicList.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            try {
                Music music = document.toObject(Music.class);
                if (music != null && music.getFileUrl() != null) {
                    musicList.add(music);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing music data: ", e);
            }
        }
        musicAdapter.notifyDataSetChanged();

        if (musicList.isEmpty()) {
            Toast.makeText(requireContext(), "No music available in the library", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleMusicLoadFailure(Task<QuerySnapshot> task) {
        Exception e = task.getException();
        if (e != null) {
            Log.e(TAG, "Music load failed: ", e);
        }
        Toast.makeText(requireContext(), "Failed to load music library", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMusicClick(int position) {
        if (position >= 0 && position < musicList.size()) {
            Intent intent = new Intent(requireContext(), PlayerActivity.class);
            intent.putExtra("MUSIC_URL", musicList.get(position).getFileUrl());
            intent.putExtra("MUSIC_TITLE", musicList.get(position).getTitle());
            intent.putExtra("MUSIC_ARTIST", musicList.get(position).getArtist());
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Invalid music selection", Toast.LENGTH_SHORT).show();
        }
    }
}