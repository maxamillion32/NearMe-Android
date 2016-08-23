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
import android.widget.TextView;

import com.xolider.nearme.R;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Clément on 22/08/2016.
 */
public class PeopleAdapter extends BaseAdapter {

    private HashMap<String, String> people;
    private LayoutInflater layoutInflater;
    private Activity c;

    public PeopleAdapter(HashMap<String, String> people, Activity c) {
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
        final TextView title = (TextView)convertView.findViewById(R.id.title_user);
        final String user = (String)people.keySet().toArray()[pos];
        title.setText(user);
        final ImageView imageView = (ImageView)convertView.findViewById(R.id.image_user);

        new Thread(new Runnable() {
            @Override
            public void run() {
                c.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(getImage(people.get(user)));
                    }
                });
            }
        }).start();

        return  convertView;
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
            return BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_account_circle_black_24dp);
        }
    }
}
