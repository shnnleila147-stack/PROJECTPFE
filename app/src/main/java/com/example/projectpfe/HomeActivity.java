package com.example.projectpfe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectpfe.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        setupClickListeners();
    }

    private void setupUI() {
        // يمكنك تغيير اسم المستخدم هنا
        binding.tvUserName.setText("RAYANE");
        binding.tvHello.setText("Hello,");
        binding.tvProgressPercent.setText("75%");
        binding.tvTimeFocus.setText("4.2 hrs");
        binding.tvStreak.setText("12 Days");
        binding.tvFocusSubject.setText("Advanced\nMathematics");
    }

    private void setupClickListeners() {
        // زر START SESSION
        binding.btnStartSession.setOnClickListener(v -> {
            // هنا يمكنك الانتقال لصفحة الجلسة
            // Intent intent = new Intent(this, SessionActivity.class);
            // startActivity(intent);
        });

        // زر VIEW ALL
        binding.tvViewAll.setOnClickListener(v -> {
            // هنا يمكنك الانتقال لصفحة جميع الأهداف
        });

        // الزر العائم (FAB)
        binding.fabAdd.setOnClickListener(v -> {
            // هنا يمكنك إضافة هدف جديد
        });

        // أيقونة الإشعارات
        binding.ivNotification.setOnClickListener(v -> {
            // هنا يمكنك الانتقال لصفحة الإشعارات
        });
    }
}
