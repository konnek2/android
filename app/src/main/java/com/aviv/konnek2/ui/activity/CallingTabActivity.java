package com.aviv.konnek2.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.aviv.konnek2.R;
import com.aviv.konnek2.adapters.SwipeViewAdapter;
import com.aviv.konnek2.utils.Constant;
import com.quickblox.sample.chat.ui.fragment.ChatFragment;
import com.quickblox.sample.groupchatwebrtc.fragments.CallHistoryFragment;
import com.quickblox.sample.groupchatwebrtc.fragments.ContactFragment;

public class CallingTabActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager TabViewPager;
    TabLayout tabLayout;
    SwipeViewAdapter swipeViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_tab);
        toolbar = (Toolbar) findViewById(R.id.toolbar_tab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(Constant.HOME + Constant.GREATER_THAN + Constant.CONNECT);
        toolbar.setNavigationIcon(R.drawable.ic_app_navigation_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        initViews();

    }

    private void initViews() {

        TabViewPager = (ViewPager) findViewById(R.id.viewPager_tab);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_tab);
        swipeViewer();

        if (getIntent() != null && getIntent().getIntExtra(Constant.TAB_POSITION, 0) != 4) {
            int pos = getIntent().getIntExtra(Constant.TAB_POSITION, 0);
            TabViewPager.setCurrentItem(pos);
        }
    }

    public void swipeViewer() {

        try {
            tabLayout.addTab(tabLayout.newTab().setText(Constant.TAB_ONE));
            tabLayout.addTab(tabLayout.newTab().setText(Constant.TAB_TWO));
            tabLayout.addTab(tabLayout.newTab().setText(Constant.TAB_THREE));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            swipeViewAdapter = new SwipeViewAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            TabViewPager.setAdapter(swipeViewAdapter);
            tabLayout.setTabsFromPagerAdapter(swipeViewAdapter);
            tabLayout.setupWithViewPager(TabViewPager);
            tabLayout.setVisibility(View.VISIBLE);
            TabViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    TabViewPager.setCurrentItem(tab.getPosition());
                    if (tab.getPosition() == 0)
                        CallHistoryFragment.callHistoryTrigger();
                    else if (tab.getPosition() == 1)
                        ChatFragment.chatTrigger();
                    else
                        ContactFragment.contactsTrigger();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });


        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
