package com.example.gamevault;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gamevault.adapter.GameAdapter;
import com.example.gamevault.model.GameResponse;
import com.example.gamevault.network.RawgApi;
import com.example.gamevault.network.RetrofitClient;

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

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            setupSpinners();

            buttonSearch.setOnClickListener(v -> performSearch());

            return view;
        }

        private void setupSpinners() {
            spinnerGenre.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"All", "Action", "RPG", "Adventure", "Shooter"}));

            spinnerPlatform.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"All", "PC", "PlayStation", "Xbox"}));

            spinnerSort.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"Rating", "Popularity", "Release date"}));
        }

        // ✅ THIS METHOD BELONGS HERE
        private void performSearch() {

            String genre = spinnerGenre.getSelectedItem().toString();
            String platform = spinnerPlatform.getSelectedItem().toString();
            String sort = spinnerSort.getSelectedItem().toString();

            String genreParam = genre.equals("All") ? null : genre.toLowerCase();
            String platformParam = mapPlatform(platform);
            String sortParam = mapSort(sort);

            RawgApi api = RetrofitClient.getInstance().create(RawgApi.class);

            api.searchGames(API_KEY, genreParam, platformParam, sortParam, 40)
                    .enqueue(new Callback<GameResponse>() {

                        @Override
                        public void onResponse(Call<GameResponse> call, Response<GameResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                gamesList.clear();
                                gamesList.addAll(response.body().results);

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
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GameResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Search failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        private String mapPlatform(String platform) {
            switch (platform) {
                case "PC": return "4";
                case "PlayStation": return "18";
                case "Xbox": return "1";
                default: return null;
            }
        }

        private String mapSort(String sort) {
            switch (sort) {
                case "Rating": return "-rating";
                case "Popularity": return "-added";
                case "Release date": return "-released";
                default: return null;
            }
        }
    }
