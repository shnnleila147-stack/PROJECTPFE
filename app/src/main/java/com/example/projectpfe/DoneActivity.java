package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DoneActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        setupNotificationBell(); // ✅
        setupBottomNav(1);       // ✅

        tabToDo  = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone  = findViewById(R.id.tabDone);

        tabToDo.setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });
        tabDoing.setOnClickListener(v -> {
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });
        tabDone.setOnClickListener(v -> {});

        Button btnCompleteCheckin = findViewById(R.id.btnCompleteCheckin);
        btnCompleteCheckin.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}