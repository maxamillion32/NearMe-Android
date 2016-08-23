package com.xolider.nearme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.xolider.nearme.utils.Session;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImg;
    private ProgressBar mPb;

    public static final int GET_PHOTO_FOR_CHANGE_PROFILE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.profile) + " - " + Session.name);

        mProfileImg = (ImageView)findViewById(R.id.profil_img);

        Bitmap bit = Session.imgUser;

        if(bit == BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_black_24dp)) {
            mProfileImg.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
        else {
            mProfileImg.setImageBitmap(bit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.take_photo:

                break;
            case R.id.change_photo:
                pickImg();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void pickImg() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, GET_PHOTO_FOR_CHANGE_PROFILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_PHOTO_FOR_CHANGE_PROFILE:
                if(resultCode == Activity.RESULT_OK) {
                    if(data != null) {
                        try {
                            InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                            final Bitmap b = BitmapFactory.decodeStream(inputStream);
                            mPb = new ProgressBar(this);
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mProfileImg.getLayoutParams();
                            mProfileImg.setVisibility(View.INVISIBLE);
                            mPb.setIndeterminate(true);
                            addContentView(mPb, layoutParams);
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                public Void doInBackground(Void... voids) {
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                    String encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

                                    nameValuePairs.add(new BasicNameValuePair("encoded", encoded));
                                    nameValuePairs.add(new BasicNameValuePair("name", Session.name));

                                    HttpClient client = new DefaultHttpClient();
                                    HttpPost httpPost = new HttpPost("http://192.168.1.199/NearMe/upload_img.php");
                                    try {
                                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                                        client.execute(httpPost);
                                        Intent i = new Intent(ProfileActivity.this, Connection.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        return;
                    }
                }
                break;
        }
    }
}
