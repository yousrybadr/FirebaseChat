package com.pentavalue.yousry.firebasechat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pentavalue.yousry.firebasechat.fragments.ContactFragment;
import com.pentavalue.yousry.firebasechat.fragments.GroupChatFragment;
import com.pentavalue.yousry.firebasechat.fragments.LoginFragment;
import com.pentavalue.yousry.firebasechat.fragments.RecentChatFragment;

/**
 * Created by yousry on 9/5/2017.
 */

public class ChatPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Chat", "Contacts" };

    public ChatPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return RecentChatFragment.newInstance();
            case 1: // Fragment # 1 - This will show SecondFragment
                return ContactFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //Log.v(ChatPagerAdapter.class.getSimpleName(),"The Page Count is " +PAGE_COUNT);

        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
