package com.example.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamevault.R;
import com.example.gamevault.SingleGame;


import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    public interface OnGameClickListener {
        void onGameClick(SingleGame game);
    }

    private List<SingleGame> games;
    private OnGameClickListener listener;

    public GameAdapter(List<SingleGame> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
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
        SingleGame game = games.get(position);

        holder.name.setText(game.getName());
        holder.year.setText(game.getReleased());

        Glide.with(holder.imageGame.getContext())
                .load(game.getBackgroundImage())
                .placeholder(android.R.color.darker_gray)
                .into(holder.imageGame);

        holder.itemView.setOnClickListener(v -> listener.onGameClick(game));
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
