package com.example.gamevault;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerFavorites;
    private GameAdapter adapter;
    private List<SingleGame> favoriteGames = new ArrayList<>();
    private ListenerRegistration favoritesListener;

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
            showClearLibraryConfirmation();
        });

        recyclerFavorites = view.findViewById(R.id.recyclerFavorites);
        recyclerFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new GameAdapter(favoriteGames, game -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("game", game);
            bundle.putBoolean("isFromLibrary", true);

            GameDetails detailFragment = new GameDetails();
            detailFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerFavorites.setAdapter(adapter);

        setupFavoritesListener();

        return view;
    }

    private void showClearLibraryConfirmation() {
        if (favoriteGames.isEmpty()) {
            Toast.makeText(getContext(), "Library is already empty", Toast.LENGTH_SHORT).show();
            return;
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear Library")
                .setMessage("Are you sure you want to remove all games from your library?")
                .setPositiveButton("Clear All", (dialog, which) -> clearLibrary())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupFavoritesListener() {
        String userId = ((MainActivity) requireActivity()).getUserId();

        if (userId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        favoritesListener = db.collection("users")
                .document(userId)
                .collection("favorites")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("ProfileFragment", "Error listening to favorites", error);
                        return;
                    }

                    if (value != null) {
                        favoriteGames.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            SingleGame game = doc.toObject(SingleGame.class);
                            if (game != null) {
                                favoriteGames.add(game);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void clearLibrary() {
        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId == null) return;

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
                                Toast.makeText(getContext(), "Library cleared successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to clear library: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (favoritesListener != null) {
            favoritesListener.remove();
        }
    }
}
