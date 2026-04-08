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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TodoAiActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnChoosePlan1, btnChoosePlan2;
    TextView tvPlan1Title, tvPlan1Desc, tvPlan2Title, tvPlan2Desc;
    ProgressBar progressBar;

    // ✅ ضعي مفتاح Claude API هنا
    private static final String CLAUDE_API_KEY = "YOUR_CLAUDE_API_KEY_HERE";

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
        progressBar    = findViewById(R.id.progressBarAi);

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
        // قراءة بيانات المستخدم من SharedPreferences
        SharedPreferences todoPrefs = getSharedPreferences("todo_data", MODE_PRIVATE);
        SharedPreferences profilePrefs = getSharedPreferences("profile_data", MODE_PRIVATE);

        String goal        = todoPrefs.getString("goal", "تحسين المستوى الدراسي");
        String topic       = todoPrefs.getString("topic", "");
        String time        = todoPrefs.getString("time", "");
        String description = todoPrefs.getString("description", "");
        String personality = profilePrefs.getString("personality_type", "");
        String schedule    = profilePrefs.getString("student_schedule", "");

        // إظهار مؤشر التحميل
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        String prompt = "أنت مساعد ذكي لطالب جامعي. بناءً على المعلومات التالية، اقترح خطتين دراسيتين مختصرتين:\n" +
                "الهدف: " + goal + "\n" +
                "الموضوع: " + topic + "\n" +
                "الوقت المتاح: " + time + "\n" +
                "التفاصيل: " + description + "\n" +
                "نوع الشخصية: " + personality + "\n" +
                "جدول الطالب: " + schedule + "\n\n" +
                "أجب بـ JSON فقط بهذا الشكل بدون أي نص إضافي:\n" +
                "{\"plan1\":{\"title\":\"اسم الخطة 1\",\"description\":\"وصف مختصر\"},\"plan2\":{\"title\":\"اسم الخطة 2\",\"description\":\"وصف مختصر\"}}";

        OkHttpClient client = new OkHttpClient();
        JSONObject requestBody;
        try {
            requestBody = new JSONObject();
            requestBody.put("model", "claude-opus-4-6");
            requestBody.put("max_tokens", 500);
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            requestBody.put("messages", messages);
        } catch (Exception e) {
            showDefaultPlans();
            return;
        }

        Request request = new Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", CLAUDE_API_KEY)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(RequestBody.create(requestBody.toString(),
                        MediaType.get("application/json")))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    showDefaultPlans();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject json = new JSONObject(body);
                        String text = json.getJSONArray("content")
                                .getJSONObject(0).getString("text");
                        JSONObject plans = new JSONObject(text.trim());

                        String plan1Title = plans.getJSONObject("plan1").getString("title");
                        String plan1Desc  = plans.getJSONObject("plan1").getString("description");
                        String plan2Title = plans.getJSONObject("plan2").getString("title");
                        String plan2Desc  = plans.getJSONObject("plan2").getString("description");

                        if (tvPlan1Title != null) tvPlan1Title.setText(plan1Title);
                        if (tvPlan1Desc  != null) tvPlan1Desc.setText(plan1Desc);
                        if (tvPlan2Title != null) tvPlan2Title.setText(plan2Title);
                        if (tvPlan2Desc  != null) tvPlan2Desc.setText(plan2Desc);

                        // حفظ الخطط
                        getSharedPreferences("todo_data", MODE_PRIVATE).edit()
                                .putString("plan1_title", plan1Title)
                                .putString("plan1_desc", plan1Desc)
                                .putString("plan2_title", plan2Title)
                                .putString("plan2_desc", plan2Desc)
                                .apply();

                    } catch (Exception e) {
                        showDefaultPlans();
                    }
                });
            }
        });
    }

    private void showDefaultPlans() {
        if (tvPlan1Title != null) tvPlan1Title.setText("خطة مكثفة");
        if (tvPlan1Desc  != null) tvPlan1Desc.setText("مراجعة شاملة مع التركيز على النقاط الصعبة");
        if (tvPlan2Title != null) tvPlan2Title.setText("خطة تدريجية");
        if (tvPlan2Desc  != null) tvPlan2Desc.setText("تقسيم المادة على أيام مع استراحات منتظمة");
    }

    private void savePlanChoice(String planKey) {
        getSharedPreferences("todo_data", MODE_PRIVATE).edit()
                .putString("chosen_plan", planKey)
                .apply();
    }
}