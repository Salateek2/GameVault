package com.example.gamevault.model;

import com.google.gson.annotations.SerializedName;

public class GameResult {

    @SerializedName("name")
    public String name;

    @SerializedName("released")
    public String released;

    @SerializedName("background_image")
    public String backgroundImage;

    public String getYear() {
        if (released != null && released.length() >= 4) {
            return released.substring(0, 4);
        }
        return "N/A";
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }
}
