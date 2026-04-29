package com.example.projectpfe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class WaveView extends View {

    private Paint wavePaint;
    private Path wavePath;
    private List<Integer> data = new ArrayList<>();

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setColor(0xFF00E5FF);
        wavePaint.setStrokeWidth(4f);

        wavePath = new Path();
    }

    public void setData(List<Integer> values) {
        this.data = values;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.size() < 2) return;

        wavePath.reset();

        float width = getWidth();
        float height = getHeight();

        float stepX = width / (data.size() - 1);

        for (int i = 0; i < data.size(); i++) {
            float x = i * stepX;
            float y = height - (data.get(i) / 5f) * height;

            if (i == 0) {
                wavePath.moveTo(x, y);
            } else {
                wavePath.lineTo(x, y);
            }
        }

        canvas.drawPath(wavePath, wavePaint);
    }
}