package com.example.gamevault;

import com.google.gson.annotations.SerializedName;

public class Screenshot {

    @SerializedName("id")
    public int id;

    @SerializedName("image")
    public String imageUrl;
}
