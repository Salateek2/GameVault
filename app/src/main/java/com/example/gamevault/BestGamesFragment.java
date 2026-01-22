package com.example.gamevault;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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


public class BestGamesFragment extends Fragment {

    private static final String API_KEY = "415d86e2c1bb4892be23a624f1955b6e";

    private Spinner spinnerGenre, spinnerSort;
    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<SingleGame> gamesList = new ArrayList<>();

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

        spinnerGenre = view.findViewById(R.id.spinnerGenre);
        spinnerSort = view.findViewById(R.id.spinnerSort);

        setupSpinners();

        loadBestGames();  // initial load

        return view;
    }

    private void setupSpinners() {
        String[] genres = {
                "All",
                "Action",
                "RPG",
                "Adventure",
                "Shooter",
                "Strategy"
        };
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                genres
        );
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        String[] sortOptions = {
                "Popularity",
                "Rating",
                "Release Date",
                "Name"
        };
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                sortOptions
        );
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadBestGames();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        };

        spinnerGenre.setOnItemSelectedListener(listener);
        spinnerSort.setOnItemSelectedListener(listener);
    }

    private void loadBestGames() {
        String selectedGenre = spinnerGenre.getSelectedItem().toString();
        String selectedSort = spinnerSort.getSelectedItem().toString();

        String genreParam = selectedGenre.equals("All") ? null : selectedGenre.toLowerCase();

        String sortParam;
        switch (selectedSort) {
            case "Popularity":
                sortParam = "-added";
                break;
            case "Rating":
                sortParam = "-rating";
                break;
            case "Release Date":
                sortParam = "-released";
                break;
            case "Name":
                sortParam = "name";
                break;
            default:
                sortParam = "-rating";
        }

        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);
        api.getBestGamesOfYear(
                API_KEY,
                "2024-01-01,2024-12-31",
                sortParam,
                40,
                genreParam
        ).enqueue(new Callback<GameResponse>() {
            @Override
            public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SingleGame> results = response.body().results;
                    if (results != null && !results.isEmpty()) {
                        gamesList.clear();
                        gamesList.addAll(results);

                        if (adapter == null) {
                            adapter = new GameAdapter(gamesList, game -> {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("game", game);

                                GameDetails detailFragment = new GameDetails();
                                detailFragment.setArguments(bundle);

                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.main_container, detailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            });
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
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
