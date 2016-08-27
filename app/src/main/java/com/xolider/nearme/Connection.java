package com.xolider.nearme;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xolider.nearme.chat.ChatRequestListenerService;
import com.xolider.nearme.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class Connection extends AppCompatActivity {

    private TextInputEditText mUsername;
    private TextInputEditText mPass;
    private Button mConnect;

    private TextView mIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mUsername = (TextInputEditText) findViewById(R.id.username_c);
        mPass = (TextInputEditText) findViewById(R.id.password_c);
        mConnect = (Button)findViewById(R.id.button_c);

        mIncorrect = (TextView)findViewById(R.id.incorrect_login);

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnect.setText(Connection.this.getResources().getString(R.string.logging_in));
                mConnect.setEnabled(false);
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
                               Session.pass = mPass.getText().toString();
                               Bitmap b = getImage();
                               Session.imgUser = b;
                               if(Session.loc == null) {
                                   Intent intent = new Intent(Connection.this, LoadingPositionActivity.class);
                                   startActivity(intent);
                                   createFileLogin(Session.name, Session.pass);
                                   new Timer().schedule(new TimerTask() {
                                       @Override
                                       public void run() {
                                           Intent intent1 = new Intent(Connection.this, ChatRequestListenerService.class);
                                           intent1.putExtra("user", Session.name);
                                           startService(intent1);
                                       }
                                   }, 0, 5000);
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
                           mConnect.setEnabled(true);
                       }
                    }

                }.execute(mUsername.getText().toString(), mPass.getText().toString());
            }
        });
    }

    public Bitmap getImage() {
            AsyncTask<Void, Void, Bitmap> asyncTask = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        URL url = new URL("http://192.168.1.199/NearMe/get_image.php?user=" + Session.name + "&width=" + getWindowManager().getDefaultDisplay().getWidth());
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
        if(b == null) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_black_24dp);
        }
            return b;
    }

    public void createFileLogin(String user, String pass) {
        if(!getExternalFilesDir(null).exists()) getExternalFilesDir(null).mkdirs();
        File f = new File(getExternalFilesDir(null), "login.dat");
        if(!f.exists()) {
            try {
                f.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fw = new FileWriter(f);
            fw.write("");
            fw.append(user + "\n");
            fw.append(pass + "\n");
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
