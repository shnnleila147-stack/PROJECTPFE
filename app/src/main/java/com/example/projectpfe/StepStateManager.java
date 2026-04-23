package com.example.projectpfe;

import android.os.Handler;
import android.util.Log;

public class StepStateManager {

    private long startTime;
    private boolean running = false;
    private long duration;

    private Handler handler = new Handler();
    private Runnable timerRunnable;

    public interface Callback {
        void onProgress(int progress, long elapsed);
    }

    private Callback callback;

    public StepStateManager(long duration, Callback callback) {
        this.duration = duration;
        this.callback = callback;
    }

    public void startStep(long alreadyElapsed) {

        stop(); // مهم جداً
        String index = null;
        Log.d("DEBUG", "🚀 startStep CALLED index=" + index);
        startTime = System.currentTimeMillis() - alreadyElapsed;
        running = true;

        Log.d("TIMER", "START STEP - elapsed=" + alreadyElapsed);

        timerRunnable = new Runnable() {
            @Override
            public void run() {

                if (!running) return;

                long elapsed = System.currentTimeMillis() - startTime;

                int progress = (int) ((elapsed * 100) / duration);

                if (progress >= 100) {
                    progress = 100;
                    running = false;
                }

                Log.d("TIMER", "progress=" + progress + " elapsed=" + elapsed);

                callback.onProgress(progress, elapsed);

                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(timerRunnable, 1000);
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(timerRunnable);
    }

    public boolean isRunning() {
        return running;
    }
}