package com.example.projectpfe.api;

import com.example.projectpfe.model.PersonalizationRequest;
import com.example.projectpfe.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/users/register")
    Call<User> register(@Body User user);

    @POST("api/users/login")
    Call<User> login(@Body User user);

    @POST("api/personalization")

    Call<Void> saveAnswers(@Body PersonalizationRequest request);

    // ✅ الجديد (مهم)
    @PUT("api/users/{id}/personalized")
    Call<User> setPersonalized(@Path("id") int id);
}