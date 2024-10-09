package com.example.avaliaon1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class GNSSActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private PositionUser positionUser;
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private static final int AZIMUTH_HISTORY_SIZE = 10;
    private final List<Float> azimuthHistory = new ArrayList<>();
    private int escolhaCoordenadas = 0;
    String tipoCoordenadas = "Graus [+/-DDD.DDDDD]";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssactivity);

        positionUser = findViewById(R.id.positionUser);
        positionUser.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        escolhaCoordenadas = getIndexOfTipo(tipoCoordenadas);

        requestLocationPermission();
    }

    private int getIndexOfTipo(String tipo) {
        String[] escolhas = {"Graus [+/-DDD.DDDDD]", "Graus-Minutos [+/-DDD:MM.MMMMM]", "Graus-Minutos-Segundos [+/-DDD:MM:SS.SSSSS]"};
        for (int i = 0; i < escolhas.length; i++) {
            if (escolhas[i].equals(tipo)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);

            float[] orientationAngles = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            float azimuth = (float) Math.toDegrees(orientationAngles[0]);

            azimuthHistory.add(azimuth);
            if (azimuthHistory.size() > AZIMUTH_HISTORY_SIZE) {
                azimuthHistory.remove(0);
            }

            float smoothedAzimuth = 0;
            for (float a : azimuthHistory) {
                smoothedAzimuth += a;
            }
            smoothedAzimuth /= azimuthHistory.size();

            float roundedAzimuth = Math.round(smoothedAzimuth);

            positionUser.setRotate(roundedAzimuth);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.positionUser) {
          String[] escolhasCoordenadas = {"Graus [+/-DDD.DDDDD]", "Graus-Minutos [+/-DDD:MM.MMMMM]", "Graus-Minutos-Segundos [+/-DDD:MM:SS.SSSSS]"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder
                    .setTitle("Trocar tipo de coordenada:")
                    .setPositiveButton("Salvar", (dialog, which) -> {
                        tipoCoordenadas = escolhasCoordenadas[which];
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        Toast.makeText(this, "Cancelado!", Toast.LENGTH_SHORT).show();

                    })
                    .setSingleChoiceItems(escolhasCoordenadas, escolhaCoordenadas, (dialog, which) -> {
                        escolhaCoordenadas = which;
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Seção de Localização ->

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Sem permissão para acessar o sistema de posicionamento", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            positionUser.setPosition(tipoCoordenadas, location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(@NonNull String provider) {}

        @Override
        public void onProviderDisabled(@NonNull String provider) {}
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
