package com.example.gamevault;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SingleGame implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("released")
    private String released;

    @SerializedName("background_image")
    private String backgroundImage;

    @SerializedName("rating")
    private float rating;

    @SerializedName("description_raw")
    private String description;

    @SerializedName("platforms")
    private List<PlatformWrapper> platforms;

    public static class PlatformWrapper {
        @SerializedName("platform")
        public Platform platform;
    }
    public static class Platform {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;

        @SerializedName("slug")
        public String slug;
    }

    // ---- Constructor ----
    public SingleGame(int id, String name, String released, String backgroundImage, float rating) {
        this.id = id;
        this.name = name;
        this.released = released;
        this.backgroundImage = backgroundImage;
        this.rating = rating;
    }

    // ---- Parcelable constructor ----
    protected SingleGame(Parcel in) {
        id = in.readInt();
        name = in.readString();
        released = in.readString();
        backgroundImage = in.readString();
        rating = in.readFloat();
        description = in.readString(); // Added description to parcel
    }
    public SingleGame() {
        // Empty constructor required for Firestore deserialization
    }

    public static final Creator<SingleGame> CREATOR = new Creator<SingleGame>() {
        @Override
        public SingleGame createFromParcel(Parcel in) {
            return new SingleGame(in);
        }

        @Override
        public SingleGame[] newArray(int size) {
            return new SingleGame[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(released);
        dest.writeString(backgroundImage);
        dest.writeFloat(rating);
        dest.writeString(description); // Added description to parcel
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ---- Getters & Setters ----
    public int getId() { return id; }
    public String getName() { return name; }
    public List<PlatformWrapper> getPlatforms() {
        return platforms;
    }
    public String getReleased() { return released; }
    public String getBackgroundImage() { return backgroundImage; }
    public float getRating() { return rating; }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
