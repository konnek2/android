package com.aviv.konnek2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.aviv.konnek2.utils.Constant;
import com.quickblox.sample.chat.ui.fragment.ChatFragment;
import com.quickblox.sample.groupchatwebrtc.fragments.AVCallFragment;
import com.quickblox.sample.groupchatwebrtc.fragments.CallHistoryFragment;
import com.quickblox.sample.groupchatwebrtc.fragments.ContactFragment;

/**
 * Created by Lenovo on 26-06-2017.
 */

public class SwipeViewAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    CharSequence tabViewTitles[] = {Constant.TAB_CALL_HISTORY, Constant.TAB_CHAT, Constant.TAB_CONTACTS};

    public SwipeViewAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:

                return new CallHistoryFragment();
            case 1:

                return new ChatFragment();
            case 2:

                return new ContactFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "" + tabViewTitles[position];
    }

}
