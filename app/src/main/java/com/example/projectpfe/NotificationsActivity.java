package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // ✅ زر CONTINUE NOW → يرجع للـ Dashboard
        Button btnContinueNow = findViewById(R.id.btnContinueNow);
        btnContinueNow.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });

        // ✅ زر LATER → يغلق الإشعارات
        Button btnLater = findViewById(R.id.btnLater);
        btnLater.setOnClickListener(v -> finish());

        // ✅ زر INITIALIZE PROTOCOL
        Button btnInitialize = findViewById(R.id.btnInitialize);
        btnInitialize.setOnClickListener(v -> {
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });
    }
}