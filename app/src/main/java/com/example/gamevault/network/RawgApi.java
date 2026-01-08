package com.example.gamevault.network;

import com.example.gamevault.adapter.GameAdapter;
import com.example.gamevault.model.GameResponse;
import com.example.gamevault.model.GameResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
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

}
