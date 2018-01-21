package com.shubh.androidchatter.adapters;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shubh.androidchatter.fragments.ChatsListFragment;
import com.shubh.androidchatter.fragments.FriendsFragment;
import com.shubh.androidchatter.fragments.RequestsFragment;
import com.shubh.androidchatter.fragments.SearchFragment;


/**
 * Created by shubham on 2/20/2017.
 */

public class MypagerAdapter extends FragmentPagerAdapter {
    String[] tabs;
    private Resources resources;
   // int[] icons={R.drawable.ic_bucket_with_plus,R.drawable.ic_002_cart};
    String[] text={"Chats","Friends","Requests"};
    public MypagerAdapter(FragmentManager fm, Resources resources){
        super(fm);
        this.resources=resources;
        tabs=text;
    }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position){
                case 0:
                    fragment= ChatsListFragment.newInstance("","");
                    break;
                case 1:
                    fragment= FriendsFragment.newInstance("","");
                    break;
                case 2:
                    fragment= RequestsFragment.newInstance("","");
                    break;
            }
            return fragment;
        }
    @Override
    public int getItemPosition(Object object) {
        if (object instanceof ChatsListFragment ) {
            ((ChatsListFragment )object).updateMyLogicOrView();
        }
        if (object instanceof FriendsFragment) {
            ((FriendsFragment)object).updateMyLogicOrView();
        }

        if (object instanceof RequestsFragment) {
            ((RequestsFragment)object).updateMyLogicOrView();
        }
        return super.getItemPosition(object);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }
    public String getText(int pos){
        return text[pos];
    }
    @Override
    public int getCount() {
        return 3;
    }
}
