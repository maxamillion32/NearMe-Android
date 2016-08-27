package com.xolider.nearme.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xolider.nearme.chat.ChatRequestListenerService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ChatRequestListenerService.class);
        i.putExtra("user", getUser(context));
        context.startService(i);
    }

    public String getUser(Context c) {
        File file = new File(c.getExternalFilesDir(null), "login.dat");
        String r = "";
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            ArrayList<String> lines = new ArrayList<>();
            String line = "";
            while((line = bf.readLine()) != null) {
                lines.add(line);
            }
            r = lines.get(0);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }
}
