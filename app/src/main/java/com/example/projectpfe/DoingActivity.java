package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DoingActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;
    LinearLayout containerTasks;
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing);

        setupNotificationBell();
        setupBottomNav(1);

        tabToDo = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone = findViewById(R.id.tabDone);
        containerTasks = findViewById(R.id.containerTasks);

        tabToDo.setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });

        tabDone.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);
        userId = userPrefs.getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
            return;
        }

        loadPlans();
    }

    // ================= LOAD =================
    private void loadPlans() {

        new Thread(() -> {
            try {

                URL url = new URL("http://192.168.1.3:8080/api/plan/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                Scanner scanner = new Scanner(conn.getInputStream());
                String response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();

                Log.d("API_RESPONSE", response);

                runOnUiThread(() -> displayPlans(response));

            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }

    // ================= DISPLAY =================
    private void displayPlans(String response) {

        try {

            JSONArray plans = new JSONArray(response);
            containerTasks.removeAllViews();

            for (int i = 0; i < plans.length(); i++) {

                JSONObject plan = plans.getJSONObject(i);

                long planId = plan.getLong("id");
                String title = plan.optString("title", "");

                // 🔥 FIX: steps is STRING not JSONArray
                JSONArray steps;
                try {
                    steps = new JSONArray(plan.getString("steps"));
                } catch (Exception e) {
                    steps = new JSONArray();
                }

                JSONArray doneSteps = plan.optJSONArray("doneSteps");
                if (doneSteps == null) doneSteps = new JSONArray();

                View view = getLayoutInflater().inflate(R.layout.item_plan, null);

                TextView tvTitle = view.findViewById(R.id.title);

                TextView step1 = view.findViewById(R.id.step1);
                TextView step2 = view.findViewById(R.id.step2);
                TextView step3 = view.findViewById(R.id.step3);

                ImageView icon1 = view.findViewById(R.id.point1);
                ImageView icon2 = view.findViewById(R.id.point2);
                ImageView icon3 = view.findViewById(R.id.point3);

                TextView tvPercent = view.findViewById(R.id.progressText);
                CircularProgressView progress = view.findViewById(R.id.progress);

                tvTitle.setText(title);

                // ================= STEPS =================
                step1.setText(steps.optString(0, ""));
                step2.setText(steps.optString(1, ""));
                step3.setText(steps.optString(2, ""));

                // ================= DONE =================
                boolean d1 = doneSteps.optBoolean(0, false);
                boolean d2 = doneSteps.optBoolean(1, false);
                boolean d3 = doneSteps.optBoolean(2, false);

                applyStep(step1, icon1, d1);
                applyStep(step2, icon2, d2);
                applyStep(step3, icon3, d3);

                int doneCount = (d1 ? 1 : 0) + (d2 ? 1 : 0) + (d3 ? 1 : 0);
                int percent = (doneCount * 100) / 3;

                progress.setProgress(percent);
                tvPercent.setText(percent + "%");

                view.setOnClickListener(v -> {
                    Intent intent = new Intent(this, DoingSessionActivity.class);
                    intent.putExtra("planId", planId);
                    startActivity(intent);
                });

                containerTasks.addView(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Parse error", Toast.LENGTH_LONG).show();
        }
    }

    private void applyStep(TextView text, ImageView dot, boolean done) {

        if (done) {
            text.setPaintFlags(text.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            text.setTextColor(android.graphics.Color.parseColor("#444455"));
            dot.setImageResource(R.drawable.circle_selected);
        } else {
            text.setPaintFlags(0);
            text.setTextColor(android.graphics.Color.parseColor("#AAAACC"));
            dot.setImageResource(R.drawable.circle_unselected);
        }
    }
}