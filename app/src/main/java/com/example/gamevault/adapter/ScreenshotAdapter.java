package com.example.gamevault.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamevault.R;
import com.github.chrisbanes.photoview.PhotoView;

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
        // Using dp for dimensions (converted from px roughly)
        float density = context.getResources().getDisplayMetrics().density;
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams((int)(200 * density), (int)(120 * density));
        params.setMargins((int)(8 * density), 0, (int)(8 * density), 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenshotAdapter.ViewHolder holder, int position) {
        String url = screenshots.get(position);
        Glide.with(context).load(url).into((ImageView) holder.itemView);

        holder.itemView.setOnClickListener(v -> showFullScreenImage(url));
    }

    private void showFullScreenImage(String url) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_full_screen_image, null);
        dialog.setContentView(view);

        PhotoView photoView = view.findViewById(R.id.fullScreenImageView);
        ImageView btnClose = view.findViewById(R.id.btnClose);

        Glide.with(context).load(url).into(photoView);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
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
