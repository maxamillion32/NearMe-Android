package com.xolider.nearme.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Clément on 20/08/2016.
 */
public class User {

    private String username;
    private String img;

    public User(String username, String imageProfile) {
        this.username = username;
        this.img = imageProfile;
    }

    public String getUsername() {
        return username;
    }

    public String getImageProfile() throws MalformedURLException {
        return img;
    }
}
