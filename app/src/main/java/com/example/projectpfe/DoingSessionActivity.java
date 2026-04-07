package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DoingSessionActivity extends AppCompatActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnDayCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_session);

        tabToDo  = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone  = findViewById(R.id.tabDone);
        btnDayCompleted = findViewById(R.id.btnDayCompleted);

        tabToDo.setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });

        tabDoing.setOnClickListener(v -> {
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });

        tabDone.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        // ✅ زر DAY COMPLETED → ينتقل لـ DoneActivity
        btnDayCompleted.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });
    }
}