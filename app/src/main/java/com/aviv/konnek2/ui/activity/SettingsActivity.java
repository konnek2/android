package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aviv.konnek2.R;
import com.aviv.konnek2.adapters.RVSettingsAdapter;
import com.aviv.konnek2.adapters.SettingsListAdapter;
import com.aviv.konnek2.utils.Common;
import com.aviv.konnek2.utils.Constant;
import com.quickblox.sample.core.utils.Toaster;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    Toolbar toolbar;
    public String itemName[];
    public TypedArray settingsImage ;

    private CircleImageView circleImageView;
    ImageView setings_imageView;
    public SettingsListAdapter settingsListAdapter;
    private ProgressBar progress;
    CoordinatorLayout parent;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initviews();
    }

    public void initviews() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.settings_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        circleImageView = (CircleImageView) findViewById(R.id.settings_profileImage);
        setings_imageView = (ImageView) findViewById(R.id.img_content_listview);
        parent = (CoordinatorLayout) findViewById(R.id.activity_settingsParent);
        itemName = getResources().getStringArray(R.array.setings_tittle);
        settingsImage = getResources().obtainTypedArray(R.array.setings_image);
        nameList = new ArrayList<String>(Arrays.asList(itemName));
        RecyclerView.Adapter settingsListAdapter = new RVSettingsAdapter(nameList, settingsImage);
        recyclerView.setAdapter(settingsListAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {


                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    listItemPosition(position);
                    if (position == 1) {
                        Toast.makeText(getApplicationContext(), nameList.get(position), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    public void listItemPosition(int position) {

        switch (position) {
            case 0:
                break;
            case 1:
//
                Common.displayToast(Constant.TOAST_MESSAGE);
                break;

            case 2:
                Common.displayToast(Constant.TOAST_MESSAGE);

                break;
            case 3:

                Common.displayToast(Constant.TOAST_MESSAGE);

                break;

            case 4:
//
                Common.displayToast(Constant.TOAST_MESSAGE);
                break;
            default:
                break;

        }
    }
}
