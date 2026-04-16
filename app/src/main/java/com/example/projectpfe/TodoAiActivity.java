package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TodoAiActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnChoosePlan1, btnChoosePlan2;

    TextView tvPlan1Title, tvPlan1Desc;
    TextView tvPlan2Title, tvPlan2Desc;

    TextView tvPlan1Step1, tvPlan1Step2, tvPlan1Step3;
    TextView tvPlan2Step1, tvPlan2Step2, tvPlan2Step3;

    ProgressBar progressBar;

    long userId;
    String topic, goal, time, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_ai);

        setupNotificationBell();
        setupBottomNav(1);

        // Views
        tabToDo = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone = findViewById(R.id.tabDone);

        btnChoosePlan1 = findViewById(R.id.btnChoosePlan1);
        btnChoosePlan2 = findViewById(R.id.btnChoosePlan2);

        tvPlan1Title = findViewById(R.id.tvPlan1Title);
        tvPlan1Desc = findViewById(R.id.tvPlan1Desc);
        tvPlan1Step1 = findViewById(R.id.tvPlan1Step1);
        tvPlan1Step2 = findViewById(R.id.tvPlan1Step2);
        tvPlan1Step3 = findViewById(R.id.tvPlan1Step3);

        tvPlan2Title = findViewById(R.id.tvPlan2Title);
        tvPlan2Desc = findViewById(R.id.tvPlan2Desc);
        tvPlan2Step1 = findViewById(R.id.tvPlan2Step1);
        tvPlan2Step2 = findViewById(R.id.tvPlan2Step2);
        tvPlan2Step3 = findViewById(R.id.tvPlan2Step3);

        progressBar = findViewById(R.id.progressBarAi);

        // SharedPreferences
        SharedPreferences todoPrefs = getSharedPreferences("todo_data", MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);

        goal = todoPrefs.getString("goal", "");
        topic = todoPrefs.getString("topic", "general");
        time = todoPrefs.getString("time", "");
        description = todoPrefs.getString("description", "");

        userId = userPrefs.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
            return;
        }

        // Tabs
        tabToDo.setOnClickListener(v -> startActivity(new Intent(this, TodoActivity.class)));
        tabDoing.setOnClickListener(v -> startActivity(new Intent(this, DoingActivity.class)));
        tabDone.setOnClickListener(v -> startActivity(new Intent(this, DoneActivity.class)));

        // Generate plans
        generatePlans();

        // Buttons
        btnChoosePlan1.setOnClickListener(v -> {
            sendFeedback("plan1");
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });

        btnChoosePlan2.setOnClickListener(v -> {
            sendFeedback("plan2");
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });
    }

    // 🔥 Generate Plans
    private void generatePlans() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.12:8080/api/plan/generate");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("userId", userId);
                body.put("topic", topic);
                body.put("goal", goal);
                body.put("time", time);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                Scanner scanner = new Scanner(conn.getInputStream());
                String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    parseResponse(response);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // 🔥 Parse JSON
    private void parseResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);

            JSONObject plan1 = json.getJSONObject("plan1");
            JSONObject plan2 = json.getJSONObject("plan2");

            tvPlan1Title.setText(plan1.getString("title"));
            tvPlan1Desc.setText(plan1.getString("description"));

            JSONArray steps1 = plan1.getJSONArray("steps");
            setSteps(steps1, tvPlan1Step1, tvPlan1Step2, tvPlan1Step3);

            tvPlan2Title.setText(plan2.getString("title"));
            tvPlan2Desc.setText(plan2.getString("description"));

            JSONArray steps2 = plan2.getJSONArray("steps");
            setSteps(steps2, tvPlan2Step1, tvPlan2Step2, tvPlan2Step3);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Parsing error", Toast.LENGTH_LONG).show();
        }
    }

    private void setSteps(JSONArray steps, TextView s1, TextView s2, TextView s3) {
        s1.setText("• " + steps.optString(0, ""));
        s2.setText("• " + steps.optString(1, ""));
        s3.setText("• " + steps.optString(2, ""));
    }

    // 🔥 Send Feedback
    private void sendFeedback(String planKey) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.12:8080/api/plan/feedback");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("userId", userId);
                body.put("topic", topic); // 🔥 أهم سطر
                body.put("chosenPlan", planKey);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                conn.getResponseCode(); // فقط لإرسال الطلب

                Log.d("FEEDBACK", "Sent");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("FEEDBACK", "Error");
            }
        }).start();
    }
}