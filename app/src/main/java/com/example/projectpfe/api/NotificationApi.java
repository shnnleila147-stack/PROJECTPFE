package com.example.projectpfe.api;

import com.example.projectpfe.NotificationModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {

    @GET("notifications/{userId}")
    Call<List<NotificationModel>> getNotifications(@Path("userId") long userId);
    @GET("notifications/unread/{userId}")
    Call<Boolean> hasUnread(@Path("userId") long userId);

    @POST("notifications/seen/{userId}")
    Call<Void> markSeen(@Path("userId") long userId);
}