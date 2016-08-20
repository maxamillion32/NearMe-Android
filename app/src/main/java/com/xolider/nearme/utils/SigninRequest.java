package com.xolider.nearme.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.xolider.nearme.SignInActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Clément on 20/08/2016.
 */
public class SigninRequest extends AsyncTask<String, Void, Void> {

    private Activity a;

    public SigninRequest(Activity a) {
        this.a = a;
    }

    @Override
    public Void doInBackground(String... params) {
        String username = params[0];
        String pass = params[1];
        String email = params[2];
        String name = params[3];

        try {
            URL url = new URL(Utils.URL_SIGNIN);
            HttpURLConnection c = (HttpURLConnection)url.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("User-Agent", "Mozilla/5.0");
            c.setRequestProperty("Accept-Language", "en-US");
            String parameters = "user=" + username + "&pass=" + pass + "&email=" + email + "&name=" + name;
            c.connect();
            DataOutputStream wr = new DataOutputStream(c.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close();

            SignInActivity.isCreated = true;
            a.finish();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
