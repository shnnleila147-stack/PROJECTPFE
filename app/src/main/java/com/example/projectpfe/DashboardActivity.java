package com.example.projectpfe;

import android.os.Bundle;

public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupNotificationBell(); // ✅
        setupBottomNav(3);       // ✅ الأيقونة 4 مفعّلة
    }
}