package com.xolider.nearme;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xolider.nearme.utils.SigninRequest;
import com.xolider.nearme.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SignInActivity extends AppCompatActivity {

    public static boolean isCreated = false;

    private EditText mUsernaame;
    private EditText mPass;
    private EditText mName;
    private EditText mEmail;
    private Button mSignin;

    private TextView mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUsernaame = (EditText)findViewById(R.id.username_s);
        mPass = (EditText)findViewById(R.id.password_s);
        mName = (EditText)findViewById(R.id.name_s);
        mEmail = (EditText)findViewById(R.id.email_s);
        mSignin = (Button)findViewById(R.id.button_s);

        mError = (TextView)findViewById(R.id.error_empty);

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(mUsernaame.getText().toString().isEmpty() && mPass.getText().toString().isEmpty() && mEmail.getText().toString().isEmpty() && mName.getText().toString().isEmpty())) {
                    new SigninRequest(SignInActivity.this).execute(mUsernaame.getText().toString(), mPass.getText().toString(), mEmail.getText().toString(), mName.getText().toString());
                }
                else {
                    mError.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
