package com.example.kevin.eventapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class Pageadaptert extends FragmentPagerAdapter {

    int nTabs;

    public Pageadaptert(FragmentManager fm, int nooftab) {
        super(fm);
        this.nTabs = nooftab;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                    InvitesTab tab1 = new InvitesTab();
                    return tab1;

            case 1:
                    EventsTab tab2 = new EventsTab();
                    return  tab2;


            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return nTabs;
    }
}
