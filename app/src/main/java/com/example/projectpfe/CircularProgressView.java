package com.example.projectpfe;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressView extends View {

    private Paint bgPaint;
    private Paint progressPaint;
    private Paint glowPaint;
    private Paint centerGlowPaint;
    private RectF rectF;
    private RectF glowRectF;

    private int progress = 75;
    private int max = 100;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // ⬅️ هذا السطر ضروري جداً لظهور التوهج
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // دائرة الخلفية الداكنة
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(0xFF1A2A2A);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        // قوس التقدم الأزرق
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(0xFF00E5FF);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // التوهج الخارجي حول القوس
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setColor(0xFF00E5FF);
        glowPaint.setStrokeCap(Paint.Cap.ROUND);
        glowPaint.setMaskFilter(new BlurMaskFilter(30f, BlurMaskFilter.Blur.NORMAL));

        // التوهج الداخلي (الضوء خلف الدائرة)
        centerGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerGlowPaint.setStyle(Paint.Style.FILL);
        centerGlowPaint.setColor(0x1500E5FF);
        centerGlowPaint.setMaskFilter(new BlurMaskFilter(60f, BlurMaskFilter.Blur.NORMAL));

        rectF = new RectF();
        glowRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().density;
        float strokeWidth = 14f * density;
        float glowStrokeWidth = 22f * density;

        int width = getWidth();
        int height = getHeight();
        float cx = width / 2f;
        float cy = height / 2f;

        bgPaint.setStrokeWidth(strokeWidth);
        progressPaint.setStrokeWidth(strokeWidth);
        glowPaint.setStrokeWidth(glowStrokeWidth);

        float padding = glowStrokeWidth / 2f + 8f * density;
        rectF.set(padding, padding, width - padding, height - padding);

        // ✅ 1) رسم التوهج المركزي الداخلي (الضوء خلف الدائرة)
        float radius = (width - padding * 2) / 2f;
        canvas.drawCircle(cx, cy, radius * 0.7f, centerGlowPaint);

        // ✅ 2) رسم الحلقة الخلفية الداكنة
        canvas.drawArc(rectF, 0, 360, false, bgPaint);

        // ✅ 3) حساب زاوية التقدم
        float sweepAngle = (360f * progress) / max;

        // ✅ 4) رسم التوهج خلف القوس
        glowRectF.set(padding + 4 * density, padding + 4 * density,
                width - padding - 4 * density, height - padding - 4 * density);
        canvas.drawArc(glowRectF, -90, sweepAngle, false, glowPaint);

        // ✅ 5) رسم قوس التقدم فوق التوهج
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }
}