package com.xolider.nearme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.Os;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xolider.nearme.view.TextViewWithDrawable;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_FINE_LOCATION = 0;

    private Button mConnect;
    private Button mSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestForPermsIfNeeded();

        mConnect = (Button)findViewById(R.id.main_connect);
        mSignin = (Button)findViewById(R.id.main_signin);

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToConnect();
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSignIgn();
            }
        });
    }

    public void requestForPermsIfNeeded() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(SignInActivity.isCreated) {
            notifAccountCreated();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    Toast.makeText(this, getResources().getString(R.string.app_cannot_run_without_location), Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean dataFoldersExists() {
        return this.getExternalFilesDir(null).exists() && this.getFilesDir().exists();
    }

    public void switchToSignIgn() {
        Intent i = new Intent(this, SignInActivity.class);
        startActivity(i);
    }

    public void switchToConnect() {
        Intent i = new Intent(this, Connection.class);
        startActivity(i);
    }

    public void notifAccountCreated() {
        TextViewWithDrawable mText = (TextViewWithDrawable)findViewById(R.id.txtDrawable);
        mText.setVisibility(View.VISIBLE);
        mText.setY(mText.getY()-100);
        mText.animate()
                .setDuration(3000)
                .y(100);
    }
}
