package com.example.projectpfe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        LinearLayout bottomNavBar = findViewById(R.id.bottomNavBar);

        // الأيقونة 1 - Home
        bottomNavBar.getChildAt(0).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // الأيقونة 2 - TODO
        bottomNavBar.getChildAt(1).setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });

        // الأيقونة 3
        bottomNavBar.getChildAt(2).setOnClickListener(v -> {});

        // الأيقونة 4 - Dashboard (نحن هنا)
        bottomNavBar.getChildAt(3).setOnClickListener(v -> {});

        // الأيقونة 5 - Profile
        bottomNavBar.getChildAt(4).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });
    }
}