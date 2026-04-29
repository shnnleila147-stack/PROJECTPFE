package com.example.projectpfe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DashboardActivity extends BaseActivity {

    private static final String TAG = "DASHBOARD_DEBUG";

    private TextView srlText, completionText, focusText, observationText;
    private ProgressBar srlProgress;
    private TextView[] days = new TextView[7];
    private WaveView waveView;

    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupNotificationBell();
        setupBottomNav(3);
        initViews();
        loadUser();
        loadDashboard();
    }

    private void initViews() {

        srlText = findViewById(R.id.srlScore);
        completionText = findViewById(R.id.completionRate);
        focusText = findViewById(R.id.focusRate);
        observationText = findViewById(R.id.observationText);
        srlProgress = findViewById(R.id.srlProgress);
        waveView = findViewById(R.id.waveView);

        days[0] = findViewById(R.id.day1);
        days[1] = findViewById(R.id.day2);
        days[2] = findViewById(R.id.day3);
        days[3] = findViewById(R.id.day4);
        days[4] = findViewById(R.id.day5);
        days[5] = findViewById(R.id.day6);
        days[6] = findViewById(R.id.day7);
    }

    private void loadUser() {
        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);
        userId = userPrefs.getLong("user_id", -1);

        Log.d(TAG, "👤 Loaded userId: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
        }
    }

    private void loadDashboard() {

        if (userId == -1) return;

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.12:8080/api/dashboard/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.d(TAG, "🔥 RAW RESPONSE: " + result.toString());

                JSONObject json = new JSONObject(result.toString());

                runOnUiThread(() -> updateUI(json));

            } catch (Exception e) {
                Log.e(TAG, "❌ NETWORK ERROR", e);
            }
        }).start();
    }

    private void updateUI(JSONObject json) {

        try {
            Log.d(TAG, "========== UI UPDATE START ==========");

            int srl = json.getInt("srlScore");

            if (srl > 100) srl = 100;
            if (srl < 0) srl = 0;

            Log.d(TAG, "SRL SCORE: " + srl);

            srlText.setText(String.valueOf(srl));
            srlProgress.setMax(100);
            srlProgress.setProgress(srl);

            completionText.setText(json.getInt("completionRate") + "%");
            focusText.setText(json.getInt("averageFocus") + "%");
            observationText.setText(json.getString("observation"));

            // =========================
            // 🔥 WEEKLY DEBUG + FIX
            // =========================
            JSONArray arr = json.getJSONArray("weeklyDays");

            Log.d(TAG, "WEEKLY ARRAY SIZE: " + arr.length());

            // reset UI
            for (int i = 0; i < 7; i++) {
                days[i].setBackgroundResource(R.drawable.circle_unselected);
            }

            for (int i = 0; i < arr.length(); i++) {

                int value = arr.getInt(i);

                Log.d(TAG, "WEEKLY INDEX: " + i + " VALUE: " + value);

                if (value == 0) {
                    Log.d(TAG, "➡️ Day " + i + " = NO ACTIVITY");
                } else {
                    Log.d(TAG, "✅ Day " + i + " = ACTIVE");
                    // 👇 هنا الإصلاح الصحيح
                    days[i].setBackgroundResource(R.drawable.circle_selected);
                }
            }

            // =========================
            // 🔥 EMOTION DEBUG
            // =========================
            JSONArray emo = json.getJSONArray("emotions");

            Log.d(TAG, "EMOTION ARRAY SIZE: " + emo.length());

            ArrayList<Integer> list = new ArrayList<>();

            for (int i = 0; i < emo.length(); i++) {
                int e = emo.getInt(i);
                list.add(e);

                Log.d(TAG, "EMOTION DAY " + i + " = " + e);
            }

            waveView.setData(list);

            Log.d(TAG, "FINAL EMOTION LIST: " + list.toString());

            Log.d(TAG, "========== UI UPDATE END ==========");

        } catch (Exception e) {
            Log.e(TAG, "❌ UI ERROR", e);
        }
    }
}