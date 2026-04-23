package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

            // ✅ ⭐ الحل المهم: تخزين البيانات محليًا
            SharedPreferences.Editor editor = getSharedPreferences("todo_data", MODE_PRIVATE).edit();
            editor.putString("goal", goal);
            editor.putString("topic", topic);
            editor.putString("time", time);
            editor.putString("description", description);
            editor.apply();

            // ✅ جلب userId
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);

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

            String url = "http://192.168.1.3:8080/api/todo";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    json,

                    response -> {
                        Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();

                        // ❗ لم نعد بحاجة Intent extras
                        startActivity(new Intent(this, TodoAiActivity.class));
                    },

                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);
        });
    }
}