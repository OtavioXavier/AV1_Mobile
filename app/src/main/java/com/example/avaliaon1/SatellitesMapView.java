package com.example.avaliaon1;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SatellitesMapView extends View {

    private GnssStatus status;
    private int height, width, raio;
    private static final int FLAG_WIDTH = 64;
    private static final int FLAG_HEIGHT = 44;

    private String filteredConstellations = "All";
    private boolean filterUsedInFix = false;
    private float rotationAngle = 0f;


    private Paint circlePaint = new Paint();
    private Paint satellitePaint = new Paint();
    private Paint backgroundPaint = new Paint();
    private Paint borderPaint = new Paint();
    private Paint linePaint = new Paint();
    private Paint concentricCirclePaint = new Paint();
    private Paint borderCirclePaint = new Paint();
    private Paint textPaint = new Paint();

    public SatellitesMapView(Context context) {
        super(context);
        init(null, 0);
    }

    public SatellitesMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SatellitesMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        backgroundPaint.setColor(Color.rgb(0, 0, 156));
        backgroundPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(0xFF000000);
        linePaint.setStrokeWidth(5);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(12);
        borderPaint.setColor(Color.rgb(218, 165, 32));
        borderPaint.setAntiAlias(true);

        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.rgb(34, 139, 34));
        circlePaint.setAntiAlias(true);

        borderCirclePaint.setStyle(Paint.Style.STROKE);
        borderCirclePaint.setColor(0xFF000000);
        borderCirclePaint.setStrokeWidth(10);
        borderCirclePaint.setAntiAlias(true);

        concentricCirclePaint.setStyle(Paint.Style.STROKE);
        concentricCirclePaint.setColor(0xFF000000);
        concentricCirclePaint.setStrokeWidth(5);
        concentricCirclePaint.setAntiAlias(true);

        satellitePaint.setColor(Color.RED);
        satellitePaint.setStyle(Paint.Style.FILL);
        satellitePaint.setAntiAlias(true);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Plano de fundo
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);

        // definindo o raio do circulo
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (width < height)
            raio = (int) (width / 2 * 0.9);
        else
            raio = (int) (height / 2 * 0.9);
        // Circulo

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        canvas.save();
        canvas.rotate(rotationAngle, centerX, centerY);

        canvas.drawCircle(centerX, centerY, raio, circlePaint);
        canvas.drawCircle(centerX, centerY, raio, borderCirclePaint);

        for (int i = 1; i <= 3; i++) {
            int smallerRadius = raio - (i * (raio / 4));
            canvas.drawCircle(centerX, centerY, smallerRadius, concentricCirclePaint);
        }

        canvas.drawLine(centerX - raio, centerY, centerX + raio, centerY, linePaint);
        canvas.drawLine(centerX, centerY - raio, centerX, centerY + raio, linePaint);

        // Desenhar lista de satelites
        if (status != null) {
            for (int i = 0; i < status.getSatelliteCount(); i++) {
                boolean shouldDraw = filteredConstellations.equals("All") ||
                        getConstellationName(status.getConstellationType(i)).equals(filteredConstellations);
                if (shouldDraw && (!filterUsedInFix || (filterUsedInFix && status.usedInFix(i)))) {
                    float az = status.getAzimuthDegrees(i);
                    float el = status.getElevationDegrees(i);
                    float x = (float) (raio * Math.cos(Math.toRadians(el)) * Math.sin(Math.toRadians(az)));
                    float y = (float) (raio * Math.cos(Math.toRadians(el)) * Math.cos(Math.toRadians(az)));
                    satellitePaint.setColor(status.usedInFix(i) ? Color.GREEN : Color.RED);
                    canvas.drawCircle(computeXc(x), computeYc(y), 10, satellitePaint);
                    String satID = status.getSvid(i) + " " + getConstellationName(status.getConstellationType(i));
                    canvas.drawText(satID, computeXc(x) + 10, computeYc(y) + 10, textPaint);
                    Drawable constellationIcon = getConstellationIcon(status.getConstellationType(i));
                    if (constellationIcon != null) {
                        int flagLeft = computeXc(x) - (FLAG_WIDTH / 2);
                        int flagTop = computeYc(y) + 20;
                        int flagRight = flagLeft + FLAG_WIDTH;
                        int flagBottom = flagTop + FLAG_HEIGHT;
                        constellationIcon.setBounds(flagLeft, flagTop, flagRight, flagBottom);
                        constellationIcon.draw(canvas);
                    }
                }
            }
        }
    }

    private Drawable getConstellationIcon(int constellationType) {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_GPS:
                return getResources().getDrawable(R.drawable.eua);
            case GnssStatus.CONSTELLATION_GLONASS:
                return getResources().getDrawable(R.drawable.russia);
            case GnssStatus.CONSTELLATION_BEIDOU:
                return getResources().getDrawable(R.drawable.china);
            case GnssStatus.CONSTELLATION_GALILEO:
                return getResources().getDrawable(R.drawable.eu);
            case GnssStatus.CONSTELLATION_QZSS:
                return getResources().getDrawable(R.drawable.japao);
            default:
                return null;
        }
    }

    private String getConstellationName(int constellationType) {
        switch (constellationType) {
            case GnssStatus.CONSTELLATION_GPS:
                return "GPS";
            case GnssStatus.CONSTELLATION_GLONASS:
                return "Glonass";
            case GnssStatus.CONSTELLATION_BEIDOU:
                return "Beidou";
            case GnssStatus.CONSTELLATION_GALILEO:
                return "Galileo";
            case GnssStatus.CONSTELLATION_QZSS:
                return "QZSS";
            default:
                return "Unknown";
        }
    }

    private int computeXc(double x) {
        return (int) (x + width / 2);
    }

    private int computeYc(double y) {
        return (int) (-y + height / 2);
    }

    public void setStatus(GnssStatus status) {
        this.status = status;
        invalidate();
    }

    public void setFilters(String satellites, boolean usedIn) {
        this.filteredConstellations = satellites;
        this.filterUsedInFix = usedIn;
        invalidate();
    }

    public void setRotation(float rotation) {
        this.rotationAngle = rotation;
        invalidate();
    }
}