package com.example.projectpfe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View {

    private Paint wavePaint;
    private Path wavePath;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setColor(0xFF00E5FF);
        wavePaint.setStrokeWidth(4f);
        wavePaint.setStrokeCap(Paint.Cap.ROUND);

        wavePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();
        float mid = h / 2f;

        wavePath.reset();
        wavePath.moveTo(0, mid + 20);
        wavePath.cubicTo(w * 0.15f, mid + 30, w * 0.2f, mid - 10, w * 0.35f, mid - 20);
        wavePath.cubicTo(w * 0.5f,  mid - 30, w * 0.55f, mid + 5,  w * 0.65f, mid);
        wavePath.cubicTo(w * 0.75f, mid - 5,  w * 0.85f, mid - 25, w,          mid - 15);

        canvas.drawPath(wavePath, wavePaint);
    }
}