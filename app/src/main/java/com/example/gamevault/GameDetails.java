package com.example.gamevault;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gamevault.adapter.ScreenshotAdapter;
import com.example.gamevault.network.RawgApi;
import com.example.gamevault.network.RetrofitClient;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameDetails extends Fragment {
    private static final String API_KEY = "415d86e2c1bb4892be23a624f1955b6e";

    private RecyclerView recyclerScreenshots;
    private ScreenshotAdapter screenshotAdapter;
    private List<String> screenshotUrls = new ArrayList<>();


    private ImageView gameImage;
    private Button btnSaveToLibrary;
    private TextView textPlatforms;

    private SingleGame currentGame;
    private TextView gameTitle;
    private TextView gameDescription;
    private ProgressBar progressBar;
    private boolean isFromLibrary = false;

    public GameDetails() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game_details, container, false);
        btnSaveToLibrary = view.findViewById(R.id.buttonSaveGame);
        textPlatforms = view.findViewById(R.id.textPlatforms);

        gameImage = view.findViewById(R.id.imageGameCover);
        gameTitle = view.findViewById(R.id.textGameTitle);
        gameDescription = view.findViewById(R.id.textDescription);
        
        progressBar = view.findViewById(R.id.progressBar);

        recyclerScreenshots = view.findViewById(R.id.recyclerScreenshots);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerScreenshots.setLayoutManager(layoutManager);
        screenshotAdapter = new ScreenshotAdapter(getContext(), screenshotUrls);
        recyclerScreenshots.setAdapter(screenshotAdapter);


        Bundle args = getArguments();
        if (args != null) {
            SingleGame basicGame = args.getParcelable("game");
            isFromLibrary = args.getBoolean("isFromLibrary", false);

            if (isFromLibrary) {
                btnSaveToLibrary.setText("Delete from Library");
                btnSaveToLibrary.setOnClickListener(v -> deleteGameFromLibrary());
            } else {
                btnSaveToLibrary.setOnClickListener(v -> saveGameToLibrary());
            }

            if (basicGame != null) {
                gameTitle.setText(basicGame.getName());

                Glide.with(this)
                        .load(basicGame.getBackgroundImage())
                        .into(gameImage);

                loadGameDetails(basicGame.getId());
                loadScreenshots(basicGame.getId());
                this.currentGame = basicGame;
                
                if (!isFromLibrary) {
                    checkIfGameInLibrary(basicGame.getId());
                }
            }
        }

        return view;
    }

    private void checkIfGameInLibrary(int gameId) {
        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId == null) return;

        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(gameId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {
                        btnSaveToLibrary.setText("In Library");
                        btnSaveToLibrary.setEnabled(false);
                    }
                })
                .addOnFailureListener(e -> Log.e("GameDetails", "Error checking library", e));
    }

    private void loadScreenshots(int gameId) {
        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

        api.getGameScreenshots(gameId, API_KEY).enqueue(new Callback<ScreenshotResponse>() {
            @Override
            public void onResponse(Call<ScreenshotResponse> call, Response<ScreenshotResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    screenshotUrls.clear();
                    for (Screenshot screenshot : response.body().results) {
                        screenshotUrls.add(screenshot.imageUrl);
                    }
                    screenshotAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ScreenshotResponse> call, Throwable t) {
                Log.e("GameDetails", "Failed to load screenshots", t);
            }
        });
    }

    private void saveGameToLibrary() {
        if (currentGame == null) return;

        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User ID not ready. Please try again in a moment.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Visual feedback immediately
        btnSaveToLibrary.setEnabled(false);
        btnSaveToLibrary.setText("In Library");

        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(currentGame.getId()))
                .set(currentGame)
                .addOnSuccessListener(aVoid -> {
                    // Force the update to happen on the UI thread and check if fragment is still attached
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            btnSaveToLibrary.setText("In Library");
                            btnSaveToLibrary.setEnabled(false);
                            Toast.makeText(getContext(), currentGame.getName() + " added to library!", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            btnSaveToLibrary.setEnabled(true);
                            btnSaveToLibrary.setText("Save to My Library");
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                    }
                    Log.e("GameDetails", "Failed to save to library", e);
                });
    }

    private void deleteGameFromLibrary() {
        if (currentGame == null) return;

        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId == null) return;

        btnSaveToLibrary.setEnabled(false);
        btnSaveToLibrary.setText("Deleted successfully!");

        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(currentGame.getId()))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), currentGame.getName() + " removed from library", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        btnSaveToLibrary.setEnabled(true);
                        btnSaveToLibrary.setText("Delete from Library");
                        Toast.makeText(getContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadGameDetails(int gameId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);
        api.getGameDetails(gameId, API_KEY)
                .enqueue(new Callback<SingleGame>() {
                    @Override
                    public void onResponse(Call<SingleGame> call, Response<SingleGame> response) {
                        if (!isAdded()) return;
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            SingleGame fullGame = response.body();

                            if (fullGame.getDescription() != null && !fullGame.getDescription().isEmpty()) {
                                gameDescription.setText(fullGame.getDescription());
                                // Update current game with full description if needed
                                if (currentGame != null) {
                                    currentGame.setDescription(fullGame.getDescription());
                                }
                            } else {
                                gameDescription.setText("No description available.");
                            }

                            if (fullGame.getPlatforms() != null) {
                                StringBuilder platformsString = new StringBuilder();
                                for (SingleGame.PlatformWrapper p : fullGame.getPlatforms()) {
                                    if (p.platform != null && p.platform.name != null) {
                                        if (platformsString.length() > 0) platformsString.append(", ");
                                        platformsString.append(p.platform.name);
                                    }
                                }
                                textPlatforms.setText("Platforms: " + platformsString.toString());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleGame> call, Throwable t) {
                        if (isAdded()) {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            gameDescription.setText("Failed to load game details.");
                        }
                    }
                });
    }
}
