package net.hailm.firebaseapp.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.hailm.firebaseapp.view.fragments.HouseFragment;
import net.hailm.firebaseapp.view.fragments.PlaceFragment;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class HomeVpgAdapter extends FragmentStatePagerAdapter {

    public HomeVpgAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HouseFragment();
                break;
            case 1:
                fragment = new PlaceFragment();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
