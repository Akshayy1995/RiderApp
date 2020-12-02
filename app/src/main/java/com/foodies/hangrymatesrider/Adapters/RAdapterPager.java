package com.foodies.hangrymatesrider.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.res.Resources;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments.RChatFragment;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments.RJobsFragment;
import com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments.RProfileFragment;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class RAdapterPager  extends FragmentPagerAdapter {
    private final Resources resources;

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    public RAdapterPager(final Resources resources, FragmentManager fm) {
        super(fm);
        this.resources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fm=null;
        switch (position) {

            case 0:
                fm = new RJobsFragment();
                break;
            case 1:
                fm = new RChatFragment();
                break;
            case 2:
                fm = new RProfileFragment();
                break;

        }
        return fm;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(final int position) {

        return null;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    /**
     * Get the Fragment by position
     *
     * @param position tab position of the fragment
     * @return
     */
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

}


