package com.example.projectpfe.api;

import com.example.projectpfe.model.User;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProfileApi {

    @GET("api/profile/{userId}/bigfive")
    Call<Map<String, Integer>> getBigFive(@Path("userId") Long userId);
    @GET("api/profile/{userId}")
    Call<User> getUser(@Path("userId") Long userId);
}