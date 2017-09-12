package com.pentavalue.yousry.firebasechat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.pentavalue.yousry.firebasechat.fragments.ChatListFragment;
import com.pentavalue.yousry.firebasechat.fragments.ContactFragment;
import com.pentavalue.yousry.firebasechat.fragments.GroupFragment;
import com.pentavalue.yousry.firebasechat.fragments.LoginFragment;
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
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return ContactFragment.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return LoginFragment.newInstance();
            case 2: // Fragment # 1 - This will show SecondFragment
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
