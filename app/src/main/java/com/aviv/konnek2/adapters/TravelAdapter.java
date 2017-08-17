package com.aviv.konnek2.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv.konnek2.R;

/**
 * Created by Lenovo on 29-06-2017.
 */

public class TravelAdapter extends BaseAdapter {

    private Context mContext;
    private final String[] travelName;
    private final TypedArray travelImage;

    public TravelAdapter(Context c, String[] travelName, TypedArray travelImage) {
        mContext = c;
        this.travelName = travelName;
        this.travelImage = travelImage;
    }


    @Override
    public int getCount() {
        return travelName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.item_travel, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_trveltext);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_trvelimage);
            textView.setText(travelName[position]);
            imageView.setImageResource(travelImage.getResourceId(position, 0));
        } else {
            grid = (View) convertView;
        }


        return grid;
    }
}
