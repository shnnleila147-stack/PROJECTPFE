package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DoingSessionActivity extends AppCompatActivity {

    long planId, userId;

    TextView step1, step2, step3;
    TextView step1Progress, step2Progress, step3Progress;
    TextView tvStep1Timer, tvStep2Timer, tvStep3Timer;

    ProgressBar progress1, progress2, progress3;

    StepStateManager manager;

    int currentStep = -1;

    long stepTime = 0;

    int[] progress = {0, 0, 0};
    long[] elapsed = {0, 0, 0};

    JSONArray stepsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_session);

        Log.d("DEBUG", "🔥 DoingSessionActivity STARTED");

        planId = getIntent().getLongExtra("planId", -1);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        Log.d("DEBUG", "planId = " + planId + " userId = " + userId);

        bindViews();
        loadData();

        Button btn = findViewById(R.id.btnDayCompleted);
        btn.setOnClickListener(v -> {
            Log.d("DEBUG", "✅ Day Completed Clicked");
            onDayCompleted();
        });
    }

    private void bindViews() {

        step1 = findViewById(R.id.tvStep1Title);
        step2 = findViewById(R.id.tvStep2Title);
        step3 = findViewById(R.id.tvStep3Title);

        step1Progress = findViewById(R.id.tvStep1Progress);
        step2Progress = findViewById(R.id.tvStep2Progress);
        step3Progress = findViewById(R.id.tvStep3Progress);

        progress1 = findViewById(R.id.progressStep1);
        progress2 = findViewById(R.id.progressStep2);
        progress3 = findViewById(R.id.progressStep3);

        tvStep1Timer = findViewById(R.id.tvStep1Timer);
        tvStep2Timer = findViewById(R.id.tvStep2Timer);
        tvStep3Timer = findViewById(R.id.tvStep3Timer);

        Log.d("DEBUG", "Views binded successfully");
    }

    // ================= LOAD =================

    private void loadData() {

        new Thread(() -> {
            try {

                Log.d("DEBUG", "📡 Loading Plan...");

                URL url = new URL("http://192.168.1.12:8080/api/plan/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Scanner sc = new Scanner(conn.getInputStream());
                String res = sc.useDelimiter("\\A").next();

                Log.d("DEBUG", "Plan Response = " + res);

                JSONArray arr = new JSONArray(res);
                JSONObject plan = null;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject p = arr.getJSONObject(i);
                    if (p.getLong("id") == planId) {
                        plan = p;
                        break;
                    }
                }

                if (plan == null) {
                    Log.e("ERROR", "❌ Plan not found!");
                    return;
                }


                stepsArray = new JSONArray(plan.getString("steps"));

                Log.d("DEBUG", "Steps parsed = " + stepsArray.toString());

                long totalTime = getTodoTime();

                stepTime = (totalTime * 60 * 1000) / 3;

                Log.d("DEBUG", "stepTime = " + stepTime);

                runOnUiThread(this::renderUI);

            } catch (Exception e) {
                Log.e("LOAD", "❌ ERROR loading plan", e);
            }
        }).start();
    }

    private long getTodoTime() {
        try {

            Log.d("DEBUG", "📡 Loading Todo Time...");

            URL url = new URL("http://192.168.1.12:8080/api/todo/" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Scanner sc = new Scanner(conn.getInputStream());
            String res = sc.useDelimiter("\\A").next();

            Log.d("DEBUG", "Todo Response = " + res);

            JSONArray arr = new JSONArray(res);
            JSONObject obj = arr.getJSONObject(0);

            long time = obj.getLong("time");

            Log.d("DEBUG", "Total Time = " + time);

            return time;

        } catch (Exception e) {
            Log.e("DEBUG", "❌ Error getting todo time", e);
            return 1; // 1 minute fallback
        }
    }

    // ================= UI =================

    private void renderUI() {

        if (stepsArray == null) {
            Log.e("ERROR", "stepsArray is NULL ❌");
            return;
        }

        Log.d("DEBUG", "🎨 Rendering UI...");

        step1.setText(stepsArray.optString(0));
        step2.setText(stepsArray.optString(1));
        step3.setText(stepsArray.optString(2));

        setupClicks();
        loadProgress();
    }

    // ================= PROGRESS =================

    private void loadProgress() {

        new Thread(() -> {
            try {

                Log.d("DEBUG", "📡 Loading Progress...");

                URL url = new URL("http://192.168.1.12:8080/api/step/" + userId + "/" + planId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Scanner sc = new Scanner(conn.getInputStream());
                String res = sc.useDelimiter("\\A").next();

                Log.d("DEBUG", "Progress Response = " + res);

                JSONArray arr = new JSONArray(res);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject obj = arr.getJSONObject(i);

                    int index = obj.getInt("stepIndex");

                    progress[index] = obj.getInt("progress");
                    elapsed[index] = obj.getLong("elapsedTime");

                    Log.d("DEBUG", "Step " + index + " progress=" + progress[index]);
                }

                runOnUiThread(this::updateUI);

            } catch (Exception e) {
                Log.e("LOAD_PROGRESS", "❌ ERROR", e);
            }
        }).start();
    }

    private void updateUI() {

        Log.d("DEBUG", "🔄 Updating UI...");

        progress1.setProgress(progress[0]);
        progress2.setProgress(progress[1]);
        progress3.setProgress(progress[2]);

        step1Progress.setText(progress[0] + "%");
        step2Progress.setText(progress[1] + "%");
        step3Progress.setText(progress[2] + "%");

        tvStep1Timer.setText(formatTime(elapsed[0]));
        tvStep2Timer.setText(formatTime(elapsed[1]));
        tvStep3Timer.setText(formatTime(elapsed[2]));
    }

    // ================= CLICK =================

    private void setupClicks() {

        step1.setOnClickListener(v -> {
            Log.d("DEBUG", "▶ Step 1 clicked");
            startStep(0);
        });

        step2.setOnClickListener(v -> {
            Log.d("DEBUG", "▶ Step 2 clicked");
            startStep(1);
        });

        step3.setOnClickListener(v -> {
            Log.d("DEBUG", "▶ Step 3 clicked");
            startStep(2);
        });
    }

    // ================= TIMER =================

    private void startStep(int index) {

        Log.d("DEBUG", "🚀 startStep CALLED with index = " + index);

        if (index < 0 || index > 2) {
            Log.e("ERROR", "❌ INVALID STEP INDEX: " + index);
            return;
        }

        if (manager != null) {
            Log.d("DEBUG", "⏹ Stopping previous step");
            manager.stop();
        }

        currentStep = index;

        Log.d("DEBUG", "✅ currentStep set = " + currentStep);

        manager = new StepStateManager(stepTime, (p, e) -> {

            progress[index] = p;
            elapsed[index] = e;

            Log.d("DEBUG", "⏱ Step " + index + " progress = " + p + " elapsed = " + e);

            runOnUiThread(() -> {

                updateUI();

                if (index == 0)
                    tvStep1Timer.setText(formatTime(e));
                else if (index == 1)
                    tvStep2Timer.setText(formatTime(e));
                else if (index == 2)
                    tvStep3Timer.setText(formatTime(e));

            });

            if (p == 100) {
                Log.d("DEBUG", "🎉 Step " + index + " COMPLETED");
                saveStep();
            }

        });

        Log.d("DEBUG", "▶ Starting timer for step " + index);

        manager.startStep(elapsed[index]);
    }

    // ================= SAVE =================

    private void saveStep() {

        new Thread(() -> {
            try {

                Log.d("DEBUG", "💾 Saving step START");

                URL url = new URL("http://192.168.1.12:8080/api/step/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject body = new JSONObject();
                body.put("userId", userId);
                body.put("planId", planId);
                body.put("stepIndex", currentStep);
                body.put("progress", progress[currentStep]);
                body.put("elapsedTime", elapsed[currentStep]);
                body.put("status", progress[currentStep] == 100 ? "DONE" : "PAUSED");

                Log.d("DEBUG", "📤 Request Body = " + body.toString());

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                // 🔥 أهم شيء: قراءة response
                int responseCode = conn.getResponseCode();
                Log.d("DEBUG", "📡 Response Code = " + responseCode);

                Scanner sc;

                if (responseCode >= 200 && responseCode < 300) {
                    sc = new Scanner(conn.getInputStream());
                } else {
                    sc = new Scanner(conn.getErrorStream());
                }

                String res = sc.useDelimiter("\\A").hasNext() ? sc.next() : "";
                Log.d("DEBUG", "📥 Response Body = " + res);

                conn.disconnect();

            } catch (Exception e) {
                Log.e("SAVE_ERROR", "❌ ERROR saving step", e);
            }
        }).start();
    }

    // ================= DAY COMPLETE =================

    private void onDayCompleted() {

        Log.d("DEBUG", "🏁 Day Completed START");

        saveStep();

        Intent i = new Intent(this, DoneActivity.class);
        startActivity(i);

        Log.d("DEBUG", "➡️ Navigated to DoneActivity");
    }

    private String formatTime(long ms) {
        long sec = ms / 1000;
        long min = sec / 60;
        sec = sec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("DEBUG", "⏸ Activity paused → saving + stopping timer");

        if (manager != null) {
            manager.stop();
        }

        saveStep();
    }
}