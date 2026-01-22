package com.example.gamevault.network;

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
    @GET("games")
    Call<GameResponse> getBestGamesOfYear(
            @Query("key") String apiKey,
            @Query("dates") String dates,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize
    );

    @GET("games")
    Call<GameResponse> getBestGamesOfYear(
            @Query("key") String apiKey,
            @Query("dates") String dateRange,
            @Query("ordering") String ordering,
            @Query("page_size") int pageSize,
            @Query("genres") String genres  // note this added parameter!
    );

    @GET("games/{id}")
    Call<SingleGame> getGameDetails(
            @Path("id") int gameId,
            @Query("key") String apiKey
    );

}
