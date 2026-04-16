package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.projectpfe.api.ApiClient;
import com.example.projectpfe.api.ApiService;
import com.example.projectpfe.model.PersonalizationRequest;
import com.example.projectpfe.model.PersonalizationResponse;
import com.example.projectpfe.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalizationQuestionsActivity extends AppCompatActivity {

    private String[] questions = {
            "How satisfied are you with your current study-life balance?",
            "How well do you manage your time for studying?",
            "How confident are you in your academic performance?",
            "How motivated are you to achieve your goals?",
            "How comfortable are you with asking for help?"
    };
    // ✅ تحديد الشخصية (بسيط مؤقت)
    private String getPersonalityType() {
        int sum = 0;
        for (int a : answers) {
            sum += a;
        }

        if (sum >= 15) return "organized";
        else if (sum >= 10) return "balanced";
        else return "needs_guidance";
    }

    // ✅ تحديد الجدول
    private String getScheduleType() {
        int avg = 0;
        for (int a : answers) {
            avg += a;
        }
        avg = avg / answers.length;

        if (avg >= 3) return "busy";
        else return "flexible";
    }
    private int currentQuestion = 0;
    private final int totalQuestions = questions.length;
    private int[] answers;
    private int selectedAnswer = -1;
    private View[] circles;

    private TextView tvQuestion;
    private TextView tvQuestionIndicator;
    private AppCompatButton btnBack;
    private AppCompatButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PERS_QS", "PersonalizationQuestionsActivity created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalization_questions);

        // تهيئة الإجابات
        answers = new int[totalQuestions];
        for (int i = 0; i < totalQuestions; i++) {
            answers[i] = -1;
        }

        // ربط العناصر
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionIndicator = findViewById(R.id.tvQuestionIndicator);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        ImageView ivBack = findViewById(R.id.ivBack);

        circles = new View[]{
                findViewById(R.id.circle1),
                findViewById(R.id.circle2),
                findViewById(R.id.circle3),
                findViewById(R.id.circle4),
                findViewById(R.id.circle5)
        };

        // الضغط على الدوائر
        for (int i = 0; i < circles.length; i++) {
            final int index = i;
            circles[i].setOnClickListener(v -> selectAnswer(index));
        }

        displayQuestion();

        // زر العودة السهم
        ivBack.setOnClickListener(v -> finish());

        // زر BACK
        btnBack.setOnClickListener(v -> {
            if (currentQuestion > 0) {
                answers[currentQuestion] = selectedAnswer;
                currentQuestion--;
                selectedAnswer = answers[currentQuestion];
                displayQuestion();
            } else {
                finish();
            }
        });

        // زر NEXT
        btnNext.setOnClickListener(v -> {
            Log.d("PERS_QS", "Next button clicked. Current question: " + currentQuestion);
            if (selectedAnswer == -1) {
                Log.d("PERS_QS", "No answer selected!");
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // حفظ الإجابة الحالية
            answers[currentQuestion] = selectedAnswer;

            if (currentQuestion < totalQuestions - 1) {
                currentQuestion++;
                selectedAnswer = answers[currentQuestion];
                displayQuestion();
                Log.d("PERS_QS", "Moved to question: " + currentQuestion);
            } else {
                Log.d("PERS_QS", "Last question reached, sending answers to server");
                // نهاية الأسئلة → إرسال البيانات
                Toast.makeText(this, "Analyzing your personality...", Toast.LENGTH_SHORT).show();
                sendAnswersToServer();
            }
        });
    }

    private void displayQuestion() {
        tvQuestion.setText(questions[currentQuestion]);
        tvQuestionIndicator.setText("Question " + (currentQuestion + 1) + " of " + totalQuestions);
        selectedAnswer = answers[currentQuestion];
        updateCircles();
    }

    private void selectAnswer(int index) {
        selectedAnswer = index;
        updateCircles();
    }

    private void updateCircles() {
        for (int i = 0; i < circles.length; i++) {
            if (i == selectedAnswer) {
                circles[i].setBackgroundResource(R.drawable.circle_selected);
            } else {
                circles[i].setBackgroundResource(R.drawable.circle_unselected);
            }
        }
    }

    private void sendAnswersToServer() {

        long userId = getSharedPreferences("user", MODE_PRIVATE)
                .getLong("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            return;
        }

        PersonalizationRequest request = new PersonalizationRequest();
        request.setUserId(userId);
        request.setQ1(answers[0] + 1);
        request.setQ2(answers[1] + 1);
        request.setQ3(answers[2] + 1);
        request.setQ4(answers[3] + 1);
        request.setQ5(answers[4] + 1);

        ApiService apiService = ApiClient.getService();

        apiService.saveAnswersAndSetPersonalized(request)
                .enqueue(new Callback<PersonalizationResponse>() {

                    @Override
                    public void onResponse(Call<PersonalizationResponse> call,
                                           Response<PersonalizationResponse> response) {

                        if (response.isSuccessful()) {

                            // 🔥 هنا الحل الحقيقي
                            updateUserPersonalized(userId);

                        } else {
                            Toast.makeText(
                                    PersonalizationQuestionsActivity.this,
                                    "Server error",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PersonalizationResponse> call, Throwable t) {
                        Toast.makeText(
                                PersonalizationQuestionsActivity.this,
                                "Network error",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void updateUserPersonalized(long userId) {

        ApiService apiService = ApiClient.getService();

        apiService.setUserPersonalized(userId)
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {

                        if (response.isSuccessful()) {

                            // ✅ حفظ الحالة محليًا
                            getSharedPreferences("user", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("is_personalized", true)
                                    .apply();

                            // 🚀 الانتقال إلى Home
                            Intent intent = new Intent(
                                    PersonalizationQuestionsActivity.this,
                                    HomeActivity.class
                            );
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(
                                PersonalizationQuestionsActivity.this,
                                "Failed to update user",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}