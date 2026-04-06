package com.example.projectpfe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TodoAiActivity extends AppCompatActivity {

    TextView tabToDo, tabDoing, tabDone;
    Button btnChoosePlan1, btnChoosePlan2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_ai);

        tabToDo = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone = findViewById(R.id.tabDone);
        btnChoosePlan1 = findViewById(R.id.btnChoosePlan1);
        btnChoosePlan2 = findViewById(R.id.btnChoosePlan2);

        // ✅ Tab TO DO
        tabToDo.setOnClickListener(v -> {
            setActiveTab(0);
            startActivity(new Intent(this, TodoActivity.class));
            finish();
        });

        // ✅ Tab DOING
        tabDoing.setOnClickListener(v -> {
            setActiveTab(1);
            startActivity(new Intent(this, DoingActivity.class));
            finish();
        });

        // ✅ Tab DONE
        tabDone.setOnClickListener(v -> {
            setActiveTab(2);
            startActivity(new Intent(this, DoneActivity.class));
            finish();
        });

        // أزرار الخطط
        btnChoosePlan1.setOnClickListener(v ->
                Toast.makeText(this, "Plan 1: Focused Concept selected!", Toast.LENGTH_SHORT).show()
        );

        btnChoosePlan2.setOnClickListener(v ->
                Toast.makeText(this, "Plan 2: Micro-Step selected!", Toast.LENGTH_SHORT).show()
        );
    }

    private void setActiveTab(int index) {
        tabToDo.setTextColor(0xFF666680);
        tabDoing.setTextColor(0xFF666680);
        tabDone.setTextColor(0xFF666680);

        tabToDo.setBackgroundResource(R.drawable.tab_inactive_bg);
        tabDoing.setBackgroundResource(R.drawable.tab_inactive_bg);
        tabDone.setBackgroundResource(R.drawable.tab_inactive_bg);

        if (index == 0) {
            tabToDo.setTextColor(0xFF00E5FF);
            tabToDo.setBackgroundResource(R.drawable.tab_active_bg);
        } else if (index == 1) {
            tabDoing.setTextColor(0xFF00E5FF);
            tabDoing.setBackgroundResource(R.drawable.tab_active_bg);
        } else {
            tabDone.setTextColor(0xFF00E5FF);
            tabDone.setBackgroundResource(R.drawable.tab_active_bg);
        }
    }
}