package com.xolider.nearme.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xolider.nearme.R;
import com.xolider.nearme.chat.ChatRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Clément on 22/08/2016.
 */
public class PeopleAdapter extends BaseAdapter {

    private ArrayList<String> people;
    private LayoutInflater layoutInflater;
    private Activity c;

    public PeopleAdapter(ArrayList<String> people, Activity c) {
        this.people = people;
        this.c = c;
        layoutInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public Object getItem(int pos) {
        return null;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.people_layout, null);
        }

        ImageView chat = (ImageView)convertView.findViewById(R.id.chat_request);
        final TextView title = (TextView)convertView.findViewById(R.id.title_user);
        final String user = people.get(pos);
        title.setText(user);
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.image_user);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChatRequest(user);
                Toast.makeText(c, c.getResources().getString(R.string.chat_request_sent), Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(getImage(user));
                    }
                });
            }
        }).start();

        return  convertView;
    }

    public Bitmap getImage(final String user) {
            AsyncTask<Void, Void, Bitmap> asyncTask = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    try {
                        URL url = new URL("http://192.168.1.199/NearMe/get_image.php?user=" + user + "&width=" + c.getWindowManager().getDefaultDisplay().getWidth());
                        final URLConnection urlConnection = url.openConnection();
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

    public void sendChatRequest(final String user) {
        new ChatRequest(user).sendRequest();
    }
}
