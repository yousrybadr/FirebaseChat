package com.pentavalue.yousry.firebasechat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pentavalue.yousry.firebasechat.fragments.ChatListFragment;
import com.pentavalue.yousry.firebasechat.fragments.ContactFragment;
import com.pentavalue.yousry.firebasechat.fragments.GroupFragment;
import com.pentavalue.yousry.firebasechat.models.Contact;

/**
 * Created by yousry on 9/5/2017.
 */

public class ChatPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Chat", "Groups", "Contacts" };

    public ChatPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ContactFragment.newInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
