package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TodoActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnSave;
    EditText etGoal, etTopic, etTime, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        setupNotificationBell();
        setupBottomNav(1);

        tabToDo       = findViewById(R.id.tabToDo);
        tabDoing      = findViewById(R.id.tabDoing);
        tabDone       = findViewById(R.id.tabDone);
        btnSave       = findViewById(R.id.btnSave);
        etGoal        = findViewById(R.id.etGoal);
        etTopic       = findViewById(R.id.etTopic);
        etTime        = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);

        tabToDo.setOnClickListener(v -> {});
        tabDoing.setOnClickListener(v -> {
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });
        tabDone.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        btnSave.setOnClickListener(v -> {
            String goal        = etGoal.getText().toString().trim();
            String topic       = etTopic.getText().toString().trim();
            String time        = etTime.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (goal.isEmpty()) {
                Toast.makeText(this, "Please enter a goal!", Toast.LENGTH_SHORT).show();
                return;
            }

            // حفظ البيانات
            getSharedPreferences("todo_data", MODE_PRIVATE).edit()
                    .putString("goal", goal)
                    .putString("topic", topic)
                    .putString("time", time)
                    .putString("description", description)
                    .apply();

            // تحديث عداد المهام
            SharedPreferences stats = getSharedPreferences("task_stats", MODE_PRIVATE);
            int count = stats.getInt("todo_count", 0);
            stats.edit().putInt("todo_count", count + 1).apply();

            startActivity(new Intent(this, TodoAiActivity.class));
        });
    }
}