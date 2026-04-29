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

    private void loadData() {
        new Thread(() -> {
            try {

                URL url = new URL("http://192.168.1.12:8080/api/plan/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Scanner sc = new Scanner(conn.getInputStream());
                String res = sc.useDelimiter("\\A").next();

                JSONArray arr = new JSONArray(res);
                JSONObject plan = null;

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject p = arr.getJSONObject(i);
                    if (p.getLong("id") == planId) {
                        plan = p;
                        break;
                    }
                }

                stepsArray = new JSONArray(plan.getString("steps"));

                long totalTime = getTodoTime();
                stepTime = (totalTime * 60 * 1000) / 3;

                runOnUiThread(this::renderUI);

            } catch (Exception e) {
                Log.e("LOAD", "❌ ERROR loading plan", e);
            }
        }).start();
    }

    private long getTodoTime() {
        try {

            URL url = new URL("http://192.168.1.12:8080/api/todo/" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Scanner sc = new Scanner(conn.getInputStream());
            String res = sc.useDelimiter("\\A").next();

            JSONArray arr = new JSONArray(res);
            JSONObject obj = arr.getJSONObject(0);

            return obj.getLong("time");

        } catch (Exception e) {
            return 1;
        }
    }

    private void renderUI() {

        step1.setText(stepsArray.optString(0));
        step2.setText(stepsArray.optString(1));
        step3.setText(stepsArray.optString(2));

        setupClicks();
        loadProgress();
    }

    private void loadProgress() {

        new Thread(() -> {
            try {

                URL url = new URL("http://192.168.1.12:8080/api/step/" + userId + "/" + planId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Scanner sc = new Scanner(conn.getInputStream());
                String res = sc.useDelimiter("\\A").next();

                JSONArray arr = new JSONArray(res);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject obj = arr.getJSONObject(i);

                    int index = obj.getInt("stepIndex");

                    progress[index] = obj.getInt("progress");
                    elapsed[index] = obj.getLong("elapsedTime");
                }

                runOnUiThread(this::updateUI);

            } catch (Exception e) {
                Log.e("LOAD_PROGRESS", "❌ ERROR", e);
            }
        }).start();
    }

    private void updateUI() {

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

    private void setupClicks() {

        step1.setOnClickListener(v -> startStep(0));
        step2.setOnClickListener(v -> startStep(1));
        step3.setOnClickListener(v -> startStep(2));
    }

    private void startStep(int index) {

        if (manager != null) manager.stop();

        currentStep = index;
        saveStep();
        manager = new StepStateManager(stepTime, (p, e) -> {

            progress[index] = p;
            elapsed[index] = e;

            runOnUiThread(this::updateUI);

            if (p == 100) saveStep();

        });

        manager.startStep(elapsed[index]);
    }

    // ================= SAVE (MODIFIED ONLY HERE) =================

    private void saveStep() {

        new Thread(() -> {
            try {

                URL url = new URL("http://192.168.1.12:8080/api/step/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject body = new JSONObject();

                int p = progress[currentStep];
                String status;

                if (p == 0)
                    status = "NOT_STARTED";
                else if (p < 100)
                    status = "IN_PROGRESS";
                else
                    status = "DONE";

                body.put("userId", userId);
                body.put("planId", planId);
                body.put("stepIndex", currentStep);
                body.put("progress", p);
                body.put("elapsedTime", elapsed[currentStep]);
                body.put("status", status);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                conn.getResponseCode();

                conn.disconnect();

            } catch (Exception e) {
                Log.e("SAVE_ERROR", "❌ ERROR saving step", e);
            }
        }).start();
    }

    private void onDayCompleted() {
        saveStep();

        Intent i = new Intent(this, DoneActivity.class);
        i.putExtra("planId", planId); // 🔥 لازم تضيفها
        startActivity(i);
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

        if (manager != null) manager.stop();
        saveStep();
    }
}