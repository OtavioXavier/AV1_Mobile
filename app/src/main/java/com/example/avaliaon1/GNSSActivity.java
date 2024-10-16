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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

public class GNSSActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    private LocationProvider locationProvider;
    private RotationSensorHandler rotationSensorHandler;

    CoordinatesView coordinatesView;
    CompassView compassView;
    SatellitesMapView satellitesMapView;

    SatelliteSignalView satelliteSignalView;

    private int choiceCoordinate = 0;
    private String selectedConstellation = "All";
    private boolean filterUsedInFix = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssactivity);

        coordinatesView = (CoordinatesView) findViewById(R.id.coordinates);
        compassView = (CompassView) findViewById(R.id.compass);
        satellitesMapView = (SatellitesMapView) findViewById(R.id.satellites_map);
        satelliteSignalView = (SatelliteSignalView) findViewById(R.id.satellite_signal_view);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Verifique se locationManager não é null
        if (locationManager == null) {
            Toast.makeText(this, R.string.erro_ao_inicializar_locationmanager, Toast.LENGTH_SHORT).show();
            return;
        }

        checkPermissions();
        getLocation();

        rotationSensorHandler = new RotationSensorHandler(this, this);
        rotationSensorHandler.startListening();

        satellitesMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSatellitesFilter();
            }
        });

        coordinatesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSwitcherCoordinate();
            }
        });

    }

    public void showSatellitesFilter() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_satellite_filter, null);

        RadioGroup constellationGroup = dialogView.findViewById(R.id.constellation_group);
        CheckBox usedInFixCheckBox = dialogView.findViewById(R.id.used_in_fix);
        usedInFixCheckBox.setChecked(filterUsedInFix);

        switch (selectedConstellation) {
            case "GPS":
                constellationGroup.check(R.id.gps);
                break;
            case "Galileo":
                constellationGroup.check(R.id.galileo);
                break;
            case "Glonass":
                constellationGroup.check(R.id.glonass);
                break;
            case "Beidou":
                constellationGroup.check(R.id.beidou);
                break;
            case "QZSS":
                constellationGroup.check(R.id.qzss);
                break;
            default:
                constellationGroup.check(R.id.all_constellations);
        }

        usedInFixCheckBox.setChecked(filterUsedInFix);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.filtrar_sat_lites)
                .setView(dialogView)
                .setPositiveButton(R.string.aplicar, (dialog, which) -> {
                    // Update filter variables
                    int selectedId = constellationGroup.getCheckedRadioButtonId();
                    if (selectedId == R.id.gps) {
                        selectedConstellation = "GPS";
                    } else if (selectedId == R.id.galileo) {
                        selectedConstellation = "Galileo";
                    } else if (selectedId == R.id.glonass) {
                        selectedConstellation = "Glonass";
                    } else if (selectedId == R.id.beidou) {
                        selectedConstellation = "Beidou";
                    } else if (selectedId == R.id.qzss) {
                        selectedConstellation = "QZSS";
                    } else {
                        selectedConstellation = "All";
                    }

                    filterUsedInFix = usedInFixCheckBox.isChecked();
                    satellitesMapView.setFilters(selectedConstellation, filterUsedInFix);
                    satelliteSignalView.setFilters(selectedConstellation, filterUsedInFix);
                })
                .setNegativeButton(R.string.cancelar, (dialog, which) -> {
                    // Revert selection if needed
                }).show();
    }

    public void showSwitcherCoordinate() {
        CoordinateTypes[] choices = CoordinateTypes.values();
        String[] choiceStrings = new String[choices.length];
        for (int i = 0; i < choices.length; i++) {
            choiceStrings[i] = choices[i].toString();
        }

        AlertDialog.Builder switchBox = new AlertDialog.Builder(GNSSActivity.this);
        switchBox.setTitle(R.string.switch_coordinate_type).setIcon(R.drawable.switch_box_icon).setSingleChoiceItems(choiceStrings, choiceCoordinate, (dialog, which) -> {
            choiceCoordinate = which;
        }).setPositiveButton(R.string.salvar, (dialog, which) -> {
            choiceCoordinate = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            for (CoordinateTypes type : CoordinateTypes.values()) {
                if (choices[choiceCoordinate].equals(type)) {
                    coordinatesView.setType(type);
                    break;
                }
            }
            Toast.makeText(GNSSActivity.this, getString(R.string.tipo_salvo) + choiceStrings[choiceCoordinate], Toast.LENGTH_SHORT).show();
        }).setNegativeButton(R.string.cancelar, (dialog, which) -> {
            Toast.makeText(GNSSActivity.this, R.string.cancelado, Toast.LENGTH_SHORT).show();
        });
        switchBox.show();
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
                satelliteSignalView.setStatus(status);
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        coordinatesView.setCoordinates(location.getLatitude(), location.getLongitude());
    }

    public void updateAzimuth(float azimuth) {
        compassView.setDegree(azimuth);
        satellitesMapView.setRotation(azimuth);
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
