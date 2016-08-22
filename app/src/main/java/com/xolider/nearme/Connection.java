package com.xolider.nearme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xolider.nearme.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

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
                               Session.name = j.getString("user");
                               Bitmap b = getImage(j.getString("img"));
                               Session.imgUser = b;
                               if(Session.loc == null) {
                                   Intent intent = new Intent(Connection.this, LoadingPositionActivity.class);
                                   startActivity(intent);
                                   finish();
                                   MainActivity.instance.finish();
                               }
                               else {
                                   Intent i = new Intent(Connection.this, BodyNearMe.class);
                                   startActivity(i);
                                   finish();
                               }
                           }
                           catch (JSONException e) {
                               e.printStackTrace();
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

    public Bitmap getImage(final String imgUrl) {
        if(imgUrl != null && !imgUrl.isEmpty()) {
            AsyncTask<Void, Void, Bitmap> asyncTask = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        URL url = new URL(imgUrl);
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.connect();
                        Bitmap b = BitmapFactory.decodeStream(urlConnection.getInputStream());
                        return b;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            Bitmap b = null;
            try {
                b = asyncTask.execute().get();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return b;
        }
        else {
            return BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_black_24dp);
        }
    }
}
