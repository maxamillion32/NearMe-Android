package com.xolider.nearme.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.xolider.nearme.SignInActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Clément on 20/08/2016.
 */
public class SigninRequest extends AsyncTask<String, Void, Boolean> {

    private SignInActivity a;

    public SigninRequest(SignInActivity a) {
        this.a = a;
    }

    @Override
    public Boolean doInBackground(String... params) {
        String username = params[0];
        String pass = params[1];
        String email = params[2];
        String name = params[3];

        try {
            URL u = new URL("http://192.168.1.199/NearMe/signin.php?user=" + username + "&pass=" + pass + "&email=" + email + "&name=" + name);
            HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
            if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = bf.readLine();
                bf.close();
                if(line != null && line.equalsIgnoreCase("error")) {
                    return false;
                }
            }

            SignInActivity.isCreated = true;
            a.finish();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
