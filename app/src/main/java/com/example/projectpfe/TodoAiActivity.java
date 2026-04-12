package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;



public class TodoAiActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnChoosePlan1, btnChoosePlan2;
    TextView tvPlan1Title, tvPlan1Desc, tvPlan2Title, tvPlan2Desc;
    TextView tvPlan1Step1, tvPlan1Step2, tvPlan1Step3;
    TextView tvPlan2Step1, tvPlan2Step2, tvPlan2Step3;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_ai);

      setupNotificationBell(); // ✅
        setupBottomNav(1);       // ✅ الأيقونة 2 (TODO) مفعّلة

        tabToDo        = findViewById(R.id.tabToDo);
        tabDoing       = findViewById(R.id.tabDoing);
        tabDone        = findViewById(R.id.tabDone);
        btnChoosePlan1 = findViewById(R.id.btnChoosePlan1);
        btnChoosePlan2 = findViewById(R.id.btnChoosePlan2);
       tvPlan1Title   = findViewById(R.id.tvPlan1Title);
       tvPlan1Desc    = findViewById(R.id.tvPlan1Desc);
       tvPlan2Title   = findViewById(R.id.tvPlan2Title);
       tvPlan2Desc    = findViewById(R.id.tvPlan2Desc);
        tvPlan1Step1 = findViewById(R.id.tvPlan1Step1);
        tvPlan1Step2 = findViewById(R.id.tvPlan1Step2);
        tvPlan1Step3 = findViewById(R.id.tvPlan1Step3);

        tvPlan2Step1 = findViewById(R.id.tvPlan2Step1);
        tvPlan2Step2 = findViewById(R.id.tvPlan2Step2);
        tvPlan2Step3 = findViewById(R.id.tvPlan2Step3);

        progressBar    = findViewById(R.id.progressBarAi);
        if(tvPlan1Title == null) System.out.println("tvPlan1Title NULL!");
        // Tabs
        tabToDo.setOnClickListener(v -> {
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });
        tabDoing.setOnClickListener(v -> {
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });
        tabDone.setOnClickListener(v -> {
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        // ✅ استدعاء الذكاء الاصطناعي لتوليد الخطط
        generatePlansWithAI();

        // ✅ زر الخطة 1 → ينتقل لـ DoingActivity
        btnChoosePlan1.setOnClickListener(v -> {
            savePlanChoice("plan1");
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });

        // ✅ زر الخطة 2 → ينتقل لـ DoingActivity
        btnChoosePlan2.setOnClickListener(v -> {
            savePlanChoice("plan2");
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });
    }

    private void generatePlansWithAI() {

        SharedPreferences todoPrefs = getSharedPreferences("todo_data", MODE_PRIVATE);
        SharedPreferences profilePrefs = getSharedPreferences("profile_data", MODE_PRIVATE);

        String goal = todoPrefs.getString("goal", "");
        String topic = todoPrefs.getString("topic", "");
        String time = todoPrefs.getString("time", "");
        String description = todoPrefs.getString("description", "");
        String personality = profilePrefs.getString("personality_type", "");
        String schedule = profilePrefs.getString("student_schedule", "");

        progressBar.setVisibility(View.VISIBLE);

        try {
            JSONObject json = new JSONObject();
            json.put("goal", goal);
            json.put("topic", topic);
            json.put("time", time);
            json.put("description", description);
            json.put("personality", personality);
            json.put("schedule", schedule);

            String url = "http://192.168.1.12:8080/api/ai/generate-plans";

            JsonObjectRequest request = new JsonObjectRequest(
                    com.android.volley.Request.Method.POST,
                    url,
                    json,

                    response -> {

                        progressBar.setVisibility(View.GONE);

                        try {
                            System.out.println("RESPONSE: " + response);

                            JSONObject plan1 = response.getJSONObject("plan1");
                            JSONObject plan2 = response.getJSONObject("plan2");

                            tvPlan1Title.setText(plan1.getString("title"));
                            tvPlan1Desc.setText(plan1.getString("description"));

                            JSONArray p1Steps = plan1.getJSONArray("steps");
                            tvPlan1Step1.setText("• " + p1Steps.getString(0));
                            tvPlan1Step2.setText("• " + p1Steps.getString(1));
                            tvPlan1Step3.setText("• " + p1Steps.getString(2));

                            tvPlan2Title.setText(plan2.getString("title"));
                            tvPlan2Desc.setText(plan2.getString("description"));

                            JSONArray p2Steps = plan2.getJSONArray("steps");
                            tvPlan2Step1.setText("• " + p2Steps.getString(0));
                            tvPlan2Step2.setText("• " + p2Steps.getString(1));
                            tvPlan2Step3.setText("• " + p2Steps.getString(2));

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "JSON Parse Error", Toast.LENGTH_LONG).show();
                        }
                    },

                    error -> {
                        progressBar.setVisibility(View.GONE);

                        System.out.println("ERROR: " + error.toString());
                        Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show();
                    }
            );

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }



    private void savePlanChoice(String planKey) {
        getSharedPreferences("todo_data", MODE_PRIVATE).edit()
                .putString("chosen_plan", planKey)
                .apply();
    }
}