package com.aviv.konnek2.customview;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Lenovo on 11/29/2016.
 */

public class NormalFont extends AppCompatTextView {

    public NormalFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode())
            init();
    }

    public NormalFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            init();
    }

    public NormalFont(Context context) {
        super(context);
        if (!isInEditMode())
            init();
    }

    private void init() {
//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"Roboto-Regular.ttf");
//        setTypeface(Typeface.SERIF);
    }

}
