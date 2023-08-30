package com.example.myapplication.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class MyCanvas  extends View {

    Rect rect;
    Paint paint;
    float xPos;
    float yPos;
    public MyCanvas(Context context) {
        super(context);
        //drawView.setBackground(getResources().getDrawable(R.drawable.background));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rect = new Rect(0,100,200,300);

        paint =new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(400);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(xPos,yPos,100,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xPos = event.getX();
        yPos = event.getY();
        invalidate();
        return super.onTouchEvent(event);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.setBackgroundColor(Color.RED);
    }
}
