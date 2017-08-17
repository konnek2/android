package com.aviv.konnek2.adapters;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv.konnek2.R;

import java.util.ArrayList;

/**
 * Created by Lenovo on 26-07-2017.
 */

public class RVSettingsAdapter extends RecyclerView.Adapter<RVSettingsAdapter.ViewHolder> {



    private ArrayList<String> itemName;
    private TypedArray itemImage;


    public RVSettingsAdapter(ArrayList<String> itemName,TypedArray itemImage) {
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    @Override
    public RVSettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_settings_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVSettingsAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txtTitle.setText(itemName.get(i));
        viewHolder.imageView.setImageResource(itemImage.getResourceId(i,0));
    }

    @Override
    public int getItemCount() {
        return itemName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private ImageView imageView;
        public ViewHolder(View view) {
            super(view);


            txtTitle = (TextView) view.findViewById(R.id.txt_listValues);
            imageView = (ImageView) view.findViewById(R.id.img_content_listview);
        }
    }
}
