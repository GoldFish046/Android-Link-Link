package com.example.work.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class DrawLineView extends View {

    private float[] path;

    public DrawLineView(Context context) {
        super(context);
    }

    public DrawLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DrawLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DrawLineView(Context context, float[] path) {
        super(context);
        this.path = path;
    }

    private void drawLines(Canvas canvas) {
        Log.d("click", Arrays.toString(path));
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(100);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < path.length / 2 - 1; i++){

        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLines(canvas);
    }

    public void setPath(float[] path) {
        this.path = path;
    }
}
