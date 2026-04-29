package com.example.projectpfe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectpfe.api.ProfileApi;
import com.example.projectpfe.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "PROFILE_DEBUG";

    // Big Five
    ProgressBar progressOpenness, progressConscientiousness, progressExtraversion, progressAgreeableness, progressNeuroticism;
    TextView tvOpenness, tvConscientiousness, tvExtraversion, tvAgreeableness, tvNeuroticism;

    // User Info
    TextView tvProfileName, tvProfileGrade;

    // 🔥 NEW: Behavior TextViews
    TextView tvAttemptRate, tvSuccessRate, tvPersistence, tvDifficulty;

    Retrofit retrofit;
    ProfileApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupNotificationBell();
        setupBottomNav(4);

        initViews();
        initRetrofit();
        loadUser();
    }

    private void initViews() {

        // Big Five
        progressOpenness = findViewById(R.id.progressOpenness);
        progressConscientiousness = findViewById(R.id.progressConscientiousness);
        progressExtraversion = findViewById(R.id.progressExtraversion);
        progressAgreeableness = findViewById(R.id.progressAgreeableness);
        progressNeuroticism = findViewById(R.id.progressNeuroticism);

        tvOpenness = findViewById(R.id.tvOpennessValue);
        tvConscientiousness = findViewById(R.id.tvConscientiousnessValue);
        tvExtraversion = findViewById(R.id.tvExtraversionValue);
        tvAgreeableness = findViewById(R.id.tvAgreeablenessValue);
        tvNeuroticism = findViewById(R.id.tvNeuroticismValue);

        // User
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileGrade = findViewById(R.id.tvProfileGrade);

        // 🔥 Behavior
        tvAttemptRate = findViewById(R.id.tvAttemptRate);
        tvSuccessRate = findViewById(R.id.tvSuccessRate);
        tvPersistence = findViewById(R.id.tvPersistence);
        tvDifficulty = findViewById(R.id.tvDifficulty);
    }

    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.12:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(ProfileApi.class);

        Log.d(TAG, "✅ Retrofit Initialized");
    }

    private void loadUser() {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        Log.d(TAG, "👤 userId: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            loadBigFive(userId);
            loadUserProfile(userId);
            loadBehavior(userId); // 🔥 NEW
        }
    }

    // =========================
    // 🔥 NEW: LOAD BEHAVIOR
    // =========================
    private void loadBehavior(Long userId) {

        Log.d(TAG, "🚀 Loading Behavior...");

        api.getBehavior(userId).enqueue(new Callback<Map<String, Integer>>() {

            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {

                Log.d(TAG, "📥 Behavior Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    Map<String, Integer> data = response.body();

                    Log.d(TAG, "🔥 Behavior Data: " + data.toString());

                    setBehavior(tvAttemptRate, data.get("attemptRate"), "Attempt");
                    setBehavior(tvSuccessRate, data.get("successRate"), "Success");
                    setBehavior(tvPersistence, data.get("persistence"), "Persistence");
                    setBehavior(tvDifficulty, data.get("difficultyHandling"), "Difficulty");

                } else {
                    Log.e(TAG, "❌ Behavior Response Failed");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                Log.e(TAG, "❌ Behavior ERROR", t);
            }
        });
    }

    private void setBehavior(TextView tv, Integer value, String label) {

        if (value == null) {
            Log.w(TAG, "⚠️ " + label + " NULL → set to 0");
            value = 0;
        }

        Log.d(TAG, "📊 " + label + ": " + value);

        tv.setText(value + "%");
    }

    // =========================
    // BIG FIVE
    // =========================
    private void loadBigFive(Long userId) {

        Log.d(TAG, "🚀 Loading Big Five...");

        api.getBigFive(userId).enqueue(new Callback<Map<String, Integer>>() {

            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {

                Log.d(TAG, "📥 BigFive Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    Map<String, Integer> scores = response.body();

                    Log.d(TAG, "🔥 BigFive Data: " + scores.toString());

                    setData(progressOpenness, tvOpenness, scores.get("openness"), "Openness");
                    setData(progressConscientiousness, tvConscientiousness, scores.get("conscientiousness"), "Conscientiousness");
                    setData(progressExtraversion, tvExtraversion, scores.get("extraversion"), "Extraversion");
                    setData(progressAgreeableness, tvAgreeableness, scores.get("agreeableness"), "Agreeableness");
                    setData(progressNeuroticism, tvNeuroticism, scores.get("neuroticism"), "Neuroticism");

                } else {
                    Log.e(TAG, "❌ BigFive Response Failed");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                Log.e(TAG, "❌ BigFive ERROR", t);
            }
        });
    }

    // =========================
    // USER PROFILE
    // =========================
    private void loadUserProfile(Long userId) {

        Log.d(TAG, "🚀 Loading User Profile...");

        api.getUser(userId).enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Log.d(TAG, "📥 User Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    User user = response.body();

                    Log.d(TAG, "👤 User: " + user.getName() + " | " + user.getGrade());

                    tvProfileName.setText(user.getName());

                    if (tvProfileGrade != null)
                        tvProfileGrade.setText(user.getGrade());

                } else {
                    Log.e(TAG, "❌ User Response Failed");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "❌ User ERROR", t);
            }
        });
    }

    // =========================
    // SET DATA
    // =========================
    private void setData(ProgressBar progressBar, TextView textView, Integer value, String label) {

        if (value == null) {
            Log.w(TAG, "⚠️ " + label + " is NULL");
            value = 0;
        }

        Log.d(TAG, "📊 " + label + ": " + value);

        progressBar.setMax(100);
        progressBar.setProgress(value);

        textView.setText(value + "%");
    }
}