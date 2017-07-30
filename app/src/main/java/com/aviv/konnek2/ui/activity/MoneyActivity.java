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
import com.aviv.konnek2.adapters.MoneyAdapter;
import com.quickblox.sample.core.utils.Toaster;

public class MoneyActivity extends AppCompatActivity {


    private GridView gridView;
    MoneyAdapter moneyAdapter;
    Toolbar toolbar;

    String[] MoneylName = {
            "Money Transfer",
            "Top Up",
            "DTH Recharge",
            "Loans"

    };
    int[] MoneyImage = {
            R.drawable.ic_app_grid_money_transfer,
            R.drawable.ic_app_grid_topup,
            R.drawable.ic_app_grid_dth,
            R.drawable.ic_app_grid_loan,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        toolbar = (Toolbar) findViewById(R.id.toolbar_money);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle("Home > m-Store > Money");
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        gridView = (GridView) findViewById(R.id.money_grid);
        moneyAdapter = new MoneyAdapter(MoneyActivity.this, MoneylName, MoneyImage);
        gridView.setAdapter(moneyAdapter);
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
        inflater.inflate(R.menu.menu_money, menu);
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
