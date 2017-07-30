package com.aviv.konnek2.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv.konnek2.R;

/**
 * Created by Lenovo on 26-07-2017.
 */

public class SettingsListAdapter extends ArrayAdapter<String> {

    public   String[] itemName;
    public  int[] imgId;
    public Activity context;

    public SettingsListAdapter(Activity context,int[] imgId,String[] itemName) {
        super(context, R.layout.item_settings_view, itemName);
        this.context = context;
        this.itemName = itemName;
        this.imgId = imgId;

    }


    public View getView(int position, View view, ViewGroup parent) {
//        Log.d("SettingsListAdapter","  getView");
        Holder holder = new Holder();
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_settings_view, null, true);
        holder.txtTitle = (TextView) rowView.findViewById(R.id.txt_listValues);
        holder.imageView = (ImageView) rowView.findViewById(R.id.img_content_listview);
        holder.txtTitle.setText(itemName[position]);
        holder.imageView.setImageResource(imgId[position]);
        return rowView;

    }

    public class Holder {
        public   TextView txtTitle;
        public   ImageView imageView;

    }
}
