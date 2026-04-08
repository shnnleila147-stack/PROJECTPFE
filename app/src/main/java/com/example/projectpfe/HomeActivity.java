package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout bottomNavBar = findViewById(R.id.bottomNavBar);

        // الأيقونة 1 - Home (نحن هنا)
        bottomNavBar.getChildAt(0).setOnClickListener(v -> {});

        // الأيقونة 2 - TODO
        bottomNavBar.getChildAt(1).setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
        });

        // الأيقونة 3 - Community
        bottomNavBar.getChildAt(2).setOnClickListener(v -> {});

        // الأيقونة 4 - Dashboard
        bottomNavBar.getChildAt(3).setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
        });

        // الأيقونة 5 - Profile
        bottomNavBar.getChildAt(4).setOnClickListener(v -> {

            // ✅ قراءة userId من SharedPreferences
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);

            if (userId != -1) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", userId); // تمرير userId
                startActivity(intent);
            } else {
                // إذا لم يوجد userId (مثلاً لم يتم تسجيل الدخول بعد)
                // يمكنك توجيه المستخدم إلى LoginActivity
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }
}