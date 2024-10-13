package com.example.avaliaon1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class RotationSensorHandler implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private GNSSActivity activity;

    public RotationSensorHandler(Context context, GNSSActivity activity) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.activity = activity;
    }

    public void startListening() {
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rotationMatrix);

            float[] orientationAngles = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float azimuth = (float) Math.toDegrees(orientationAngles[0]);
            azimuth = (azimuth + 360) % 360;
            activity.updateAzimuth(azimuth + 45);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
