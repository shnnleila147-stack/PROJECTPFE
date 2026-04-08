package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DoingActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing);

        setupNotificationBell(); // ✅
        setupBottomNav(1);       // ✅

        tabToDo  = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone  = findViewById(R.id.tabDone);

        tabToDo.setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });
        tabDoing.setOnClickListener(v -> {});
        tabDone.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        Button btnPauseTimer = findViewById(R.id.btnPauseTimer);
        btnPauseTimer.setOnClickListener(v ->
                startActivity(new Intent(this, DoingSessionActivity.class))
        );
    }
}