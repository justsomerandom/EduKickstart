package com.example.edukickstart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class AnalogClockView extends View {

    private Paint circlePaint;
    private Paint handPaint;
    private float cx, cy;
    private float radius;
    private float hourHandLength, minuteHandLength;
    private int hour, minute;

    public AnalogClockView(Context context) {
        super(context);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5f);

        handPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handPaint.setColor(Color.BLACK);
        handPaint.setStyle(Paint.Style.FILL);
        handPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        cx = w / 2f;
        cy = h / 2f;
        radius = Math.min(w, h) / 2f;

        float hourHandLengthRatio = 0.6f;
        float minuteHandLengthRatio = 0.9f;
        hourHandLength = radius * hourHandLengthRatio;
        minuteHandLength = radius * minuteHandLengthRatio;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawCircle(canvas);
        drawHands(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(cx, cy, radius, circlePaint);
    }

    private void drawHands(Canvas canvas) {
        float hourRotation = (hour + minute / 60f) * 30f; // Each hour mark is 30 degrees
        float minuteRotation = (minute) * 6f; // Each minute mark is 6 degrees

        canvas.save();

        // Draw hour hand
        canvas.rotate(hourRotation, cx, cy);
        canvas.drawLine(cx, cy, cx, cy - hourHandLength, handPaint);

        // Draw minute hand
        canvas.rotate(minuteRotation - hourRotation, cx, cy);
        canvas.drawLine(cx, cy, cx, cy - minuteHandLength, handPaint);

        canvas.restore();
    }

    /**
     * Set the time for the analog clock.
     *
     * @param hour   The hour (0-11).
     * @param minute The minute (0-59).
     */
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        invalidate();
    }
}