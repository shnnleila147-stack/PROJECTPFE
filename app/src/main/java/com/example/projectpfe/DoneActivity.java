package com.example.projectpfe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DoneActivity extends BaseActivity {

    TextView tabToDo, tabDoing, tabDone;

    SeekBar seekAchieve, seekUnderstanding, seekPlan, seekFocus, seekEffort;
    TextView txtAchieveValue, txtUnderstandingValue, txtPlanValue, txtFocusValue, txtEffortValue;

    EditText inputLearning, inputImprove, inputMotivation;

    TextView tagUnderstanding, tagPractice, tagSummary, tagSelf_test;
    TextView tagExhausted, tagMotivated, tagFrustrated, tagCalm;
    String emotion = "Motivated";
    TextView btnYes, btnNo;

    Button btnComplete;

    boolean isConsistent = true;
    String bestMethod = "Understanding";

    long planId, userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        Log.d("DONE_DEBUG", "🚀 DoneActivity STARTED");

        setupNotificationBell();
        setupBottomNav(1);

        // ================= DATA =================
        planId = getIntent().getLongExtra("planId", -1);
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        Log.d("DONE_DEBUG", "planId = " + planId);
        Log.d("DONE_DEBUG", "userId = " + userId);

        // ================= BIND =================
        seekAchieve = findViewById(R.id.seekAchieve);
        seekUnderstanding = findViewById(R.id.seekUnderstanding);
        seekPlan = findViewById(R.id.seekPlan);
        seekFocus = findViewById(R.id.seekFocus);
        seekEffort = findViewById(R.id.seekEffort);

        inputLearning = findViewById(R.id.editLearning);
        inputImprove = findViewById(R.id.editImprove);
        inputMotivation = findViewById(R.id.editMotivation);

        tagUnderstanding = findViewById(R.id.tagUnderstanding);
        tagPractice = findViewById(R.id.tagPractice);
        tagSummary = findViewById(R.id.tagSummary);
        tagSelf_test = findViewById(R.id.tagSelf_test);

        tagExhausted = findViewById(R.id.tagExhausted);
        tagMotivated = findViewById(R.id.tagMotivated);
        tagFrustrated = findViewById(R.id.tagFrustrated);
        tagCalm = findViewById(R.id.tagCalm);
        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        btnComplete = findViewById(R.id.btnCompleteCheckin);

        txtAchieveValue = findViewById(R.id.txtAchieveValue);
        txtUnderstandingValue = findViewById(R.id.txtUnderstandingValue);
        txtPlanValue = findViewById(R.id.txtPlanValue);
        txtFocusValue = findViewById(R.id.txtFocusValue);
        txtEffortValue = findViewById(R.id.txtEffortValue);

        // ================= DEBUG NULL =================
        if (seekAchieve == null) Log.e("DONE_DEBUG", "seekAchieve NULL");
        if (btnComplete == null) Log.e("DONE_DEBUG", "btnComplete NULL");

        // ================= SEEK BARS =================
        setupSeekBar(seekAchieve, txtAchieveValue);
        setupSeekBar(seekUnderstanding, txtUnderstandingValue);
        setupSeekBar(seekPlan, txtPlanValue);
        setupSeekBar(seekFocus, txtFocusValue);
        setupSeekBar(seekEffort, txtEffortValue);

        // ================= TAGS =================
        tagUnderstanding.setOnClickListener(v -> {
            bestMethod = "Understanding";
            selectTag(tagUnderstanding, tagPractice, tagSummary, tagSelf_test);
            Log.d("DONE_DEBUG", "Tag Understanding clicked");
        });

        tagPractice.setOnClickListener(v -> {
            bestMethod = "Practice";
            selectTag(tagPractice, tagUnderstanding, tagSummary ,tagSelf_test);
            Log.d("DONE_DEBUG", "Tag Practice clicked");
        });

        tagSummary.setOnClickListener(v -> {
            bestMethod = "Summary";
            selectTag(tagSummary, tagUnderstanding, tagPractice, tagSelf_test);
            Log.d("DONE_DEBUG", "Tag Summary clicked");
        });

        tagSelf_test.setOnClickListener(v -> {
            bestMethod = "Self-test";
            selectTag(tagSelf_test, tagUnderstanding, tagPractice,tagSummary );
            Log.d("DONE_DEBUG", "Tag Self-test clicked");
         });
//--------------------

        tagExhausted.setOnClickListener(v -> {
            emotion = "Exhausted";
            selectEmotion(tagExhausted, tagMotivated, tagFrustrated, tagCalm);
        });

        tagMotivated.setOnClickListener(v -> {
            emotion = "Motivated";
            selectEmotion(tagMotivated, tagExhausted, tagFrustrated, tagCalm);
        });

        tagFrustrated.setOnClickListener(v -> {
            emotion = "Frustrated";
            selectEmotion(tagFrustrated, tagExhausted, tagMotivated, tagCalm);
        });

        tagCalm.setOnClickListener(v -> {
            emotion = "Calm";
            selectEmotion(tagCalm, tagExhausted, tagMotivated, tagFrustrated);
        });
        // ================= YES / NO =================
        btnYes.setOnClickListener(v -> {
            isConsistent = true;
            btnYes.setBackgroundResource(R.drawable.btn_save);
            btnNo.setBackgroundResource(R.drawable.tab_inactive_bg);
            Log.d("DONE_DEBUG", "YES clicked");
        });

        btnNo.setOnClickListener(v -> {
            isConsistent = false;
            btnNo.setBackgroundResource(R.drawable.btn_save);
            btnYes.setBackgroundResource(R.drawable.tab_inactive_bg);
            Log.d("DONE_DEBUG", "NO clicked");
        });

        // ================= SAVE BUTTON =================
        btnComplete.setOnClickListener(v -> {
            Log.d("DONE_DEBUG", "✅ COMPLETE CLICKED");
            saveReflection();
        });
        loadReflection();
    }

    // ================= SEEK BAR =================
    private void setupSeekBar(SeekBar seekBar, TextView label) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (label != null) label.setText(progress + "%");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // ================= TAG UI =================
    private void selectTag(TextView selected, TextView t1, TextView t2, TextView t3) {
        selected.setBackgroundResource(R.drawable.btn_save);
        selected.setTextColor(android.graphics.Color.BLACK);

        t1.setBackgroundResource(R.drawable.tab_inactive_bg);
        t2.setBackgroundResource(R.drawable.tab_inactive_bg);
        t3.setBackgroundResource(R.drawable.tab_inactive_bg);
        t1.setTextColor(android.graphics.Color.GRAY);
        t2.setTextColor(android.graphics.Color.GRAY);
        t3.setTextColor(android.graphics.Color.GRAY);
    }
    //=============
    private void selectEmotion(TextView selected, TextView t1, TextView t2, TextView t3) {
        selected.setBackgroundResource(R.drawable.btn_save);
        selected.setTextColor(android.graphics.Color.BLACK);

        t1.setBackgroundResource(R.drawable.tab_inactive_bg);
        t2.setBackgroundResource(R.drawable.tab_inactive_bg);
        t3.setBackgroundResource(R.drawable.tab_inactive_bg);

        t1.setTextColor(android.graphics.Color.GRAY);
        t2.setTextColor(android.graphics.Color.GRAY);
        t3.setTextColor(android.graphics.Color.GRAY);
    }
    // ================= SAVE =================
    private void saveReflection() {

        Log.d("DONE_DEBUG", "📡 SAVE START");

        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.12:8080/api/reflection/save");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject body = new JSONObject();

                body.put("planId", planId);
                body.put("userId", userId);

                body.put("achievement", seekAchieve.getProgress());
                body.put("understanding", seekUnderstanding.getProgress());
                body.put("planEffectiveness", seekPlan.getProgress());
                body.put("focus", seekFocus.getProgress());
                body.put("effort", seekEffort.getProgress());

                body.put("bestMethod", bestMethod);
                body.put("consistent", isConsistent);

                body.put("learningNote", inputLearning.getText().toString());
                body.put("improvementNote", inputImprove.getText().toString());
                body.put("motivationNote", inputMotivation.getText().toString());
                body.put("emotion", emotion);
                Log.d("DONE_DEBUG", "📤 JSON = " + body.toString());

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();

                Log.d("DONE_DEBUG", "Response Code = " + code);

                runOnUiThread(() -> {
                    if (code >= 200 && code < 300) {
                        Toast.makeText(this, "Saved ✅", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error ❌", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("DONE_DEBUG", "ERROR", e);
            }
        }).start();
    }

    //=================
    private void loadReflection() {


        new Thread(() -> {
            try {

                URL url = new URL("http://192.168.1.12:8080/api/reflection/" + userId + "/" + planId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                Scanner sc = new Scanner(conn.getInputStream());
                String res = sc.useDelimiter("\\A").next();

                JSONObject obj = new JSONObject(res);

                runOnUiThread(() -> {

                    // ================= SEEKBARS =================
                    seekAchieve.setProgress(obj.optInt("achievement"));
                    seekUnderstanding.setProgress(obj.optInt("understanding"));
                    seekPlan.setProgress(obj.optInt("planEffectiveness"));
                    seekFocus.setProgress(obj.optInt("focus"));
                    seekEffort.setProgress(obj.optInt("effort"));

                    // ================= TEXT =================
                    inputLearning.setText(obj.optString("learningNote"));
                    inputImprove.setText(obj.optString("improvementNote"));
                    inputMotivation.setText(obj.optString("motivationNote"));

                    // ================= BEST METHOD =================
                    bestMethod = obj.optString("bestMethod", "Understanding");

                    if (bestMethod.equals("Understanding")) {
                        selectTag(tagUnderstanding, tagPractice, tagSummary, tagSelf_test);
                    } else if (bestMethod.equals("Practice")) {
                        selectTag(tagPractice, tagUnderstanding, tagSummary, tagSelf_test);
                    } else if (bestMethod.equals("Summary")) {
                        selectTag(tagSummary, tagUnderstanding, tagPractice, tagSelf_test);
                    } else if (bestMethod.equals("Self-test")) {
                        selectTag(tagSelf_test, tagUnderstanding, tagPractice, tagSummary);
                    }

                    Log.d("DONE_DEBUG", "bestMethod = " + bestMethod);

                    // ================= CONSISTENT =================
                    isConsistent = obj.optBoolean("consistent", true);

                    if (isConsistent) {
                        btnYes.setBackgroundResource(R.drawable.btn_save);
                        btnNo.setBackgroundResource(R.drawable.tab_inactive_bg);
                    } else {
                        btnNo.setBackgroundResource(R.drawable.btn_save);
                        btnYes.setBackgroundResource(R.drawable.tab_inactive_bg);
                    }

                    Log.d("DONE_DEBUG", "consistent = " + isConsistent);

                    // ================= EMOTION =================
                    emotion = obj.optString("emotion", "Motivated");

                    if (emotion.equals("Exhausted")) {
                        selectEmotion(tagExhausted, tagMotivated, tagFrustrated, tagCalm);
                    } else if (emotion.equals("Motivated")) {
                        selectEmotion(tagMotivated, tagExhausted, tagFrustrated, tagCalm);
                    } else if (emotion.equals("Frustrated")) {
                        selectEmotion(tagFrustrated, tagExhausted, tagMotivated, tagCalm);
                    } else if (emotion.equals("Calm")) {
                        selectEmotion(tagCalm, tagExhausted, tagMotivated, tagFrustrated);
                    }

                    Log.d("DONE_DEBUG", "emotion = " + emotion);
                });

            } catch (Exception e) {
                Log.e("DONE_DEBUG", "LOAD ERROR", e);
            }
        }).start();


    }

}
