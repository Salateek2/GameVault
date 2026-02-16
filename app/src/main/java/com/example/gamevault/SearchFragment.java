package com.example.gamevault;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamevault.adapter.GameAdapter;
import com.example.gamevault.model.GameResponse;
import com.example.gamevault.network.RawgApi;
import com.example.gamevault.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {

    private static final String API_KEY = "415d86e2c1bb4892be23a624f1955b6e";

    private Spinner spinnerGenre, spinnerPlatform, spinnerSort;
    private RecyclerView recyclerView;
    private Button buttonSearch;
    private ProgressBar progressBar;
    private TextInputEditText editSearchQuery;

    private GameAdapter adapter;
    private List<SingleGame> gamesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        spinnerGenre = view.findViewById(R.id.spinnerGenre);
        spinnerPlatform = view.findViewById(R.id.spinnerPlatform);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        recyclerView = view.findViewById(R.id.recyclerSearch);
        progressBar = view.findViewById(R.id.progressBarSearch);
        editSearchQuery = view.findViewById(R.id.editSearchQuery);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSpinners();

        buttonSearch.setOnClickListener(v -> performSearch());

        if (!gamesList.isEmpty()) {
            setupRecyclerView();
        }

        return view;
    }

    private void setupSpinners() {
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Action", "RPG", "Adventure", "Shooter", "Strategy"});
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"All", "PC", "PlayStation", "Xbox"});
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlatform.setAdapter(platformAdapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Rating", "Popularity", "Release date"});
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
    }

    private void performSearch() {
        String query = editSearchQuery.getText() != null ? editSearchQuery.getText().toString().trim() : "";
        String genre = spinnerGenre.getSelectedItem().toString();
        String platform = spinnerPlatform.getSelectedItem().toString();
        String sort = spinnerSort.getSelectedItem().toString();

        String genreParam = mapGenre(genre);
        String platformParam = mapPlatform(platform);
        String sortParam = mapSort(sort);
        
        // Use query string if not empty, otherwise null
        String searchParam = query.isEmpty() ? null : query;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        buttonSearch.setEnabled(false);

        RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

        api.searchGames(API_KEY, searchParam, genreParam, platformParam, sortParam, 40)
                .enqueue(new Callback<GameResponse>() {
                    @Override
                    public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                        if (!isAdded()) return;
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        buttonSearch.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            gamesList.clear();
                            if (response.body().results != null) {
                                gamesList.addAll(response.body().results);
                            }
                            setupRecyclerView();
                            if (gamesList.isEmpty()) {
                                Toast.makeText(getContext(), "No games found matching criteria", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("SearchFragment", "Response failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GameResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        buttonSearch.setEnabled(true);
                        Toast.makeText(getContext(), "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupRecyclerView() {
        if (adapter == null) {
            adapter = new GameAdapter(gamesList, game -> {
                Bundle b = new Bundle();
                b.putParcelable("game", game);

                GameDetails f = new GameDetails();
                f.setArguments(b);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_container, f)
                        .addToBackStack(null)
                        .commit();
            });
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private String mapGenre(String genre) {
        switch (genre) {
            case "Action": return "action";
            case "RPG": return "role-playing-games-rpg";
            case "Adventure": return "adventure";
            case "Shooter": return "shooter";
            case "Strategy": return "strategy";
            default: return null;
        }
    }

    private String mapPlatform(String platform) {
        switch (platform) {
            case "PC": return "4";
            case "PlayStation": return "18,187,19,16,15,27"; // Multiple PS platforms
            case "Xbox": return "1,186,14,80"; // Multiple Xbox platforms
            default: return null;
        }
    }

    private String mapSort(String sort) {
        switch (sort) {
            case "Rating": return "-rating";
            case "Popularity": return "-added";
            case "Release date": return "-released";
            default: return "-rating";
        }
    }
}
