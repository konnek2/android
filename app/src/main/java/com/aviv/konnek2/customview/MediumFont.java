package com.aviv.konnek2.customview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Lenovo on 11/29/2016.
 */

public class MediumFont extends AppCompatTextView {


    public MediumFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            init();
    }

    public MediumFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            init();
    }

    public MediumFont(Context context) {
        super(context);
        if (!isInEditMode())
            init();
    }

    private void init() {
//        setTypeface(Typeface.SANS_SERIF);
        setTypeface(Typeface.DEFAULT_BOLD);
    }
}
