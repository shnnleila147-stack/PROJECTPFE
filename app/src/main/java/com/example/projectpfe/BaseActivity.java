package com.example.projectpfe;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    // ✅ هذه الدالة تعمل الـ Bottom Bar في كل الواجهات
    protected void setupBottomNav(int activeIndex) {
        LinearLayout bottomNavBar = findViewById(R.id.bottomNavBar);
        if (bottomNavBar == null) return;

        // الأيقونة 1 - Home
        bottomNavBar.getChildAt(0).setOnClickListener(v -> {
            if (activeIndex != 0) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });

        // الأيقونة 2 - TODO
        bottomNavBar.getChildAt(1).setOnClickListener(v -> {
            if (activeIndex != 1) {
                startActivity(new Intent(this, TodoActivity.class));
                finish();
            }
        });

        // الأيقونة 3 - Community
        bottomNavBar.getChildAt(2).setOnClickListener(v -> {
            if (activeIndex != 2) {
                startActivity(new Intent(this, CommunityActivity.class));
                finish();
            }
        });

        // الأيقونة 4 - Dashboard
        bottomNavBar.getChildAt(3).setOnClickListener(v -> {
            if (activeIndex != 3) {
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            }
        });

        // الأيقونة 5 - Profile
        bottomNavBar.getChildAt(4).setOnClickListener(v -> {
            if (activeIndex != 4) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
        });
    }

    // ✅ هذه الدالة تعمل الجرس في كل الواجهات
    protected void setupNotificationBell() {
        ImageView ivNotification = findViewById(R.id.ivNotification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v ->
                    startActivity(new Intent(this, NotificationsActivity.class))
            );
        }
    }
}
