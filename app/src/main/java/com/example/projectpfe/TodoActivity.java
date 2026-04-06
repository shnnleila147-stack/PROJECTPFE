package com.example.projectpfe;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TodoActivity extends AppCompatActivity {

    TextView tabToDo, tabDoing, tabDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        tabToDo  = findViewById(R.id.tabToDo);
        tabDoing = findViewById(R.id.tabDoing);
        tabDone  = findViewById(R.id.tabDone);

        tabToDo.setOnClickListener(v -> setActiveTab(0));
        tabDoing.setOnClickListener(v -> setActiveTab(1));
        tabDone.setOnClickListener(v -> setActiveTab(2));
    }

    private void setActiveTab(int index) {
        // إعادة كل التابات لغير مفعّل
        tabToDo.setTextColor(0xFF666680);
        tabDoing.setTextColor(0xFF666680);
        tabDone.setTextColor(0xFF666680);
        tabToDo.setBackgroundResource(R.drawable.tab_inactive_bg);
        tabDoing.setBackgroundResource(R.drawable.tab_inactive_bg);
        tabDone.setBackgroundResource(R.drawable.tab_inactive_bg);

        // تفعيل التاب المختار
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