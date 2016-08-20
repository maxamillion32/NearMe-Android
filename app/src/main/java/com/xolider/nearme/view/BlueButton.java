package com.xolider.nearme.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Clément on 20/08/2016.
 */
public class BlueButton extends Button {

    public BlueButton(Context c) {
        super(c);
        init();
    }

    public BlueButton(Context c, AttributeSet attr) {
        super(c, attr);
        init();
    }

    public BlueButton(Context c, AttributeSet attr, int defStyle) {
        super(c, attr, defStyle);
        init();
    }

    public void init() {
        if(Build.VERSION.SDK_INT < 21) {
            this.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        }
        else {
            this.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_blue_dark)));
        }
    }
}
