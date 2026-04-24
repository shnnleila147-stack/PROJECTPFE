package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    JSONObject plan1Data, plan2Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_ai);

        setupNotificationBell();
        setupBottomNav(1);

        initViews();
        loadData();
        setupTabs();
        generatePlans();
        setupButtons();
    }

    // ---------------- INIT VIEWS ----------------
    private void initViews() {

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
    }

    // ---------------- LOAD DATA ----------------
    private void loadData() {

        SharedPreferences todoPrefs = getSharedPreferences("todo_data", MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);

        goal = todoPrefs.getString("goal", "");
        topic = todoPrefs.getString("topic", "general");
        time = todoPrefs.getString("time", "");
        description = todoPrefs.getString("description", "");

        userId = userPrefs.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
        }
    }

    // ---------------- TABS ----------------
    private void setupTabs() {

        tabToDo.setOnClickListener(v ->
                startActivity(new Intent(this, TodoActivity.class)));

        tabDoing.setOnClickListener(v ->
                startActivity(new Intent(this, DoingActivity.class)));

        tabDone.setOnClickListener(v ->
                startActivity(new Intent(this, DoneActivity.class)));
    }

    // ---------------- BUTTONS ----------------
    private void setupButtons() {

        btnChoosePlan1.setOnClickListener(v -> {

            if (plan1Data == null) {
                Toast.makeText(this, "Wait for AI plans...", Toast.LENGTH_SHORT).show();
                return;
            }

            choosePlan(plan1Data);
        });

        btnChoosePlan2.setOnClickListener(v -> {

            if (plan2Data == null) {
                Toast.makeText(this, "Wait for AI plans...", Toast.LENGTH_SHORT).show();
                return;
            }

            choosePlan(plan2Data);
        });
    }

    // ---------------- GENERATE PLANS ----------------
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

    // ---------------- PARSE RESPONSE ----------------
    private void parseResponse(String response) {

        try {
            JSONObject json = new JSONObject(response);

            plan1Data = json.getJSONObject("plan1");
            plan2Data = json.getJSONObject("plan2");

            tvPlan1Title.setText(plan1Data.getString("title"));
            tvPlan1Desc.setText(plan1Data.getString("description"));

            setSteps(plan1Data.getJSONArray("steps"),
                    tvPlan1Step1, tvPlan1Step2, tvPlan1Step3);

            tvPlan2Title.setText(plan2Data.getString("title"));
            tvPlan2Desc.setText(plan2Data.getString("description"));

            setSteps(plan2Data.getJSONArray("steps"),
                    tvPlan2Step1, tvPlan2Step2, tvPlan2Step3);

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

    // ---------------- CHOOSE PLAN (FIXED) ----------------
    private void choosePlan(JSONObject plan) {

        new Thread(() -> {
            try {

                URL url = new URL("http://192.168.1.12:8080/api/plan/choose");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("userId", userId);
                body.put("topic", topic);
                body.put("title", plan.getString("title"));
                body.put("description", plan.getString("description"));
                body.put("steps", plan.getJSONArray("steps").toString());

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                // 🔥 قراءة الرد (important for debugging)
                Scanner scanner = new Scanner(
                        responseCode >= 200 && responseCode < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream()
                );

                String response = scanner.useDelimiter("\\A").hasNext()
                        ? scanner.next()
                        : "";
                scanner.close();

                runOnUiThread(() -> {

                    if (responseCode >= 200 && responseCode < 300) {

                        Toast.makeText(this, "Plan selected successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, DoingActivity.class));
                        finish();

                    } else {

                        Toast.makeText(this,
                                "Server error: " + response,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Network error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    // ---------------- SAVE LOCAL (OPTIONAL) ----------------
    private void saveChosenPlan(JSONObject plan) {

        try {
            getSharedPreferences("todo_data", MODE_PRIVATE)
                    .edit()
                    .putString("plan_title", plan.getString("title"))
                    .putString("plan_desc", plan.getString("description"))
                    .putString("plan_steps", plan.getJSONArray("steps").toString())
                    .putString("topic", topic)
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}