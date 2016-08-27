package com.xolider.nearme.chat;

import android.content.Intent;
import android.os.AsyncTask;

import com.xolider.nearme.utils.Session;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Clément on 23/08/2016.
 */
public class ChatRequest {

    private String mUsername;

    public ChatRequest(String username) {
        this.mUsername = username;
    }

    /**
     *
     * @return if request has been succefully sent
     */

    public boolean sendRequest() {
        AsyncTask<String, Void, Boolean> asyncTask = new AsyncTask<String, Void, Boolean>() {

            @Override
            public Boolean doInBackground(String... str) {
                try {
                    URL url = new URL("http://192.168.1.199/NearMe/send_request.php?user=" + Session.name + "&to=" + mUsername);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        return true;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        };
        boolean b = false;
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
}