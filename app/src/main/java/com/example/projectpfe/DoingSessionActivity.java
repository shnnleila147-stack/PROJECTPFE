package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DoingSessionActivity extends AppCompatActivity {

    Button btnDayCompleted;
    long planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_session);

        btnDayCompleted = findViewById(R.id.btnDayCompleted);

        planId = getIntent().getLongExtra("planId", -1);

        btnDayCompleted.setOnClickListener(v -> finishSession());
    }

    // 🔥 إنهاء session + إرسال feedback
    private void finishSession() {

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.3:8080/api/plan/feedback");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("planId", planId);
                body.put("success", true); // يمكن تغييره لاحقا حسب الأداء

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Session saved ✅", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, DoneActivity.class));
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error saving session", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}