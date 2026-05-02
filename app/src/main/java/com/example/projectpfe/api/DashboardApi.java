package com.example.projectpfe.api;
import com.example.projectpfe.model.HomeResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface DashboardApi {
    @GET("api/home/{userId}")
    Call<HomeResponse> getHome(@Path("userId") Long userId);}