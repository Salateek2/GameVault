package com.example.gamevault;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ScreenshotResponse {
    @SerializedName("results")
    public List<Screenshot> results;
}
