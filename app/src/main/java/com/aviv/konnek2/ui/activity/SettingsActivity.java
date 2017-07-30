package com.aviv.konnek2.ui.activity;

import android.content.Intent;
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
import com.quickblox.sample.core.utils.Toaster;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    Toolbar toolbar;
    public String itemName[];
//    public int imgId[];

    private CircleImageView circleImageView;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView setings_imageView;
    public SettingsListAdapter settingsListAdapter;
    private EditText et_old_password, et_new_password;
    private String old_password, new_password;
    private TextView tv_message, txt_passWordChange, text_passWordCancel;
    private android.app.AlertDialog alertDialog;
    private ProgressBar progress;
    CoordinatorLayout parent;
    ArrayList<String> nameList;
    ArrayList<Integer> imageList;

    public int[] imgId = {

            R.drawable.ic_chat_notification,
            R.drawable.ic_chat_blue,
            R.drawable.ic_share_blue,
            R.drawable.ic_password_blue,
            R.drawable.ic_settings_help

    };


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
        imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.ic_chat_notification);
        imageList.add(R.drawable.ic_chat_blue);
        imageList.add(R.drawable.ic_share_blue);
        imageList.add(R.drawable.ic_password_blue);
        imageList.add(R.drawable.ic_settings_help);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.settings_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        circleImageView = (CircleImageView) findViewById(R.id.settings_profileImage);
        setings_imageView = (ImageView) findViewById(R.id.img_content_listview);
        parent = (CoordinatorLayout) findViewById(R.id.activity_settingsParent);
        itemName = getResources().getStringArray(R.array.setingsActivity);
        nameList = new ArrayList<String>(Arrays.asList(itemName));
        RecyclerView.Adapter settingsListAdapter = new RVSettingsAdapter(nameList, imageList);
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
//                Intent goTOpreferneceActivity = new Intent(getApplicationContext(), preferencesActivity.class);
//                startActivity(goTOpreferneceActivity);
                Common.displayToast("Change Password in Progress ");
                break;

            case 2:
                // Inviate Friend
//                shareTextUrl();
                Common.displayToast("Change Password in Progress ");

                break;
            case 3:

                Common.displayToast("Change Password in Progress ");

                break;

            case 4:
//                Intent goToHelpActivity = new Intent(getApplicationContext(), HelpActivity.class);
//                startActivity(goToHelpActivity);
                Common.displayToast("Change Password in Progress ");
                break;
            default:
                break;

        }
    }
}
