package com.xolider.nearme;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xolider.nearme.utils.Session;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoadingPositionActivity extends AppCompatActivity {

    private boolean mustReloadPosition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_position);

        locate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mustReloadPosition) {
            locate();
            mustReloadPosition = false;
        }
    }

    public void locate() {
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.warning)).setMessage(getResources().getString(R.string.gps_disabled)).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    mustReloadPosition = true;
                }
            }).setCancelable(false).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    finish();
                }
            }).create().show();
        }
        else {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setCostAllowed(true);
            String provider = locationManager.getBestProvider(criteria, true);
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(provider, 0, 500, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        new AsyncTask<Location, Void, Void>() {

                            @Override
                            public Void doInBackground(Location... l) {
                                Location loc = l[0];

                                try {
                                    URL u = new URL("http://192.168.1.199/NearMe/update_loc.php?user=" + getIntent().getStringExtra("user") + "&long=" + loc.getLongitude() + "&lat=" + loc.getLatitude());
                                    HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
                                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                        urlConnection.disconnect();
                                    }
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                        }.execute(location);

                        Session.loc = location;

                        Intent intent = new Intent(LoadingPositionActivity.this, BodyNearMe.class);
                        startActivity(intent);
                        finish();
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
                });
            }
        }
    }
}
