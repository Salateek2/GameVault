package com.example.gamevault;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ---- Getters ----
    public int getId() { return id; }
    public String getName() { return name; }
    public String getReleased() { return released; }
    public String getBackgroundImage() { return backgroundImage; }
    public float getRating() { return rating; }
    public String getDescription() {
        return description;
    }
}
