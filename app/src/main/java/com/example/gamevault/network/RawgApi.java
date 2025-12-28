package com.example.gamevault.network;

import com.example.gamevault.model.GameResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RawgApi {

    @GET("games")
    Call<GameResponse> getGames(
            @Query("key") String apiKey,
            @Query("page_size") int pageSize
    );
}
