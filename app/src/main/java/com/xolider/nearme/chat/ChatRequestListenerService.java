package com.xolider.nearme.chat;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.xolider.nearme.NotificationActivity;
import com.xolider.nearme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ChatRequestListenerService extends IntentService {

    public ChatRequestListenerService() {
        super("ChatRequestListenerService");
    }

    @Override
    public void onHandleIntent(Intent i) {
        check(i.getStringExtra("user"));
    }

    private void notifyRequest(String user, int id) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, NotificationActivity.class);
        i.putExtra("user", user);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(user + " " + getResources().getString(R.string.get_request))
                .setSmallIcon(R.drawable.ic_map_marker_radius_white_48dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLights(Color.argb(0, 255, 0, 153), 2000, 5000)
                .setVibrate(new long[] {1000, 1000}).build();
        notificationManager.notify(id, notification);
        Log.i("notif", "Notifiaction sent !");
    }

    private void check(final String user) {
        String line = null;
        try {
            URL u = new URL("http://192.168.1.199/NearMe/check_for_request.php?user=" + user);
            HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
            if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                line = bufferedReader.readLine();
                Log.i("line", line);
                bufferedReader.close();
            }
        }
        catch (IOException e) {
            Log.v("try1", e.getMessage());
        }
        try {
            if(line != null && !line.isEmpty()) {
                JSONArray jsonArray = new JSONArray(line);
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String usr = object.getString("user");
                    notifyRequest(usr, i);
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
