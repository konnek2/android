package com.aviv.konnek2.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aviv.konnek2.R;
import com.aviv.konnek2.adapters.HomeAdapter;
import com.aviv.konnek2.data.preference.AppPreference;
import com.aviv.konnek2.utils.Constant;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.sample.chat.utils.Common;
import com.quickblox.sample.chat.utils.chat.ChatHelper;
import com.quickblox.sample.chat.utils.qb.QbDialogHolder;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    GridView gridView;
    Toolbar toolbar;
    public String[] title;
    String[] subtitle = new String[10];
    HomeAdapter homeAdapter;
    private DrawerLayout drawerLayout;
    ImageView profileImage;
    TypedArray imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        initViews();
        imageId = getResources().obtainTypedArray(R.array.home_image); // String  Values  from resource  files
        title = getResources().getStringArray(R.array.home_title);          //  Image  Values  from resource  files
        subtitle[6] = Constant.SUB_TITLE_ONE;
        subtitle[2] = Constant.SUB_TITLE_TWO;
        gridView = (GridView) findViewById(R.id.home_rid);

        homeAdapter = new HomeAdapter(HomeActivity.this, title, subtitle, imageId);
        gridView.setAdapter(homeAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0:
                        Intent goToContact = new Intent(getApplicationContext(), CallingTabActivity.class);
                        goToContact.putExtra(Constant.TAB_POSITION, 2);
                        startActivity(goToContact);
                        break;
                    case 1:
                        Intent goToNavigation = new Intent(getApplicationContext(), CallingTabActivity.class);
                        goToNavigation.putExtra(Constant.TAB_POSITION, 0);
                        startActivity(goToNavigation);
//                        overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);

                        break;
                    case 2:
                        Intent goTochat = new Intent(getApplicationContext(), CallingTabActivity.class);
                        goTochat.putExtra(Constant.TAB_POSITION, 1);
                        startActivity(goTochat);
                        break;
                    case 3:
                        Toaster.shortToast(Constant.TOAST_MESSAGE);
                        break;
                    case 4:
                        Intent goToRefer = new Intent(getApplicationContext(), ReferFriendActivity.class);
                        startActivity(goToRefer);
                        break;
                    case 5:
                        Intent goToSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(goToSettings);
                        break;
                    case 6:
                        Intent goToChatBot = new Intent(getApplicationContext(), ChatBotActivity.class);
                        startActivity(goToChatBot);
                        break;
                    case 7:
                        Intent goToCharity = new Intent(getApplicationContext(), CharityActivity.class);
                        startActivity(goToCharity);
                        break;
                    case 8:
                        Intent goTomStore = new Intent(getApplicationContext(), MobileStoreActivity.class);
                        startActivity(goTomStore);
                        break;

                    case 9:
                        Intent goToHangouts = new Intent(getApplicationContext(), HangoutActivity.class);
                        startActivity(goToHangouts);
                        break;
                    case 10:
                        Intent goToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(goToProfile);
                        break;
                    default:
                        break;
                }

            }
        });
    }

    public void initViews() {
        getSupportActionBar().setSubtitle(Constant.HOME);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_app_navigation_drawer_icon);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView) header.findViewById(R.id.tv_email_navigation);
        profileImage = (ImageView) header.findViewById(R.id.navigation_profile_image);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        tv_email.setText(AppPreference.getUserName());
        setProfileImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logouts:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        try {
            AppPreference.signInEditorMhd().remove(Constant.USER_NAME);
            AppPreference.putLoginStatus(false);
            AppPreference.signInEditorMhd().commit();
            // Textchat LogOut
            ChatHelper.getInstance().destroy();
            SubscribeService.unSubscribeFromPushes(HomeActivity.this);
            SharedPrefsHelper.getInstance().removeQbUser();
            QbDialogHolder.getInstance().clear();
            goToLogin();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void goToLogin() {
        Intent goToSignIn = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(goToSignIn);
        finish();
    }

    private void setProfileImage() {
        String profileImagePath = AppPreference.getProfileImagePath();

        if (getCheckFile(profileImagePath) && profileImagePath != null && AppPreference.getProfileImagePath() != null) {
            Uri userImage = Uri.parse(profileImagePath);
            profileImage.setImageURI(userImage);
        } else {
            profileImage.setImageResource(R.drawable.default_image);

        }

    }

    private boolean getCheckFile(String profileImagePath) {
        File file;
        if (profileImagePath == null)
            return false;
        else {
            file = new File(profileImagePath);
            return file.exists();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                drawerLayout.closeDrawers();
                break;

            case R.id.profile:
                Intent goToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(goToProfile);
                drawerLayout.closeDrawers();
                break;

            case R.id.nave_settings:
                Common.displayToast(Constant.TOAST_MESSAGE);
                drawerLayout.closeDrawers();
                break;

            case R.id.logout:
                logout();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
