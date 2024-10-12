package com.example.avaliaon1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

/**
 * TODO: document your custom view class.
 */
public class CoordinatesView extends View {

    private double mLatitude;
    private double mLongitude;
    private CoordinateTypes mType = CoordinateTypes.GRAUS;

    private Paint backgroundPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Paint textPaint = new Paint();

    public CoordinatesView(Context context) {
        super(context);
        init(null, 0);
    }

    public CoordinatesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CoordinatesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        backgroundPaint.setColor(Color.rgb(0, 0, 156));
        backgroundPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(12);
        borderPaint.setColor(Color.rgb(218, 165, 32));

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(22);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Plano de fundo
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);

        //Latitude e Longitude
        canvas.drawText("Latitude: " + getFormatedDegrees(mLatitude), 20, 100, textPaint);
        canvas.drawText("Longitude: " + getFormatedDegrees(mLongitude), 20, 190, textPaint);
    }


    private String getFormatedDegrees(double degrees) {
        if (degrees != 0) {
            switch (mType) {
                case GRAUS:
                    return String.format(new Locale("pt", "BR"), "%+.5f", degrees);
                case GRAUS_MINUTOS:
                    return degreesToDMM(degrees);
                case GRAUS_MINUTOS_SEGUNDOS:
                    return degreesToDMS(degrees);
            }
        }

        return "N/A";
    }

    private String degreesToDMM(double degrees) {
        double d = Math.floor(degrees);
        double m = (degrees - d) * 60.0;
        return String.format(new Locale("pt", "BR"), "%+.0f:%06.3f", d, m);
    }

    private String degreesToDMS(double degrees) {
        double d = Math.floor(degrees);
        double m = Math.floor((degrees - d) * 60.0);
        double s = (degrees - d - m / 60.0) * 3600.0;
        return String.format(new Locale("pt", "BR"), "%+.0f:%02.0f:%06.3f", d, m, s);

    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
        invalidate();
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
        invalidate();
    }

    public void setCoordinates(double latitude, double longitude) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        invalidate();
    }

    public void setType(CoordinateTypes type) {
        this.mType = type;
        invalidate();
    }
}