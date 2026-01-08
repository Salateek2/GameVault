package com.example.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamevault.R;
import com.example.gamevault.model.GameResult;


import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamevault.R;
import com.example.gamevault.model.GameResult;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private List<GameResult> games;

    public GameAdapter(List<GameResult> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GameResult game = games.get(position);
        holder.name.setText(game.name);
        holder.year.setText("Year: " + game.getYear());

        // טוען תמונה עם Glide לתוך ה-ImageView
        Glide.with(holder.imageGame.getContext())
                .load(game.getBackgroundImage())  // הנחה שיש שדה כזה ב-GameResult

                .placeholder(android.R.color.darker_gray)  // צבע רקע למשל
                .into(holder.imageGame);      // במקרה שהטעינה נכשל

    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, year;
        ImageView imageGame;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvGameName);
            year = itemView.findViewById(R.id.tvGameYear);
            imageGame = itemView.findViewById(R.id.imageGame);
        }
    }
}


