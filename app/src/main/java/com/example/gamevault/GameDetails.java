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

        recyclerScreenshots = view.findViewById(R.id.recyclerScreenshots);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerScreenshots.setLayoutManager(layoutManager);
        screenshotAdapter = new ScreenshotAdapter(getContext(), screenshotUrls);
        recyclerScreenshots.setAdapter(screenshotAdapter);



        SingleGame basicGame = getArguments().getParcelable("game");

        if (basicGame != null) {
            gameTitle.setText(basicGame.getName());

            Glide.with(this)
                    .load(basicGame.getBackgroundImage())
                    .into(gameImage);

            loadGameDetails(basicGame.getId());
            loadScreenshots(basicGame.getId());
            this. currentGame = basicGame;
        }
        btnSaveToLibrary.setOnClickListener(v -> saveGameToLibrary());

        return view;
    }
    private void loadScreenshots(int gameId) {
        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

        api.getGameScreenshots(gameId, API_KEY).enqueue(new Callback<ScreenshotResponse>() {
            @Override
            public void onResponse(Call<ScreenshotResponse> call, Response<ScreenshotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    screenshotUrls.clear();
                    for (Screenshot screenshot : response.body().results) {
                        screenshotUrls.add(screenshot.imageUrl);
                    }
                    screenshotAdapter.notifyDataSetChanged();
                } else {
                    Log.e("GameDetails", "No screenshots found or response failed");
                }
            }

            @Override
            public void onFailure(Call<ScreenshotResponse> call, Throwable t) {
                Log.e("GameDetails", "Failed to load screenshots", t);
            }
        });
    }

    private void saveGameToLibrary() {
        if (currentGame == null) {
            Toast.makeText(getContext(), "No game to save", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated yet", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(String.valueOf(currentGame.getId()))  // Assuming game ID is unique integer
                .set(currentGame)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Game saved to your library!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save game: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadGameDetails(int gameId) {

        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

        api.getGameDetails(gameId, API_KEY)
                .enqueue(new Callback<SingleGame>() {

                    @Override
                    public void onResponse(Call<SingleGame> call,
                                           Response<SingleGame> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            SingleGame fullGame = response.body();

                            if (fullGame.getDescription() != null &&
                                    !fullGame.getDescription().isEmpty()) {
                                gameDescription.setText(fullGame.getDescription());
                            } else {
                                gameDescription.setText("No description available.");
                            }

                            if (fullGame.getPlatforms() != null && !fullGame.getPlatforms().isEmpty()) {
                                StringBuilder platformsString = new StringBuilder();
                                for (SingleGame.PlatformWrapper p : fullGame.getPlatforms()) {
                                    if (p.platform != null && p.platform.name != null) {
                                        if (platformsString.length() > 0) {
                                            platformsString.append(", ");
                                        }
                                        platformsString.append(p.platform.name);
                                    }
                                }
                                textPlatforms.setText("Platforms: " + platformsString.toString());
                            } else {
                                textPlatforms.setText("Platforms: Unknown");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SingleGame> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("GameDetailFragment", "Failed to load details", t);
                        gameDescription.setText("Failed to load game details.");
                    }
                });
    }



}