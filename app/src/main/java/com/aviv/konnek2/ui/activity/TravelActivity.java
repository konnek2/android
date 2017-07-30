package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aviv.konnek2.R;
import com.aviv.konnek2.adapters.TravelAdapter;
import com.quickblox.sample.core.utils.Toaster;

public class TravelActivity extends AppCompatActivity {

    private GridView gridView;
    TravelAdapter travelAdapter;
    Toolbar toolbar;

    String[] travelName = {
            "Flight",
            "Cab",
            "Hotel",
            "Holliday"

    };
    int[] travelImage = {
            R.drawable.ic_app_grid_flight,
            R.drawable.ic_app_grid_cab,
            R.drawable.ic_app_grid_hotel,
            R.drawable.ic_app_grid_holiday

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        toolbar = (Toolbar) findViewById(R.id.toolbar_travel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Home > m-Store > Travel");
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        gridView = (GridView) findViewById(R.id.home_travelgrid);
        travelAdapter = new TravelAdapter(TravelActivity.this, travelName, travelImage);
        gridView.setAdapter(travelAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Toaster.longToast("In progress");
                        break;
                    case 1:
                        Toaster.longToast("In progress");
                        break;
                    case 2:
                        Toaster.longToast("In progress");
                        break;
                    case 3:
                        Toaster.longToast("In progress");
                        break;
                    default:
                        break;

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_travel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_homebutton1:
                Intent goToHome= new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(goToHome);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
