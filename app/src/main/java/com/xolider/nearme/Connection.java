package com.xolider.nearme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xolider.nearme.utils.User;
import com.xolider.nearme.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

import javax.net.ssl.HttpsURLConnection;

public class Connection extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPass;
    private Button mConnect;

    private TextView mIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mUsername = (EditText)findViewById(R.id.username_c);
        mPass = (EditText)findViewById(R.id.password_c);
        mConnect = (Button)findViewById(R.id.button_c);

        mIncorrect = (TextView)findViewById(R.id.incorrect_login);

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnect.setText(Connection.this.getResources().getString(R.string.logging_in));
                new AsyncTask<String, Void, String>() {

                    @Override
                    public String doInBackground(String... params) {
                        String username = params[0];
                        String pass = params[1];
                        try {
                            URL u = new URL("http://192.168.1.199/NearMe/connect.php?user=" + username + "&pass=" + pass);
                            HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
                            if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                String line = bf.readLine();
                                bf.close();
                                urlConnection.disconnect();
                                return line;
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public void onPostExecute(String str) {
                       if(str != null && !str.equalsIgnoreCase("error")) {
                           try {
                               JSONObject j = new JSONObject(str);
                               User u = new User(j.getString("user"), j.getString("img"));
                               Intent intent = new Intent(Connection.this, LoadingPositionActivity.class);
                               intent.putExtra("username", u.getUsername());
                               intent.putExtra("img", u.getImageProfile());
                               startActivity(intent);
                               finish();
                           }
                           catch (JSONException e) {
                               e.printStackTrace();
                           }
                           catch (MalformedURLException e1) {
                               e1.printStackTrace();
                           }
                       }
                        else {
                           mIncorrect.setVisibility(View.VISIBLE);
                           mConnect.setText(Connection.this.getResources().getString(R.string.button_connection));
                       }
                    }

                }.execute(mUsername.getText().toString(), mPass.getText().toString());
            }
        });
    }
}
