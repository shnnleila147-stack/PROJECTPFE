package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

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

            // ✅ جلب userId
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);

            // ✅ إنشاء JSON
            JSONObject json = new JSONObject();
            try {
                json.put("userId", userId);
                json.put("goal", goal);
                json.put("topic", topic);
                json.put("time", time);
                json.put("description", description);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // ⚠️ غيري IP حسب جهازك
            String url = "http://192.168.1.25:8080/api/todo";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    json,

                    response -> {
                        Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();

                        // ✅ نمرر البيانات لـ ToDoAI
                        Intent intent = new Intent(this, TodoAiActivity.class);
                        intent.putExtra("goal", goal);
                        intent.putExtra("topic", topic);
                        intent.putExtra("time", time);
                        intent.putExtra("description", description);
                        startActivity(intent);
                    },

                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);
        });
}}