package com.example.gamevault;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamevault.adapter.GameAdapter;
import com.example.gamevault.model.GameResponse;

import com.example.gamevault.network.RawgApi;
import com.example.gamevault.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGamesFragment extends Fragment {

    private static final String API_KEY = "415d86e2c1bb4892be23a624f1955b6e";

    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<SingleGame> gamesList = new ArrayList<>();

    public NewGamesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_games, container, false);

        recyclerView = view.findViewById(R.id.recyclerNewGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadNewGames();

        return view;
    }

    private void loadNewGames() {
        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

        api.getBestGamesOfYear(
                API_KEY,
                "2024-01-01,2024-12-31",
                "-released",
                40
        ).enqueue(new Callback<GameResponse>() {

            @Override
            public void onResponse(Call<GameResponse> call,
                                   Response<GameResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<SingleGame> results = response.body().results;

                    if (results != null && !results.isEmpty()) {
                        gamesList.clear();
                        gamesList.addAll(results);

                        adapter = new GameAdapter(gamesList, game -> {

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("game", game);

                            GameDetails detailFragment = new GameDetails();
                            detailFragment.setArguments(bundle);

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_container, detailFragment)
                                    .addToBackStack(null)
                                    .commit();
                        });

                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    Log.e("NewGamesFragment", "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GameResponse> call, Throwable t) {
                Log.e("NewGamesFragment", "API call failed", t);
            }
        });
    }
}
