package com.example.gamevault;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamevault.adapter.GameAdapter;
import com.example.gamevault.model.GameResponse;
import com.example.gamevault.model.GameResult;
import com.example.gamevault.network.RawgApi;
import com.example.gamevault.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BestGamesFragment extends Fragment {

    private static final String API_KEY = "415d86e2c1bb4892be23a624f1955b6e";

    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<GameResult> gamesList = new ArrayList<>();

    public BestGamesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_best_games, container, false);

        recyclerView = view.findViewById(R.id.recyclerBestGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadBestGames();

        return view;
    }

    private void loadBestGames() {
        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

        api.getBestGamesOfYear(
                API_KEY,
                "2024-01-01,2024-12-31",
                "-rating",
                40
        ).enqueue(new Callback<GameResponse>() {

            @Override
            public void onResponse(Call<GameResponse> call,
                                   Response<GameResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<GameResult> results = response.body().results;

                    if (results != null && !results.isEmpty()) {
                        gamesList.clear();
                        gamesList.addAll(results);

                        adapter = new GameAdapter(gamesList);
                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    Log.e("BestGamesFragment", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.e("BestGamesFragment", "API call failed", t);
            }
        });
    }
}
