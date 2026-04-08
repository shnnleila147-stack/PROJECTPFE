package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PersonalizationActivity extends AppCompatActivity {

    Button btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalization);

        // ربط الزر مع XML
        btnNext = findViewById(R.id.btnNext);

        // عند الضغط على Next ننتقل لواجهة الأسئلة
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalizationActivity.this, PersonalizationQuestionsActivity.class);
                startActivity(intent);
            }
        });
    }
}