package com.example.projectpfe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectpfe.api.ProfileApi;
import com.example.projectpfe.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    // ProgressBars Big Five
    ProgressBar progressOpenness, progressConscientiousness, progressExtraversion, progressAgreeableness, progressNeuroticism;
    // TextViews Big Five
    TextView tvOpenness, tvConscientiousness, tvExtraversion, tvAgreeableness, tvNeuroticism;
    // TextViews المستخدم
    TextView tvProfileName, tvProfileGrade;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Notification ImageView
        ImageView ivNotification = findViewById(R.id.ivNotification);
        ivNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        // ربط ProgressBars و TextViews
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

        // ربط TextViews للمستخدم
        tvProfileName = findViewById(R.id.tvProfileName);
        //tvProfileGrade = findViewById(R.id.tvPro);

        // جلب userId من SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1); // -1 إذا لم يكن موجود

        if (userId == -1) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            loadBigFive(userId);
            loadUserProfile(userId);
        }
    }

    // ===== دالة لجلب Big Five =====
    private void loadBigFive(Long userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.25:8080/") // عدل حسب السيرفر
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProfileApi api = retrofit.create(ProfileApi.class);
        api.getBigFive(userId).enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Integer> scores = response.body();
                    setData(progressOpenness, tvOpenness, scores.get("openness"));
                    setData(progressConscientiousness, tvConscientiousness, scores.get("conscientiousness"));
                    setData(progressExtraversion, tvExtraversion, scores.get("extraversion"));
                    setData(progressAgreeableness, tvAgreeableness, scores.get("agreeableness"));
                    setData(progressNeuroticism, tvNeuroticism, scores.get("neuroticism"));
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                t.printStackTrace();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // ===== دالة لجلب بيانات المستخدم =====
    private void loadUserProfile(Long userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.25:8080/") // عدل حسب السيرفر
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProfileApi api = retrofit.create(ProfileApi.class);
        api.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvProfileName.setText(user.getName());
                    tvProfileGrade.setText(user.getGrade());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // ===== دالة لمزامنة ProgressBar و TextView =====
    private void setData(ProgressBar progressBar, TextView textView, Integer value) {
        if (value != null) {
            progressBar.setProgress(value);
            textView.setText(value + "%");
        }
    }
}