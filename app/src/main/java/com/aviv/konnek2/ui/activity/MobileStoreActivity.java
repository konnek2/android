package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
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
import com.aviv.konnek2.utils.Constant;
import com.quickblox.sample.core.utils.Toaster;

public class MobileStoreActivity extends AppCompatActivity {

    GridView gridView;
    MstoreAdapter mstoreAdapter;
    Toolbar toolbar;
    String[] mStoreName;
    TypedArray mStoreImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_store);
        toolbar = (Toolbar) findViewById(R.id.toolbar_mstore);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(Constant.HOME + Constant.GREATER_THAN + Constant.M_STORE);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        gridView = (GridView) findViewById(R.id.mobile_store);

        mStoreImage = getResources().obtainTypedArray(R.array.mstore_image); // String  Values  from resource  files
        mStoreName = getResources().getStringArray(R.array.mstore);          //  Image  Values  from resource  files
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
                        Toaster.longToast(Constant.TOAST_MESSAGE);
                        break;

                    case 3:
                        Toaster.longToast(Constant.TOAST_MESSAGE);
                        break;


                    case 4:
                        Toaster.longToast(Constant.TOAST_MESSAGE);
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
