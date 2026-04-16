package com.example.projectpfe.api;

import com.example.projectpfe.model.PersonalizationRequest;
import com.example.projectpfe.model.PersonalizationResponse;
import com.example.projectpfe.model.PlanResponse;
import com.example.projectpfe.model.User;

import okhttp3.RequestBody;
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

    @PUT("api/users/{id}/personalized")
    Call<User> setUserPersonalized(@Path("id") long userId);

    // 🔥 التعديل هنا
    @POST("/api/personalization/save-and-personalize")
    Call<PersonalizationResponse> saveAnswersAndSetPersonalized(@Body PersonalizationRequest request);

    @POST("api/plan/generate")
    Call<PlanResponse> generatePlan(@Body RequestBody body);
}