package com.example.musicplayer2.utils;

import android.net.Uri;
import android.util.Log;

import com.example.musicplayer2.models.Music;
import com.example.musicplayer2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseUtils {

    private static final String TAG = "FirebaseUtils";
    private static final String MUSIC_COLLECTION = "music";
    private static final String PLAYLIST_COLLECTION = "playlists";
    private static final String USER_COLLECTION = "users";
    // Firebase instances
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    /**
     * Uploads music to Firebase Storage and retrieves the download URL.
     *
     * @param fileUri            the URI of the music file
     * @param title              the title of the music
     * @param artist             the artist of the music
     * @param onCompleteListener listener for when the upload is complete
     */
    public static void uploadMusic(Uri fileUri, String title, String artist, OnCompleteListener<Uri> onCompleteListener) {
        String userId = auth.getCurrentUser().getUid();
        String musicId = db.collection(MUSIC_COLLECTION).document().getId();
        StorageReference storageReference = storage.getReference().child("music/" + musicId);

        Log.d(TAG, "Uploading music for user: " + userId);

        // Upload the music file
        storageReference.putFile(fileUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Music upload failed: ", task.getException());
                        throw task.getException();
                    }
                    // Return the file URL after upload is complete
                    return storageReference.getDownloadUrl();
                })
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get download URL: ", e));
    }

    /**
     * Saves music metadata to Firestore.
     *
     * @param title              the title of the music
     * @param artist             the artist of the music
     * @param fileUrl            the URL of the uploaded music file
     * @param onCompleteListener listener for when the save operation is complete
     */
    public static void saveMusicData(String title, String artist, String fileUrl, OnCompleteListener<Void> onCompleteListener) {
        String userId = auth.getCurrentUser().getUid();
        String musicId = db.collection(MUSIC_COLLECTION).document().getId();

        // Create a Music object and save it in Firestore
        Music music = new Music(musicId, title, artist, fileUrl, userId);
        Log.d(TAG, "Saving music metadata: " + title + " by " + artist);

        db.collection(MUSIC_COLLECTION).document(musicId).set(music)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e(TAG, "Error saving music data: ", e));
    }

    /**
     * Retrieves the music collection from Firestore.
     *
     * @return the Firestore collection reference
     */
    public static CollectionReference getMusicCollection() {
        return db.collection(MUSIC_COLLECTION);
    }

    /**
     * Loads music data from Firestore.
     *
     * @param onCompleteListener listener for when the music data is retrieved
     */
    public static void loadMusicFromFirestore(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getMusicCollection().get()
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load music from Firestore: ", e));
    }
    public static void createUserProfile(String userId, String email, String name, OnCompleteListener<Void> listener) {
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setPlaylistIds(new ArrayList<>()); // Initialize empty lists
        newUser.setUploadedSongs(new ArrayList<>());
        newUser.setImageUrl(""); // Default empty image URL

        db.collection(USER_COLLECTION)
                .document(userId)
                .set(newUser)
                .addOnCompleteListener(listener)
                .addOnFailureListener(e -> Log.e(TAG, "Error creating user profile: ", e));
    }

    public static void checkUserExists(String userId, OnCompleteListener<Boolean> listener) {
        db.collection(USER_COLLECTION)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = task.getResult() != null && task.getResult().exists();
                        listener.onComplete(Tasks.forResult(exists));
                    } else {
                        listener.onComplete(Tasks.forResult(false));
                    }
                });
    }

    /**
     * Adds a new playlist to Firestore.
     *
     * @param playlistName       the name of the playlist
     * @param onCompleteListener listener for when the playlist is created
     */
    public static void addPlaylist(String playlistName, OnCompleteListener<Void> onCompleteListener) {
        String userId = auth.getCurrentUser().getUid();
        String playlistId = db.collection(PLAYLIST_COLLECTION).document().getId();

        // Create a playlist entry
        Map<String, Object> playlistData = new HashMap<>();
        playlistData.put("playlistId", playlistId);
        playlistData.put("name", playlistName);
        playlistData.put("ownerId", userId);

        db.collection(PLAYLIST_COLLECTION).document(playlistId).set(playlistData)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(e -> Log.e(TAG, "Error creating playlist: ", e));
    }
}
