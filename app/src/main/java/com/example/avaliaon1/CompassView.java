package com.example.avaliaon1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

    private float mDegree = 0;

    private Paint backgroundPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Paint textPaint = new Paint();

    public CompassView(Context context) {
        super(context);
        init(null, 0);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        backgroundPaint.setColor(Color.rgb(0, 0, 156));
        backgroundPaint.setAntiAlias(true);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(12);
        borderPaint.setColor(Color.rgb(218, 165, 32));

        textPaint.setTextSize(22);
        textPaint.setFakeBoldText(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Plano de fundo
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);

        // Coordenadas da seta (direção)
        int newWidth = 100;
        int newHeight = 100;
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float left = centerX - newWidth / 2;
        float top = centerY - newHeight / 2;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seta_direita);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        Matrix matrix = new Matrix();

        matrix.postTranslate(-scaledBitmap.getWidth() / 2, -scaledBitmap.getHeight() / 2);
        matrix.postRotate(mDegree);
        matrix.postTranslate(centerX, centerY);

        canvas.drawBitmap(scaledBitmap, matrix, null);

        // Mostrar o ângulo atual
        canvas.drawText(mDegree + "°", 25, 40, textPaint);
    }


    public void setDegree(float degree) {
        this.mDegree = degree;
        invalidate();
    }
}