package com.example.musicplayer2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer2.R;
import com.example.musicplayer2.models.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private Context context;
    private List<Playlist> playlistList;
    private OnPlaylistClickListener onPlaylistClickListener;

    // Constructor
    public PlaylistAdapter(Context context, List<Playlist> playlistList, OnPlaylistClickListener listener) {
        this.context = context;
        this.playlistList = playlistList;
        this.onPlaylistClickListener = listener;
    }

    // ViewHolder class
    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        public TextView playlistNameTextView;

        public PlaylistViewHolder(View itemView, final OnPlaylistClickListener listener) {
            super(itemView);
            playlistNameTextView = itemView.findViewById(R.id.playlist_name); // Ensure this matches your layout

            // Handle playlist item click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onPlaylistClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each playlist item
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        return new PlaylistViewHolder(view, onPlaylistClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        // Bind data to the playlist item
        Playlist playlist = playlistList.get(position);
        holder.playlistNameTextView.setText(playlist.getName()); // Ensure getName() method exists in Playlist model
    }

    @Override
    public int getItemCount() {
        return playlistList.size(); // Return the total number of playlists
    }

    // Interface for handling playlist item clicks
    public interface OnPlaylistClickListener {
        void onPlaylistClick(int position);
    }
}
