package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TodoActivity extends AppCompatActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnSave;
    EditText etGoal, etTopic, etTime, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        // ✅ الجرس - مباشرة بعد setContentView
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        // ===== التابات =====
        tabToDo  = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone  = findViewById(R.id.tabDone);

        // ===== الحقول =====
        btnSave       = findViewById(R.id.btnSave);
        etGoal        = findViewById(R.id.etGoal);
        etTopic       = findViewById(R.id.etTopic);
        etTime        = findViewById(R.id.etTime);
        etDescription = findViewById(R.id.etDescription);

        // ===== التنقل بين التابات =====
        tabToDo.setOnClickListener(v -> {
            // نحن هنا بالفعل
        });

        tabDoing.setOnClickListener(v -> {
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });

        tabDone.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        // ===== زر SAVE =====
        btnSave.setOnClickListener(v -> {
            String goal = etGoal.getText().toString().trim();
            if (goal.isEmpty()) {
                Toast.makeText(this, "Please enter a goal!", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, TodoAiActivity.class));
        });
    }
}