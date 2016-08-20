package com.xolider.nearme.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xolider.nearme.R;

/**
 * Created by Clément on 20/08/2016.
 */
public class TextViewWithDrawable extends TextView {

    public TextViewWithDrawable(Context c) {
        super(c);
        init();
    }

    public TextViewWithDrawable(Context c, AttributeSet attr) {
        super(c, attr);
        init();
    }

    public TextViewWithDrawable(Context c, AttributeSet attr, int def) {
        super(c, attr, def);
        init();
    }

    public void init() {
        this.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check_green_24dp, 0, 0, 0);
    }
}
