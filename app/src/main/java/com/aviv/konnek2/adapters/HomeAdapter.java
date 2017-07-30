package com.aviv.konnek2.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv.konnek2.R;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class HomeAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] title;
    private final String[] subtitle;
    private final int[] Imageid;

    public HomeAdapter(Context c, String[] title, String[] subtitle, int[] Imageid) {
        mContext = c;
        this.Imageid = Imageid;
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public int getCount() {
        return title.length;
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
            grid = inflater.inflate(R.layout.item_home_grid, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            TextView textView2 = (TextView) grid.findViewById(R.id.grid_text2);
            TextView textView3 = (TextView) grid.findViewById(R.id.grid_text3);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);


            textView.setText(title[position]);
            if (position == 2 || position == 6) {
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(subtitle[2]);

            }
            if (position == 6) {
                Log.d("GRID", "position  IFFF:::  subtitle[6] " + subtitle[6]);
                textView2.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView2.setText(subtitle[6]);
            }

//            textView2.setText(subtitle[5]);
//            textView2.setText(subtitle[6]);
            imageView.setImageResource(Imageid[position]);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
