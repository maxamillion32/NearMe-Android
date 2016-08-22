package com.xolider.nearme;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;
import com.xolider.nearme.utils.Session;
import com.xolider.nearme.view.TextViewWithDrawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class BodyNearMe extends AppCompatActivity {

    private TextView mLoc;
    private AdView mAd;

    public static final int MAX_LIKES = 15;

    public static final String APP_ID = "appabda0cbde6d5473ab7";
    public static final String ZONE_ID = "vzdabb8e356bbc40ffa9";

    private int like = getLikes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_near_me);

        mLoc = (TextView)findViewById(R.id.loc_text);

        updateStatus();

        refreshLikes(false, 0);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-7165489802020798~9755971466");

        mAd = (AdView)findViewById(R.id.nearme_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAd.loadAd(adRequest);

        AdColony.configure(this, "version:1.0,store:google", APP_ID, ZONE_ID);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nearme_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.profil_menu_btn:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.disable_ad_btn:
                if(canWatch() ) {
                    if(like <= 14) {
                        AdColonyVideoAd ad = new AdColonyVideoAd(ZONE_ID).withListener(new AdColonyAdListener() {
                            @Override
                            public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
                                refreshLikes(true, 1);
                                startTimer();
                            }

                            @Override
                            public void onAdColonyAdStarted(AdColonyAd adColonyAd) {

                            }
                        });
                        ad.show();
                    }
                    else {
                        Toast.makeText(this, getResources().getString(R.string.max_like), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, getResources().getString(R.string.does_wait), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void updateStatus() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            public Void doInBackground(Void... params) {
                try {
                    URL u = new URL("http://192.168.1.199/NearMe/update_status.php?user=" + Session.name + "&status=online");
                    HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        urlConnection.disconnect();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAd.resume();
        AdColony.resume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAd.pause();
        AdColony.pause();
    }

    public void startTimer() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            public Void doInBackground(Void... voids) {
               try {
                   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                   Date d = new Date();
                   String date = simpleDateFormat.format(d);
                   URL url = new URL("http://192.168.1.199/NearMe/update_timer.php?user=" + Session.name + "&timer=" + date);
                   HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                   if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                       urlConnection.disconnect();
                   }
               }
               catch (IOException e) {
                   e.printStackTrace();
               }
                return null;
            }

        }.execute();
    }

    public boolean canWatch() {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            public Boolean doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://192.168.1.199/NearMe/get_timer.php?user=" + Session.name);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        BufferedReader bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String line = bf.readLine();
                        bf.close();
                        if(line != null) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                            Date d = simpleDateFormat.parse(line);
                            long diff = getDiff(d, new Date(), TimeUnit.MINUTES);
                            if(diff >= 20) {
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                        else {
                            return true;
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                catch (ParseException e1) {
                    e1.printStackTrace();
                }
                return false;
            }
        };
        boolean can = false;
        try {
            can = asyncTask.execute().get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return can;
    }

    public long getDiff(Date d1, Date d2, TimeUnit timeUnit) {
        long diff = d2.getTime() - d1.getTime();
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void refreshLikes(boolean mustChangeLikes, int how) {
        if(mustChangeLikes) {
            like += how;
        }
        mLoc.setText(getResources().getString(R.string.like_rest) + " " + like);
        if(like <= 3) {
            mLoc.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        else {
            mLoc.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            public Void doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://192.168.1.199/NearMe/update_likes.php?user=" + Session.name + "&likes=" + like);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        urlConnection.disconnect();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public int getLikes() {
        AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://192.168.1.199/NearMe/get_likes.php?user=" + Session.name);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String l = bufferedReader.readLine();
                        int lk = Integer.parseInt(l);
                        bufferedReader.close();
                        return lk;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        int l = -1;
        try {
            l = asyncTask.execute().get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        return l;
    }
}
