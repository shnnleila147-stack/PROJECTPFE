package com.example.projectpfe;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class PersonalizationQuestionsActivity extends AppCompatActivity {

    // قائمة الأسئلة
    private String[] questions = {
            "How satisfied are you with your current study-life balance?",
            "How well do you manage your time for studying?",
            "How confident are you in your academic performance?",
            "How motivated are you to achieve your goals?",
            "How comfortable are you with asking for help?"
    };

    private int currentQuestion = 0;       // السؤال الحالي (يبدأ من 0)
    private int totalQuestions = 5;         // عدد الأسئلة الكلي
    private int[] answers;                  // مصفوفة لحفظ الإجابات
    private int selectedAnswer = -1;        // الإجابة المحددة حالياً (-1 = لا شيء)

    // الدوائر
    private View[] circles;

    // العناصر
    private TextView tvQuestion;
    private TextView tvQuestionIndicator;
    private AppCompatButton btnBack;
    private AppCompatButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalization_questions);

        // تهيئة مصفوفة الإجابات
        answers = new int[totalQuestions];
        for (int i = 0; i < totalQuestions; i++) {
            answers[i] = -1; // لا إجابة بعد
        }

        // ربط العناصر
        tvQuestion = findViewById(R.id.tvQuestion);
        tvQuestionIndicator = findViewById(R.id.tvQuestionIndicator);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        ImageView ivBack = findViewById(R.id.ivBack);

        // ربط الدوائر
        circles = new View[]{
                findViewById(R.id.circle1),
                findViewById(R.id.circle2),
                findViewById(R.id.circle3),
                findViewById(R.id.circle4),
                findViewById(R.id.circle5)
        };

        // إعداد الضغط على الدوائر
        for (int i = 0; i < circles.length; i++) {
            final int index = i;
            circles[i].setOnClickListener(v -> selectAnswer(index));
        }

        // عرض السؤال الأول
        displayQuestion();

        // سهم الرجوع في الأعلى
        ivBack.setOnClickListener(v -> finish());

        // زر BACK
        btnBack.setOnClickListener(v -> {
            if (currentQuestion > 0) {
                // حفظ الإجابة الحالية
                answers[currentQuestion] = selectedAnswer;
                // الرجوع للسؤال السابق
                currentQuestion--;
                selectedAnswer = answers[currentQuestion];
                displayQuestion();
            } else {
                // إذا كنا في السؤال الأول، نرجع للواجهة السابقة
                finish();
            }
        });

        // زر NEXT
        btnNext.setOnClickListener(v -> {
            if (selectedAnswer == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            // حفظ الإجابة
            answers[currentQuestion] = selectedAnswer;

            if (currentQuestion < totalQuestions - 1) {
                // الانتقال للسؤال التالي
                currentQuestion++;
                selectedAnswer = answers[currentQuestion];
                displayQuestion();
            } else {
                // انتهت جميع الأسئلة
                Toast.makeText(this, "Personalization complete!", Toast.LENGTH_SHORT).show();

                // هنا يمكنك الانتقال للواجهة التالية (مثلاً الصفحة الرئيسية)
                // Intent intent = new Intent(this, HomeActivity.class);
                // startActivity(intent);
                // finish();
            }
        });
    }

    // عرض السؤال الحالي
    private void displayQuestion() {
        tvQuestion.setText(questions[currentQuestion]);
        tvQuestionIndicator.setText("Question " + (currentQuestion + 1) + " of " + totalQuestions);
        updateCircles();
    }

    // تحديد إجابة
    private void selectAnswer(int index) {
        selectedAnswer = index;
        updateCircles();
    }

    // تحديث شكل الدوائر
    private void updateCircles() {
        for (int i = 0; i < circles.length; i++) {
            if (i == selectedAnswer) {
                circles[i].setBackgroundResource(R.drawable.circle_selected);
            } else {
                circles[i].setBackgroundResource(R.drawable.circle_unselected);
            }
        }
    }
}
