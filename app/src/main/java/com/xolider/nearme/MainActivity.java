package com.xolider.nearme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xolider.nearme.view.TextViewWithDrawable;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_FINE_LOCATION = 0;

    private Button mConnect;
    private Button mSignin;

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.settings_main:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
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
        final TextViewWithDrawable mText = (TextViewWithDrawable)findViewById(R.id.txtDrawable);
        mText.setVisibility(View.VISIBLE);
        mText.setAlpha(0);
        mText.setY(mText.getY()-100);
        mText.animate()
                .setDuration(1000)
                .y(100)
                .alpha(1);
        new Thread(new Runnable() {

            int lap = 2;

            @Override
            public void run() {
                while(lap != 0) {
                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lap--;
                }
                mText.animate().setDuration(1000).alpha(0);
            }
        }).start();
        SignInActivity.isCreated = false;
    }
}
