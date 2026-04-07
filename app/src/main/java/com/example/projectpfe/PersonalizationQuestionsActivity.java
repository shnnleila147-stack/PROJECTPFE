package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.projectpfe.api.ApiClient;
import com.example.projectpfe.api.ApiService;
import com.example.projectpfe.model.PersonalizationRequest;
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

    private int currentQuestion = 0;
    private final int totalQuestions = 5;
    private int[] answers;
    private int selectedAnswer = -1;

    private View[] circles;
    private TextView tvQuestion;
    private TextView tvQuestionIndicator;
    private AppCompatButton btnBack;
    private AppCompatButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // سهم الرجوع
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
            if (selectedAnswer == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // حفظ الإجابة الحالية
            answers[currentQuestion] = selectedAnswer;

            if (currentQuestion < totalQuestions - 1) {
                currentQuestion++;
                selectedAnswer = answers[currentQuestion];
                displayQuestion();
            } else {
                // نهاية الأسئلة → إرسال البيانات
                sendAnswersToServer();
            }
        });
    }

    private void displayQuestion() {
        tvQuestion.setText(questions[currentQuestion]);
        tvQuestionIndicator.setText("Question " + (currentQuestion + 1) + " of " + totalQuestions);
        selectedAnswer = answers[currentQuestion]; // تحديث المحدد
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

        PersonalizationRequest request = new PersonalizationRequest();

        int userId = getSharedPreferences("user", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        request.setUserId(userId);
        request.setQ1(answers[0] + 1);
        request.setQ2(answers[1] + 1);
        request.setQ3(answers[2] + 1);
        request.setQ4(answers[3] + 1);
        request.setQ5(answers[4] + 1);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // ✅ 1. نحفظ الإجابات
        apiService.saveAnswers(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {

                    // ✅ 2. نحدث personalization = true
                    apiService.setPersonalized(userId).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            if (response.isSuccessful()) {
                                Toast.makeText(PersonalizationQuestionsActivity.this,
                                        "", Toast.LENGTH_SHORT).show();

                                // ✅ 3. نروح Home
                                startActivity(new Intent(PersonalizationQuestionsActivity.this, HomeActivity.class));
                                finish();

                            } else {
                                Toast.makeText(PersonalizationQuestionsActivity.this,
                                        "Error updating user", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(PersonalizationQuestionsActivity.this,
                                    "Server error", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(PersonalizationQuestionsActivity.this,
                            "Error saving answers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PersonalizationQuestionsActivity.this,
                        "Network error", Toast.LENGTH_LONG).show();
            }
        });
    }
}