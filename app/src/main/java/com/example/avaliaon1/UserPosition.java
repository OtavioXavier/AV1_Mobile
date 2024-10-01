package com.example.avaliaon1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class UserPosition extends View {
    private String mLongitude;
    private String mLatitude;
    private String mVelocidade;
    private float mTextDimension = 0;
    private Drawable mExampleDrawable;
    private int mExampleColor = Color.BLACK;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private float mLineSpacing = 40f;

    public UserPosition(Context context) {
        super(context);
        init(null, 0);
    }

    public UserPosition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public UserPosition(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.UserPosition, defStyle, 0);

        mLatitude = a.getString(R.styleable.UserPosition_Latitude);
        mLongitude = a.getString(R.styleable.UserPosition_Longitude);
        mVelocidade = a.getString(R.styleable.UserPosition_Velocidade);

        mExampleColor = a.getColor(R.styleable.UserPosition_exampleColor, mExampleColor);
        mTextDimension = a.getDimension(R.styleable.UserPosition_exampleDimension, mTextDimension);

        if (a.hasValue(R.styleable.UserPosition_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(R.styleable.UserPosition_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Configura o TextPaint padrão
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Atualiza as medidas do texto
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextDimension);
        mTextPaint.setColor(mExampleColor);
        String displayText = getDisplayText();
        mTextWidth = mTextPaint.measureText(displayText);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    private String getDisplayText() {
        String texto = "Lat: " + mLatitude + "\nLong: " + mLongitude + "\nVelocidade: " + mVelocidade;
        return texto;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Desenha a borda
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
        canvas.drawRect(paddingLeft, paddingTop, getWidth() - paddingRight, getHeight() - paddingBottom, borderPaint);

        // Desenha o texto.
        String[] lines = getDisplayText().split("\n");
        for (int i = 0; i < lines.length; i++) {
            canvas.drawText(lines[i],
                    paddingLeft + (contentWidth - mTextWidth) / 2,
                    paddingTop + (contentHeight / 2) + (i * (mTextHeight + mLineSpacing)), // Altera para adicionar espaçamento
                    mTextPaint);
        }

        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }


    // Getters e Setters para Latitude, Longitude e Velocidade
    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
        invalidateTextPaintAndMeasurements();
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        this.mLatitude = latitude;
        invalidateTextPaintAndMeasurements();
    }

    public String getVelocidade() {
        return mVelocidade;
    }

    public void setVelocidade(String velocidade) {
        this.mVelocidade = velocidade;
        invalidateTextPaintAndMeasurements();
    }

    // Getters e Setters para os outros atributos
    public int getExampleColor() {
        return mExampleColor;
    }

    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    public float getTextDimension() {
        return mTextDimension;
    }

    public void setTextDimension(float textDimension) {
        mTextDimension = textDimension;
        invalidateTextPaintAndMeasurements();
    }

    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
