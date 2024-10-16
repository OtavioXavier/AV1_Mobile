package com.example.avaliaon1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.GnssStatus;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SatelliteSignalView extends RecyclerView {

    private GnssStatus status;
    private SatelliteSignalAdapter adapter;
    private String filteredConstellations = "All";
    private boolean filterUsedInFix = false;
    private static final int FLAG_WIDTH = 40;
    private static final int FLAG_HEIGHT = 30;

    private Paint chartPaint = new Paint();
    private Paint signalPaint = new Paint();
    private Paint signalBgPaint = new Paint();
    private Paint signalBorderPaint = new Paint();
    private Paint backgroundPaint = new Paint();
    private Paint borderPaint = new Paint();

    public SatelliteSignalView(Context context) {
        super(context);
        init(null, 0);
    }

    public SatelliteSignalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SatelliteSignalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(layoutManager);
        adapter = new SatelliteSignalAdapter(null);
        setAdapter(adapter);

        backgroundPaint.setColor(Color.rgb(0, 0, 156));
        backgroundPaint.setAntiAlias(true);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(12);
        borderPaint.setColor(Color.rgb(218, 165, 32));
        borderPaint.setAntiAlias(true);


        signalPaint.setTextAlign(Paint.Align.LEFT);
        signalPaint.setTextSize(30);
        signalPaint.setColor(Color.WHITE);
        signalPaint.setAntiAlias(true);

        signalBorderPaint.setStyle(Paint.Style.STROKE);
        signalBorderPaint.setStyle(Paint.Style.STROKE);
        signalBorderPaint.setStrokeWidth(5); // Largura do contorno
        signalBorderPaint.setColor(Color.WHITE); // Cor do contorno
        signalBorderPaint.setAntiAlias(true);
        signalBorderPaint.setTextAlign(Paint.Align.CENTER);

        signalBgPaint.setColor(Color.rgb(46, 46, 46));
        signalBgPaint.setStyle(Paint.Style.FILL);
        signalBgPaint.setAntiAlias(true);

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), borderPaint);

    }


    private String formatId(int id) {
        return id < 10 ? "0" + id : "" +id;
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

    public void setStatus(GnssStatus status) {
        this.status = status; // Armazena o status atual
        if (adapter != null) {
            adapter.setStatus(status); // Atualiza o adaptador com o novo status
            invalidate();
        }
    }


    public void setFilters(String filteredConstellations, boolean filterUsedInFix) {
        if (adapter != null) {
            adapter.setFilters(filteredConstellations, filterUsedInFix);
            invalidate();
        }
    }

}