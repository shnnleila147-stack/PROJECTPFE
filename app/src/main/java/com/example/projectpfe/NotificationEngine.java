package com.example.projectpfe;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class NotificationEngine {

    private Context context;

    public NotificationEngine(Context context) {
        this.context = context;
    }

    public List<NotificationModel> generate() {

        List<NotificationModel> list = new ArrayList<>();

        // 🔴 1. Mission Reminder
        if (isNoActivityToday()) {
            list.add(new NotificationModel(
                    "mission",
                    "You haven't started your study today. Start now to stay consistent."
            ));
        }

        // 🟡 2. Focus Warning
        if (isSessionIncomplete()) {
            list.add(new NotificationModel(
                    "focus",
                    "You have an unfinished session. Continue now to maintain flow."
            ));
        }

        // 🟢 3. Weekly update
        list.add(new NotificationModel(
                "weekly",
                "Your weekly progress is improving. Keep going!"
        ));

        // 🔥 4. Streak motivation
        if (isStreakActive()) {
            list.add(new NotificationModel(
                    "streak",
                    "Great! You are maintaining a strong learning streak 🔥"
            ));
        }

        return list;
    }

    // =========================
    // RULES (logic مؤقت)
    // =========================

    private boolean isNoActivityToday() {
        return true; // لاحقًا تربطه بقاعدة البيانات
    }

    private boolean isSessionIncomplete() {
        return true;
    }

    private boolean isStreakActive() {
        return true;
    }
}