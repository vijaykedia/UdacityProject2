package com.kediavijay.popularmovies2.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijaykedia on 15/04/16.
 * This will act as adapter class to set up tab view while navigating to detail view
 */
public class MovieDetailAdapter extends FragmentPagerAdapter {

    private final List<String> titles = new ArrayList<>();
    private final List<Fragment> fragments = new ArrayList<>();

    /**
     * Constructor
     *
     * @param fragmentManager manager to manage the fragments in tab view
     */
    public MovieDetailAdapter(@NonNull final FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragment(@NonNull final String title, @NonNull final Fragment fragment) {
        titles.add(title);
        fragments.add(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(final int position) {
        return fragments.get(position);
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(final int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
