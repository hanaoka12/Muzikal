package com.example.musicplayer2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.musicplayer2.Activity.EditProfileActivity;
import com.example.musicplayer2.Activity.LoginActivity;
import com.example.musicplayer2.Activity.PlayerActivity;
import com.example.musicplayer2.R;
import com.example.musicplayer2.adapters.MusicAdapter;
import com.example.musicplayer2.models.Music;
import com.example.musicplayer2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements MusicAdapter.OnMusicClickListener {
    private TextView userNameText;
    private CircleImageView profileImageView;
    private Button logoutButton, editProfileButton;
    private RecyclerView userMusicRecyclerView;
    private ProgressBar progressBar;
    private MusicAdapter musicAdapter;
    private List<Music> userMusicList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupListeners();
        loadUserData();
        loadUserMusic();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Reload user data when returning to the fragment
    }

    private void initializeViews(View view) {
        userNameText = view.findViewById(R.id.userNameText);
        profileImageView = view.findViewById(R.id.profileImageView);
        logoutButton = view.findViewById(R.id.logoutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        userMusicRecyclerView = view.findViewById(R.id.userMusicRecyclerView);
        progressBar = view.findViewById(R.id.profileProgressBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        userMusicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(getContext(), userMusicList, this);
        userMusicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userMusicRecyclerView.setAdapter(musicAdapter);
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            requireActivity().finish();
        });

        editProfileButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        userNameText.setText(user.getName() != null ? user.getName() : "User");

                        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                            Glide.with(this)
                                    .load(user.getImageUrl())
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile)
                                    .into(profileImageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void loadUserMusic() {
        if (mAuth.getCurrentUser() == null) return;

        progressBar.setVisibility(View.VISIBLE);
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("music")
                .whereEqualTo("uploaderId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userMusicList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot document : queryDocumentSnapshots) {
                        Music music = document.toObject(Music.class);
                        if (music != null) {
                            userMusicList.add(music);
                        }
                    }
                    musicAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    // Handle error
                });
    }

    @Override
    public void onMusicClick(int position) {
        if (position >= 0 && position < userMusicList.size()) {
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("MUSIC_URL", userMusicList.get(position).getFileUrl());
            intent.putExtra("MUSIC_TITLE", userMusicList.get(position).getTitle());
            intent.putExtra("MUSIC_ARTIST", userMusicList.get(position).getArtist());
            startActivity(intent);
        }
    }
}