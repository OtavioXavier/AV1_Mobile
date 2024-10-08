
package com.example.avaliaon1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.Nullable;

public class PositionUser extends View {

    private Paint textPaint;

    private String mLatitude = "Latitude: N/A";
    private String mLongitude = "Longitude: N/A";
    private float mDirecao = 0;

    @ColorInt
    private int textColor;

    @Dimension
    private float textHeight;

    public PositionUser(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textColor = textColor == 0 ? Color.BLACK : textColor;
        textPaint.setColor(textColor);

        if (textHeight == 0) {
            textHeight = 40;
        }
        textPaint.setTextSize(textHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.GRAY);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        canvas.drawText(mLatitude, 20, getHeight() - 40, textPaint);
        canvas.drawText(mLongitude, 20, getHeight() - 10, textPaint);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seta_direita);

        int newWidth = 100;
        int newHeight = 100;

        float left = getWidth() - newWidth - 50;
        float top = getHeight() - newHeight - 30;

        float pivotX = left + newWidth / 2;
        float pivotY = top + newHeight / 2;

        canvas.save();
        canvas.rotate(mDirecao, pivotX, pivotY);



        Rect destRect = new Rect((int) left, (int) top, (int) (left + newWidth), (int) (top + newHeight));

        canvas.drawBitmap(bitmap, null, destRect, null);
        canvas.restore();
    }

    public void setPosition(double latitude, double longitude) {
        mLatitude = "Latitude: " + latitude;
        mLongitude = "Longitude: " + longitude;
        invalidate();
    }

    public void setRotate(float rotacao ) {
        mDirecao = rotacao -90;
        invalidate();

    }

}