package com.saganet.encuestas;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MenuEncuestasContent extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    public static final String EXTRA_MENU_ENCUESTA_ID = "extra_menu_encuestas_id";
    private String provider;
    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;
    public static final int REQUEST_LOGING_RELOAD = 9;
    private LocationManager locationManager;
    private LocationRequest locRequest;
    private GoogleApiClient apiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_encuestas_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id = getIntent().getStringExtra(com.saganet.encuestas.Menu.EXTRA_MENU_ID);
        apiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        enableLocationUpdates();
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //BestLocationEnabled();
        MenuEncuestasFragment fragment = (MenuEncuestasFragment)
                getSupportFragmentManager().findFragmentById(R.id.content_menu_encuestas_content);
        if (fragment == null) {
            fragment = MenuEncuestasFragment.newInstance(id, this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_menu_encuestas_content, fragment)
                    .commit();
        }
    }
    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setFastestInterval(1000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(LOGTAG, "Configuración correcta");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
                            status.startResolutionForResult(MenuEncuestasContent.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            //btnActualizar.setChecked(false);
                            Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");
                        //btnActualizar.setChecked(false);
                        break;
                }
            }
        });
    }
    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(MenuEncuestasContent.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.
            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,MenuEncuestasContent.this);
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);
            updateUI(lastLocation);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }
    private void updateUI(Location loc) {
        if (loc != null) {
            DataUpload.VALUE_LATITUDE_POSITION = String.valueOf(loc.getLongitude());
            DataUpload.VALUE_LONGITUDE_POSITION = String.valueOf(loc.getLatitude());
            Log.v("VALUE_LATITUDE_POS",DataUpload.VALUE_LATITUDE_POSITION);
            Log.v("VALUE_LONGITUDE_POS",DataUpload.VALUE_LONGITUDE_POSITION);
        } else {
            DataUpload.VALUE_LATITUDE_POSITION = "0.0";
            DataUpload.VALUE_LONGITUDE_POSITION = "0.0";
            Log.v("VALUE_LATITUDE_POS",DataUpload.VALUE_LATITUDE_POSITION);
            Log.v("VALUE_LONGITUDE_POS",DataUpload.VALUE_LONGITUDE_POSITION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);
                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }
    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    switch (requestCode) {
    //        case PETICION_CONFIG_UBICACION:
    //            switch (resultCode) {
    //                case Activity.RESULT_OK:
    //                    startLocationUpdates();
    //                    break;
    //                case Activity.RESULT_CANCELED:
    //                    Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");
    //                    break;
    //            }
    //            break;
    //    }
    //}
    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOGTAG, "Recibida nueva ubicación!");
        //Mostramos la nueva ubicación recibida
        updateUI(location);
    }

    private void BestLocationEnabled() {
        if (!checkLocation())
            return;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 1 * 60 * 1000, 5, locationListenerBest);
            //Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
        }
    }
    private void BestLocationUnEnabled() {
        locationManager.removeUpdates(locationListenerBest);
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Encender localización")
                .setMessage("Tu configuración de posicionamiento esta 'Apagado'.\nPor favor encienda la localización " +
                        "para poder continuar")
                .setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            DataUpload.VALUE_LATITUDE_POSITION = String.valueOf(location.getLongitude());
            DataUpload.VALUE_LONGITUDE_POSITION = String.valueOf(location.getLatitude());
            Log.v("LATITUDE_POSITION",DataUpload.VALUE_LATITUDE_POSITION);
            Log.v("LONGITUDE_POSITION",DataUpload.VALUE_LONGITUDE_POSITION);
            Toast.makeText(MenuEncuestasContent.this, "LATITUDE_POSITION "+ DataUpload.VALUE_LATITUDE_POSITION , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu n)
    {
        getMenuInflater().inflate(R.menu.menu_encuestas_realizadas, n);
        return  super.onCreateOptionsMenu(n);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
