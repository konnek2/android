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
import com.aviv.konnek2.adapters.MstoreAdapter;
import com.quickblox.sample.core.utils.Toaster;

public class MobileStoreActivity extends AppCompatActivity {

    GridView gridView;
    MstoreAdapter mstoreAdapter;
    Toolbar toolbar;
    String[] mStoreName = {
            "Travel",
            "Money",
            "Retail",
            "Grocery",
            "Pharmacy",
            "Movies",
    };

    int[] mStoreImage = {
            R.drawable.ic_app_grid_travel,
            R.drawable.ic_app_grid_topup,
            R.drawable.ic_app_grid_shopping,
            R.drawable.ic_app_grid_grocery,
            R.drawable.ic_app_grid_pharmecy,
            R.drawable.ic_app_grid_movies,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_store);
        toolbar = (Toolbar) findViewById(R.id.toolbar_mstore);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Home > m-Store");
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        gridView = (GridView) findViewById(R.id.mobile_store);
        mstoreAdapter = new MstoreAdapter(MobileStoreActivity.this, mStoreName, mStoreImage);
        gridView.setAdapter(mstoreAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent goToNavigation5 = new Intent(getApplicationContext(), TravelActivity.class);
                        startActivity(goToNavigation5);
                        break;

                    case 1:
                        Intent goToMoney = new Intent(getApplicationContext(), MoneyActivity.class);
                        startActivity(goToMoney);
                        break;

                    case 2:
                        Toaster.longToast("In progress");
                        break;

                    case 3:
                        Toaster.longToast("In progress");
                        break;


                    case 4:
                        Toaster.longToast("In progress");
                        break;

                    case 5:
//                        Intent goToMoney = new Intent(getApplicationContext(), TravelActivity.class);
//                        startActivity(goToMoney);
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
        inflater.inflate(R.menu.menu_mstore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
