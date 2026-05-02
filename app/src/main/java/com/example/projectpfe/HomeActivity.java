package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectpfe.api.DashboardApi;
import com.example.projectpfe.model.HomeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HOME_DEBUG";

    TextView tvUserName, tvProgressPercent, tvTimeFocus, tvStreak;
    TextView tvGoal1, tvGoal2;
    CircularProgressView circularProgress;
    ProgressBar progressGoal1, progressGoal2;
    ImageButton btnChatbot;

    Retrofit retrofit;
    DashboardApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("========== HOME ACTIVITY START ==========");

        setContentView(R.layout.activity_home);

        initViews();
        initRetrofit();
        setupBottomNav();
        loadUserName();
        loadDashboard();
        setupActions();

        System.out.println("========== HOME ACTIVITY READY ==========");
    }

    // =========================
    private void initViews() {

        tvUserName = findViewById(R.id.tvUserName);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvTimeFocus = findViewById(R.id.tvTimeFocus);
        tvStreak = findViewById(R.id.tvStreak);

        tvGoal1 = findViewById(R.id.goal1);
        tvGoal2 = findViewById(R.id.goal2);

        circularProgress = findViewById(R.id.circularProgress);

        // ✅ مهم
        progressGoal1 = findViewById(R.id.progressGoal1);
        progressGoal2 = findViewById(R.id.progressGoal2);

        btnChatbot = findViewById(R.id.btnChatbot);
    }

    // =========================
    private void initRetrofit() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.12:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(DashboardApi.class);
    }

    // =========================
    private void loadUserName() {

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Student");

        tvUserName.setText(name);
    }

    // =========================
    private void loadDashboard() {

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId == -1) return;

        api.getHome(userId).enqueue(new Callback<HomeResponse>() {

            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    HomeResponse data = response.body();

                    // =========================
                    // 🔹 TOP DATA
                    // =========================
                    tvUserName.setText(data.getUserName());

                    int progress = data.getProgress();
                    int timeFocus = data.getTimeFocus();
                    int streak = data.getStreak();

                    tvProgressPercent.setText(progress + "%");
                    circularProgress.setProgress(progress);
                    tvTimeFocus.setText(timeFocus + "%");
                    tvStreak.setText(String.valueOf(streak));

                    // =========================
                    // 🔥 GOALS + PROGRESS
                    // =========================
                    List<String> goals = data.getGoals();
                    List<Integer> progressList = data.getGoalProgress();

                    if (goals != null && !goals.isEmpty()) {

                        // 🎯 GOAL 1
                        if (goals.size() >= 1) {
                            tvGoal1.setText(goals.get(0));

                            if (progressList != null && progressList.size() >= 1) {
                                progressGoal1.setProgress(progressList.get(0));
                            } else {
                                progressGoal1.setProgress(0);
                            }
                        }

                        // 🎯 GOAL 2
                        if (goals.size() >= 2) {
                            tvGoal2.setText(goals.get(1));

                            if (progressList != null && progressList.size() >= 2) {
                                progressGoal2.setProgress(progressList.get(1));
                            } else {
                                progressGoal2.setProgress(0);
                            }
                        }

                    } else {
                        tvGoal1.setText("No active goals");
                        tvGoal2.setText("");
                        progressGoal1.setProgress(0);
                        progressGoal2.setProgress(0);
                    }

                } else {
                    Toast.makeText(HomeActivity.this,
                            "Server Error: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {

                t.printStackTrace();

                Toast.makeText(HomeActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    private void setupActions() {

        btnChatbot.setOnClickListener(v ->
                startActivity(new Intent(this, ChatbotActivity.class)));

        findViewById(R.id.tvViewAll).setOnClickListener(v ->
                startActivity(new Intent(this, DoingActivity.class)));
    }

    // =========================
    private void setupBottomNav() {

        LinearLayout bottomNavBar = findViewById(R.id.bottomNavBar);

        bottomNavBar.getChildAt(1).setOnClickListener(v ->
                startActivity(new Intent(this, TodoActivity.class)));

        bottomNavBar.getChildAt(2).setOnClickListener(v ->
                startActivity(new Intent(this, CommunityActivity.class)));

        bottomNavBar.getChildAt(3).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        bottomNavBar.getChildAt(4).setOnClickListener(v -> {

            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            long userId = prefs.getLong("user_id", -1);

            if (userId != -1) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            } else {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }
}