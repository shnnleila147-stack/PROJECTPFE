package com.example.projectpfe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectpfe.api.ApiClient;
import com.example.projectpfe.api.NotificationApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    private TextView tvMission, tvFocus, tvWeekly, tvStreak;
    private View cardMission, cardFocus, cardWeekly, cardStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("DEBUG_NOTIF", "onCreate START");

        try {
            setContentView(R.layout.activity_notifications);

            initViews();
            setupButtons();
            loadNotifications();

            Log.e("DEBUG_NOTIF", "onCreate FINISH");

        } catch (Exception e) {
            Log.e("DEBUG_NOTIF", "onCreate ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initViews() {

        Log.e("DEBUG_NOTIF", "initViews");

        tvMission = findViewById(R.id.tvMissionMessage);
        tvFocus = findViewById(R.id.tvFocusMessage);
        tvWeekly = findViewById(R.id.tvWeeklyMessage);
        tvStreak = findViewById(R.id.tvStreakMessage);

        cardMission = findViewById(R.id.cardMission);
        cardFocus = findViewById(R.id.cardFocus);
        cardWeekly = findViewById(R.id.cardWeekly);
        cardStreak = findViewById(R.id.cardStreak);
    }

    private void hideAllCards() {

        Log.e("DEBUG_NOTIF", "hideAllCards");

        if (cardMission != null) cardMission.setVisibility(View.GONE);
        if (cardFocus != null) cardFocus.setVisibility(View.GONE);
        if (cardWeekly != null) cardWeekly.setVisibility(View.GONE);
        if (cardStreak != null) cardStreak.setVisibility(View.GONE);
    }

    // ================= LOAD =================
    private void loadNotifications() {

        Log.e("DEBUG_NOTIF", "loadNotifications START");

        hideAllCards();

        SharedPreferences userPrefs = getSharedPreferences("user", MODE_PRIVATE);
        long userId = userPrefs.getLong("user_id", -1);

        Log.e("DEBUG_NOTIF", "userId = " + userId);

        NotificationApi api = ApiClient.getClient().create(NotificationApi.class);

        api.getNotifications(userId).enqueue(new Callback<List<NotificationModel>>() {

            @Override
            public void onResponse(Call<List<NotificationModel>> call,
                                   Response<List<NotificationModel>> response) {

                Log.e("DEBUG_NOTIF", "API RESPONSE RECEIVED");
                Log.e("DEBUG_NOTIF", "HTTP CODE = " + response.code());

                List<NotificationModel> list = response.body();

                Log.e("DEBUG_NOTIF", "list null? " + (list == null));

                if (list != null) {
                    Log.e("DEBUG_NOTIF", "size = " + list.size());
                }

                // 🔴 BADGE DEBUG (بدون تغيير منطق)
                SharedPreferences notifPrefs = getSharedPreferences("notifications", MODE_PRIVATE);

                if (list != null) {

                    boolean before = notifPrefs.getBoolean("has_notifications", false);
                    Log.e("DEBUG_NOTIF", "BEFORE badge = " + before);

                    if (list.isEmpty()) {
                        notifPrefs.edit().putBoolean("has_notifications", false).apply();
                        Log.e("DEBUG_NOTIF", "SET badge = FALSE");
                    } else {
                        notifPrefs.edit().putBoolean("has_notifications", true).apply();
                        Log.e("DEBUG_NOTIF", "SET badge = TRUE");
                    }

                    boolean after = notifPrefs.getBoolean("has_notifications", false);
                    Log.e("DEBUG_NOTIF", "AFTER badge = " + after);
                }

                // UI DEBUG
                if (list != null) {

                    for (NotificationModel n : list) {

                        if (n == null) {
                            Log.e("DEBUG_NOTIF", "NULL notification");
                            continue;
                        }

                        Log.e("DEBUG_NOTIF", "TYPE = " + n.type + " MSG = " + n.message);

                        if ("mission".equals(n.type)) {
                            cardMission.setVisibility(View.VISIBLE);
                            tvMission.setText(n.message);
                        }

                        if ("focus".equals(n.type)) {
                            cardFocus.setVisibility(View.VISIBLE);
                            tvFocus.setText(n.message);
                        }

                        if ("weekly".equals(n.type)) {
                            cardWeekly.setVisibility(View.VISIBLE);
                            tvWeekly.setText(n.message);
                        }

                        if ("streak".equals(n.type)) {
                            cardStreak.setVisibility(View.VISIBLE);
                            tvStreak.setText(n.message);
                        }
                    }
                }

                Log.e("DEBUG_NOTIF", "loadNotifications END");
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {

                Log.e("DEBUG_NOTIF", "API FAILED");
                Log.e("DEBUG_NOTIF", t.getMessage());
            }
        });
    }

    private void setupButtons() {

        Log.e("DEBUG_NOTIF", "setupButtons");

        Button btnContinueNow = findViewById(R.id.btnContinueNow);
        Button btnLater = findViewById(R.id.btnLater);
        Button btnInitialize = findViewById(R.id.btnInitialize);

        if (btnContinueNow != null) {
            btnContinueNow.setOnClickListener(v -> {
                startActivity(new Intent(this, DoingActivity.class));
                finish();
            });
        }

        if (btnLater != null) {
            btnLater.setOnClickListener(v -> finish());
        }

        if (btnInitialize != null) {
            btnInitialize.setOnClickListener(v -> {
                startActivity(new Intent(this, DoingActivity.class));
                finish();
            });
        }
    }
}