package com.example.gamevault.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

    private final List<String> screenshots;
    private final Context context;

    public ScreenshotAdapter(Context context, List<String> screenshots) {
        this.context = context;
        this.screenshots = screenshots;
    }

    @NonNull
    @Override
    public ScreenshotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(200, 120);
        params.setMargins(8, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenshotAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(screenshots.get(position)).into((ImageView) holder.itemView);
    }

    @Override
    public int getItemCount() {
        return screenshots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}