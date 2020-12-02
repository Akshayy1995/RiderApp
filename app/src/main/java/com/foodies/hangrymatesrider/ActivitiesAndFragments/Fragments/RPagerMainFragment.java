package com.foodies.hangrymatesrider.ActivitiesAndFragments.Fragments;

import android.content.Context;
import android.os.Bundle;

import com.foodies.hangrymatesrider.ActivitiesAndFragments.Activities.RiderMainActivity;
import com.foodies.hangrymatesrider.Adapters.RAdapterPager;
import com.foodies.hangrymatesrider.Constants.Functions;
import com.foodies.hangrymatesrider.Utils.CustomViewPager;
import com.foodies.hangrymatesrider.Utils.RelateToFragment_OnBack.OnBackPressListener;
import com.foodies.hangrymatesrider.Utils.RelateToFragment_OnBack.RootFragment;
import com.google.android.material.tabs.TabLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodies.hangrymatesrider.R;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */

public class RPagerMainFragment extends RootFragment {
    public TabLayout tabLayout;
    public static CustomViewPager viewPager;
    RAdapterPager adapter;

    View v;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_pager, container, false);
        context=getContext();


        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout);
        viewPager =  v.findViewById(R.id.pager);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RiderMainActivity.width,  RiderMainActivity.height- Functions.dpToPx(getActivity(),72));
        viewPager.setLayoutParams(params);

        adapter = new RAdapterPager(getResources(), getChildFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
        return v;
    }

    private void setupTabIcons() {
        View view1 = LayoutInflater.from(context).inflate(R.layout.item_menu_tablayout_item, null);
        ImageView imageView1= view1.findViewById(R.id.image);
        imageView1.setImageDrawable(getResources().getDrawable(R.drawable.h_job_filled));
        TextView textView1=view1.findViewById(R.id.text);
        textView1.setText(R.string.jobs);
        textView1.setTextColor(getResources().getColor(R.color.golden));
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_menu_tablayout_item, null);
        ImageView imageView2= view2.findViewById(R.id.image);
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_not_fill));
        TextView textView=view2.findViewById(R.id.text);
        textView.setText(R.string.chat);
        textView.setTextColor(getResources().getColor(R.color.dark_gray));
        tabLayout.getTabAt(1).setCustomView(view2);


        View view3 = LayoutInflater.from(context).inflate(R.layout.item_menu_tablayout_item, null);
        ImageView imageView3= view3.findViewById(R.id.image);
        imageView3.setImageDrawable(getResources().getDrawable(R.drawable.h_profile_not_filled));
        TextView textView3=view3.findViewById(R.id.text);
        textView3.setText(R.string.profile);
        textView3.setTextColor(getResources().getColor(R.color.dark_gray));
        tabLayout.getTabAt(2).setCustomView(view3);




        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View v=tab.getCustomView();
                ImageView image=v.findViewById(R.id.image);
                switch (tab.getPosition()){
                    case 0:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.h_job_filled));
                        break;
                    case 1:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_filled));
                        break;
                    case 2:

                        image.setImageDrawable(getResources().getDrawable(R.drawable.h_profile_filled));
                        break;

                }
                TextView tv =v.findViewById(R.id.text);
                tv.setTextColor(getResources().getColor(R.color.golden));
                tab.setCustomView(v);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v=tab.getCustomView();
                ImageView image=v.findViewById(R.id.image);
                switch (tab.getPosition()){
                    case 0:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.h_job_not_filled));
                        break;
                    case 1:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_not_fill));
                        break;
                    case 2:
                        image.setImageDrawable(getResources().getDrawable(R.drawable.h_profile_not_filled));
                        break;

                }
                TextView tv =v.findViewById(R.id.text);
                tv.setTextColor(getResources().getColor(R.color.colorWhite));
                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }



    void selectPage(int pageIndex) {
        viewPager.setCurrentItem(pageIndex);

    }


    @Override
    public void onResume() {
        super.onResume();

        if(RiderMainActivity.CHAT_FLAG) {
           selectPage(1);
            RiderMainActivity.CHAT_FLAG = false;
        }

    }


    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(viewPager.getCurrentItem());

        if (currentFragment != null) {
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }





}
