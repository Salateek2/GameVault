package com.example.gamevault;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamevault.adapter.GameAdapter;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerFavorites;
    private GameAdapter adapter;
    private List<SingleGame> favoriteGames = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button buttonClearLibrary = view.findViewById(R.id.buttonClearLibrary);

        buttonClearLibrary.setOnClickListener(v -> {
            clearLibrary();
        });

        recyclerFavorites = view.findViewById(R.id.recyclerFavorites);
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GameAdapter(favoriteGames, game -> {
            // Optionally handle clicks on favorite games here (e.g., open details)
        });

        recyclerFavorites.setAdapter(adapter);

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        String userId = ((MainActivity) requireActivity()).getUserId();

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    favoriteGames.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SingleGame game = doc.toObject(SingleGame.class);
                        if (game != null) {
                            favoriteGames.add(game);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (favoriteGames.isEmpty()) {
                        Toast.makeText(getContext(), "No games in your library yet", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "Error loading favorites", e);
                });
    }
    private void clearLibrary() {
        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Library cleared!", Toast.LENGTH_SHORT).show();
                                // Optionally refresh UI here to show empty library
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to clear library: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch library: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}



