package com.example.projectpfe;

import android.os.Bundle;

public class CommunityActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // لما تصممي الواجهة، اكتبي هنا:
        setContentView(R.layout.activity_community);
         setupBottomNav(2);
        setupNotificationBell();
    }
}