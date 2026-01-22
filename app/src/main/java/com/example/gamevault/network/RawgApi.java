package com.example.gamevault.network;

import com.example.gamevault.ScreenshotResponse;
import com.example.gamevault.SingleGame;
import com.example.gamevault.model.GameResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RawgApi {

    @GET("games")
    Call<GameResponse> getGames(
            @Query("key") String apiKey,
            @Query("page") int page,
            @Query("page_size") int pageSize
    );

    // Existing version (DO NOT REMOVE – used elsewhere)
    @GET("games")
    Call<GameResponse> getBestGamesOfYear(
            @Query("key") String apiKey,
            @Query("dates") String dates,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize
    );

    // ✅ NEW version with rating (fixes your error)
    @GET("games")
    Call<GameResponse> getBestGamesOfYear(
            @Query("key") String apiKey,
            @Query("dates") String dates,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize,
            @Query("rating") String rating
    );

    @GET("games")
    Call<GameResponse> searchGames(
            @Query("key") String apiKey,
            @Query("genres") String genres,
            @Query("platforms") String platforms,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize
    );

    @GET("games/{id}/screenshots")
    Call<ScreenshotResponse> getGameScreenshots(
            @Path("id") int gameId,
            @Query("key") String apiKey
    );



    @GET("games")
    Call<GameResponse> searchGames(
            @Query("key") String apiKey,
            @Query("search") String search,
            @Query("genres") String genres,
            @Query("platforms") String platforms,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize
    );

    @GET("games/{id}")
    Call<SingleGame> getGameDetails(
            @Path("id") int gameId,
            @Query("key") String apiKey
    );
}
