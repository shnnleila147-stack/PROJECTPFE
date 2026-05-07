package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectpfe.api.ApiClient;
import com.example.projectpfe.api.NotificationApi;

public class BaseActivity extends AppCompatActivity {

    ImageView ivNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // =========================
    // 🔔 Notification Bell
    // =========================
    protected void setupNotificationBell() {

        ivNotification = findViewById(R.id.ivNotification);

        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> {
                startActivity(new Intent(this, NotificationsActivity.class));

                // 🔥 مهم: لا نحذف مباشرة هنا (حتى لا يرجع bug)
                // الحذف يتم داخل NotificationsActivity فقط
            });
        }
    }

    // =========================
    // 🔴 Badge (النقطة الحمراء)
    // =========================

    protected void updateNotificationBadge() {

        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);
        long userId = userPrefs.getLong("user_id", -1);

        View badge = findViewById(R.id.notificationBadge);
        if (badge == null) return;

        NotificationApi api = ApiClient.getClient().create(NotificationApi.class);

        api.hasUnread(userId).enqueue(new retrofit2.Callback<Boolean>() {

            @Override
            public void onResponse(retrofit2.Call<Boolean> call,
                                   retrofit2.Response<Boolean> response) {

                Boolean hasUnread = response.body();

                if (hasUnread != null && hasUnread) {
                    badge.setVisibility(View.VISIBLE);
                } else {
                    badge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Boolean> call, Throwable t) {
                badge.setVisibility(View.GONE);
            }
        });
    }

    // =========================
    // 🔥 فقط لتحديث الحالة يدوياً إذا احتجنا
    // =========================
    protected void markNotificationsAsSeen() {

        SharedPreferences prefs = getSharedPreferences("notifications", MODE_PRIVATE);
        prefs.edit().putBoolean("has_notifications", false).apply();

        updateNotificationBadge();
    }

    // =========================
    // 🔥 مهم جداً: تحديث عند الرجوع لأي شاشة
    // =========================
    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    // =========================
    // Bottom Navigation (بدون تغيير)
    // =========================
    protected void setupBottomNav(int activeIndex) {
        LinearLayout bottomNavBar = findViewById(R.id.bottomNavBar);
        if (bottomNavBar == null) return;

        bottomNavBar.getChildAt(0).setOnClickListener(v -> {
            if (activeIndex != 0) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

        bottomNavBar.getChildAt(1).setOnClickListener(v -> {
            if (activeIndex != 1) {
                startActivity(new Intent(this, TodoActivity.class));
                finish();
            }
        });

        bottomNavBar.getChildAt(2).setOnClickListener(v -> {
            if (activeIndex != 2) {
                startActivity(new Intent(this, CommunityActivity.class));
                finish();
            }
        });

        bottomNavBar.getChildAt(3).setOnClickListener(v -> {
            if (activeIndex != 3) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }
        });

        bottomNavBar.getChildAt(4).setOnClickListener(v -> {
            if (activeIndex != 4) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
        });
    }
}