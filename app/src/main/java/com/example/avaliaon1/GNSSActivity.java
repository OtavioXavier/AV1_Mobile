package com.example.avaliaon1;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GNSSActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    private LocationProvider locationProvider;
    private static final int REQUEST_LOCATION = 1;

    private RotationSensorHandler rotationSensorHandler;

    CoordinatesView coordinatesView;
    CompassView compassView;
    SatellitesMapView satellitesMapView;

    private int choiceCoordinate = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssactivity);

        coordinatesView = (CoordinatesView) findViewById(R.id.coordinates);
        compassView = (CompassView) findViewById(R.id.compass);
        satellitesMapView = (SatellitesMapView) findViewById(R.id.satellites_map);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Verifique se locationManager não é null
        if (locationManager == null) {
            Toast.makeText(this, "Erro ao inicializar LocationManager", Toast.LENGTH_SHORT).show();
            return;
        }


        checkPermissions();
        getLocation();



        rotationSensorHandler = new RotationSensorHandler(this, this);
        rotationSensorHandler.startListening();

        coordinatesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoordinateTypes[] choices = CoordinateTypes.values();
                String[] choiceStrings = new String[choices.length];
                for (int i = 0; i < choices.length; i++) {
                    choiceStrings[i] = choices[i].toString();
                }

                AlertDialog.Builder switchBox = new AlertDialog.Builder(GNSSActivity.this);
                switchBox.setTitle("Switch coordinate type")
                        .setIcon(R.drawable.switch_box_icon)
                        .setSingleChoiceItems(choiceStrings, choiceCoordinate, (dialog, which) -> {
                            choiceCoordinate = which;
                        })
                        .setPositiveButton("Salvar", (dialog, which) -> {
                            choiceCoordinate = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            for (CoordinateTypes type : CoordinateTypes.values()) {
                                if (choices[choiceCoordinate].equals(type)) {
                                    coordinatesView.setType(type);
                                    break;
                                }
                            }
                            Toast.makeText(GNSSActivity.this, "Tipo salvo: " + choiceStrings[choiceCoordinate], Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            Toast.makeText(GNSSActivity.this, "Cancelado!", Toast.LENGTH_SHORT).show();
                        });
                switchBox.show();

            }
        });

    }

    public void updateAzimuth(float azimuth) {
        compassView.setDegree(azimuth);
    }


    @SuppressLint("MissingPermission")
    public void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            authorize_provider();
        }
    }

    public void authorize_provider() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            startListeningUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    public void startListeningUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(locationProvider.getName(), 1000, 0.1f, this);
        locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                satellitesMapView.setStatus(status);
            }
        });
    }

        @Override
    public void onLocationChanged(@NonNull Location location) {
        coordinatesView.setCoordinates(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
